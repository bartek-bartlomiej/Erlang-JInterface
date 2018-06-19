package com.bsps;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.ericsson.otp.erlang.OtpErlangExit;

import javax.management.BadAttributeValueExpException;
import java.io.IOException;
import java.util.Scanner;

public class Game {
    private Player player;
    private Player opponent;
    private ErlangNode erlangNode;

    boolean isEnded;

    public Game(String playerName, String opponentName) throws IOException {
        erlangNode = new ErlangNode(playerName, opponentName);

        player = new Player("Player " + playerName);
        opponent = new Player("Opponent " + opponentName);

        isEnded = false;
    }

    public void start() throws OtpErlangExit, ErlangNode.SetUpConnectionFailException, OtpErlangDecodeException {

        System.out.println("Welcome to Rock-Paper-Scissors Game!");

        System.out.println("Waiting for " + opponent.getName() + "...");
        erlangNode.connectWithOpponent();

        startRound();
    }

    private void startRound() {
        player.setCurrentChoice(null);
        opponent.setCurrentChoice(null);

        System.out.println("New round started!");

        Thread opponentThread = new Thread(() -> {
            Choice opponentChoice = receiveCurrentChoiceFromOpponent();

            if (opponentChoice == null && isEnded) finishGame();
            else {
                opponent.setCurrentChoice(opponentChoice);

                System.out.println("Opponent has already chosen!");

                if (player.hasChosen()) finishRound();
                else System.out.println("Waiting for player choice...");
            }
        });

        Thread playerThread = new Thread(() -> {

            Choice playerChoice;
            playerChoice = getChoiceFromPlayer();

            if (playerChoice == null && isEnded) finishGame();
            else {
                player.setCurrentChoice(playerChoice);

                sendCurrentChoiceToOpponent();

                System.out.println("You have chosen: " + player.getCurrentChoice());

                if (opponent.hasChosen()) finishRound();
                else System.out.println("Waiting for opponent choice...");
            }
        });

        opponentThread.start();
        playerThread.start();
    }

    private void finishRound() {
        Player winner = null;

        Choice playerChoice   = player.getCurrentChoice();
        Choice opponentChoice = opponent.getCurrentChoice();

        System.out.println(playerChoice.toString() + " VS " + opponentChoice.toString());

        if ((playerChoice == Choice.PAPER    && opponentChoice == Choice.ROCK) ||
            (playerChoice == Choice.ROCK     && opponentChoice == Choice.SCISSORS) ||
            (playerChoice == Choice.SCISSORS && opponentChoice == Choice.PAPER)) winner = player;

        else if ((playerChoice == Choice.PAPER    && opponentChoice == Choice.SCISSORS) ||
                 (playerChoice == Choice.ROCK     && opponentChoice == Choice.PAPER) ||
                 (playerChoice == Choice.SCISSORS && opponentChoice == Choice.ROCK)) winner = opponent;

        if (winner == null) {
            System.out.println("Draw!");
        }
        else {
            System.out.println(winner.getName() + " won round!");
            winner.incrementScore();
        }

        System.out.println("Current score: " + player.getScore() + ":" + opponent.getScore());

        startRound();
    }

    private void finishGame() {
        System.out.println("Game finished!");

        System.out.println("Score: " + player.getScore() + ":" + opponent.getScore());

        Player winner = null;
        if (player.getScore() > opponent.getScore()) winner = player;
        else if (player.getScore() < opponent.getScore()) winner = opponent;

        if (winner == null) {
            System.out.println("Game finished with draw!");
        }
        else {
            System.out.println(winner.getName() + " won game!");
        }

        System.exit(0);
    }

    // TODO: inna klasa
    private Choice getChoiceFromPlayer() {
        System.out.println("What would you choose?");
        System.out.println("Quit game (0)");
        for (Choice choice : Choice.values()) {
            System.out.println(choice.getName() + " (" + choice.getIndex() + ")");
        }

        Choice choice = null;
        Scanner stdin = new Scanner(System.in);

        while (choice == null && !isEnded) {
            System.out.print("(1-3): ");

            int i = stdin.nextInt();
            System.err.println(i);

            if (i == 0) isEnded = true;
            else {
                try {
                    choice = Choice.fromIndex(i);
                } catch (BadAttributeValueExpException e) {
                    System.out.println("Sorry, choose one again!");
                }
            }
        }

        return choice;
    }

    private void sendCurrentChoiceToOpponent() {
        erlangNode.sendToOpponent(Choice.toAtom(player.getCurrentChoice()));
    }

    private Choice receiveCurrentChoiceFromOpponent()  {
        Choice choice = null;

        try {
            choice = Choice.fromAtom((OtpErlangAtom) erlangNode.getFromOpponent());
        } catch (BadAttributeValueExpException | OtpErlangDecodeException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (OtpErlangExit otpErlangExit) {
            System.out.println(opponent.getName() + " has left the game!");
            isEnded = true;
        }

        return choice;
    }
}
