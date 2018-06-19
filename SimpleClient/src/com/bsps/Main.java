package com.bsps;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        System.out.println("Client started...");

        try {
            SimpleClient client = new SimpleClient();
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
