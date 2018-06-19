package com.bsps;

import com.ericsson.otp.erlang.OtpErlangAtom;

import javax.management.BadAttributeValueExpException;

public enum Choice {
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

    public static OtpErlangAtom toAtom(Choice choice) {
        return new OtpErlangAtom(choice.name);
    }
    public static Choice fromAtom(OtpErlangAtom atom) throws BadAttributeValueExpException {
        Choice choice;

        switch (atom.toString()) {
            case "paper":    choice = PAPER;    break;
            case "scissors": choice = SCISSORS; break;
            case "rock" :    choice = ROCK;     break;
            default: throw new BadAttributeValueExpException(atom);
        }
        return choice;
    }
    public static Choice fromIndex(int i) throws BadAttributeValueExpException {
        Choice choice;

        switch (i) {
            case 1: choice = ROCK;     break;
            case 2: choice = PAPER;    break;
            case 3: choice = SCISSORS; break;
            default: throw new BadAttributeValueExpException(i);
        }
        return choice;
    }
}