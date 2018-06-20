package com.bsps;

import java.util.Scanner;

public class ConsoleCommunication implements IChoiceReceiver {

    @Override
    public Choice receiveChoice() {

        System.out.println("What would you choose?");
        for (Choice choice : Choice.values()) {
            System.out.println("- " + choice.getName() + " (" + choice.getIndex() + ")");
        }

        Choice choice = null;
        Scanner stdin = new Scanner(System.in);

        while (choice == null) {
            System.out.print("(1-3)>");

            int i = stdin.nextInt();
            choice = Choice.findByIndex(i);
        }

        return choice;
    }
}
