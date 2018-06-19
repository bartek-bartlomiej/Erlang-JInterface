package com.bsps;

import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.ericsson.otp.erlang.OtpErlangExit;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        System.out.println("App started...");

        try {
            Game game = new Game(args[0], args[1]);
            game.start();
        } catch (IOException | OtpErlangDecodeException | OtpErlangExit e) {
            e.printStackTrace();
        }

    }
}
