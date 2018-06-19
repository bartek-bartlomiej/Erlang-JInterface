package com.bsps;

public class Player {
    final private String name;
    private Choice currentChoice;
    private int score;

    public Player(String name) {
        this.name = name;
        currentChoice = null;
        score = 0;
    }

    public String getName() {
        return name;
    }

    public boolean hasChosen() {
        return currentChoice != null;
    }

    public Choice getCurrentChoice() {
        return currentChoice;
    }

    public void setCurrentChoice(Choice currentChoice) {
        this.currentChoice = currentChoice;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        score++;
    }
}
