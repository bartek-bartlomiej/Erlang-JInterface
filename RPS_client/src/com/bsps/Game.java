package com.bsps;

import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.ericsson.otp.erlang.OtpErlangExit;

import java.io.IOException;

public class Game {

    private ErlangConnection connection;
    private IChoiceReceiver playerChoiceReceiver;
    private IChoiceReceiver opponentChoiceReceiver;
    private IChoiceSender opponentChoiceSender;

    private Player player;
    private Player opponent;

    public enum State {
        NEXT_ROUND,
        FINISH
    }

    public Game(String playerName, String opponentName) {
        connection = new ErlangConnection(playerName, opponentName);

        player = new Player("Player " + playerName);
        opponent = new Player("Opponent " + opponentName);

        playerChoiceReceiver = new ConsoleCommunication();

        ErlangComunication ec = new ErlangComunication(connection);
        opponentChoiceReceiver = ec;
        opponentChoiceSender = ec;
    }

    public void start() throws IOException, OtpErlangExit, OtpErlangDecodeException {

        System.out.println("Welcome to Rock-Paper-Scissors Game!");
        System.out.println("Waiting for " + opponent.getName() + "...");

        connection.setUp();

        update(State.NEXT_ROUND);
    }

    public void update(State state) {
        switch (state) {
            case NEXT_ROUND:
                    new Round(this, player, opponent).start();
                break;
            case FINISH:
                    finish();
                break;
        }
    }

    private void finish() {
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

    public Choice receiveChoiceFromPlayer() {
        return playerChoiceReceiver.receiveChoice();
    }

    public Choice receiveChoiceFromOpponent() {
        return opponentChoiceReceiver.receiveChoice();
    }

    public void sendChoiceToOpponent(Choice choice) {
        opponentChoiceSender.sendChoice(choice);
    }
}