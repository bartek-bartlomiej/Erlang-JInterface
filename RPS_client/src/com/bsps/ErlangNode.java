package com.bsps;

import com.ericsson.otp.erlang.*;

import java.io.IOException;

public class ErlangNode {

    private OtpMbox mBox;
    final private String mBoxName = "rpsMailbox";

    private String opponentNodeName;

    public class SetUpConnectionFailException extends IOException {}

    public ErlangNode(String playerNodeName, String opponentNodeName) throws IOException {


        this.opponentNodeName = opponentNodeName;
        final String cookie = "rps_game";

        OtpNode node = new OtpNode(playerNodeName, cookie);
        mBox = node.createMbox(mBoxName);
    }

    public void connectWithOpponent() throws SetUpConnectionFailException, OtpErlangExit, OtpErlangDecodeException {

        int triesAmount = 0;
        final int maxTriesAmount = 5;
        final long timeout = 5000;

        while (triesAmount < maxTriesAmount && !mBox.ping(opponentNodeName, timeout)) {
            System.err.println(opponentNodeName + " pang, attempt no. " + (triesAmount+1));
            triesAmount++;
        }
        if (triesAmount >= maxTriesAmount) throw new SetUpConnectionFailException();

        System.err.println(opponentNodeName + " pong");

        mBox.send(mBoxName, opponentNodeName, mBox.self());

        OtpErlangObject pid = mBox.receive();

        if (pid instanceof OtpErlangPid) {
            mBox.link((OtpErlangPid) pid);

            System.err.println("Linked with " + pid.toString());
        }
        else throw new SetUpConnectionFailException();
    }

    public OtpErlangObject getFromOpponent() throws OtpErlangExit, OtpErlangDecodeException {
        return mBox.receive();
    }

    public void sendToOpponent(OtpErlangObject msg) {
        mBox.send(mBoxName, opponentNodeName, msg);
    }
}
