package com.bsps;

import java.util.HashMap;
import java.util.Map;

public enum Choice {
    QUIT("Give up", 0),
    ROCK("rock", 1),
    PAPER("paper", 2),
    SCISSORS("scissors", 3);

    private final String name;
    private final int index;

    Choice(final String name, int index) { this.name = name; this.index = index; }

    public String getName() {
        return name;
    }
    public int getIndex() {
        return index;
    }

    private static final Map<Integer,Choice> mapIndex;
    private static final Map<String, Choice> mapName;
    static {
        mapIndex = new HashMap<>();
        mapName = new HashMap<>();
        for (Choice choice: Choice.values()) {
            mapIndex.put(choice.index, choice);
            mapName.put(choice.name, choice);
        }
    }

    public static Choice findByIndex(int i) {
        return mapIndex.get(i);
    }

    public static Choice findByName(String name) {
        return mapName.get(name);
    }
}