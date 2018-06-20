package com.bsps;

import java.util.HashMap;
import java.util.Map;

public class Round {

    private Game game;
    private Player player;
    private Player opponent;
    private Map<Player, Choice> choices;

    public Round(Game game, Player player, Player opponent) {
        choices = new HashMap<>();
        choices.put(player, null);
        choices.put(opponent, null);

        this.player = player;
        this.opponent = opponent;
        this.game = game;
    }

    private boolean hasPlayerChosen(Player player) {
        return choices.get(player) != null;
    }

//    private Choice getChoice(Player player) {
//        return choices.get(player);
//    }

    private void setChoice(Player player, Choice choice) {
        choices.put(player, choice);
    }

    public void start() {
        System.out.println("New round started!");

        new Thread(() -> {
            Choice opponentChoice = game.receiveChoiceFromOpponent();
            update(opponent, opponentChoice);
        }).start();

        new Thread(() -> {
            Choice playerChoice = game.receiveChoiceFromPlayer();
            update(player, playerChoice);
        }).start();
    }

    private synchronized void update(Player updater, Choice choice) {

        if (choice == Choice.QUIT) {
            System.out.println(updater.getName() + " has left the game!");
            game.update(Game.State.FINISH);
        }
        else {
            if (updater.equals(player)) {
                System.out.println("You have chosen: " + choice.getName());
            }
            //else if (updater.equals(opponent)) {
            //  Opponent have chosen... ;)
            //}

            setChoice(updater, choice);

            if (updater.equals(player)) {
                game.sendChoiceToOpponent(choice);
            }
            else if (updater.equals(opponent)) {
                System.out.println("Opponent has already chosen!");
            }

            if (hasPlayerChosen(player) && hasPlayerChosen(opponent)) {
                finish();
                game.update(Game.State.NEXT_ROUND);
            }
            else {
                if (updater.equals(player)) System.out.println("Waiting for opponent choice...");
                else if (updater.equals(opponent)) System.out.println("Waiting for player choice...");
            }
        }
    }

    private void finish() {

        Choice playerChoice = choices.get(player);
        Choice opponentChoice = choices.get(opponent);

        System.out.println(playerChoice.toString() + " VS " + opponentChoice.toString());

        Player winner = null;

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
        System.out.println();
    }
}