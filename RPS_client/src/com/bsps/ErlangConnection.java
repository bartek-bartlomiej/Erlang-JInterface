package com.bsps;

import com.ericsson.otp.erlang.*;

import java.io.IOException;

public class ErlangConnection {

    private OtpMbox mBox;
    final private String playerNodeName;
    final private String opponentNodeName;
    final private String mBoxName = "rpsMailbox";
    final private String cookie = "rps_game";

    private class ConnectionSetUpException extends IOException {}

    public ErlangConnection(String playerNodeName, String opponentNodeName) {
        this.playerNodeName = playerNodeName;
        this.opponentNodeName = opponentNodeName;
    }

    public void setUp() throws IOException, OtpErlangExit, OtpErlangDecodeException {

        OtpNode node = new OtpNode(playerNodeName, cookie);
        mBox = node.createMbox(mBoxName);

        int triesAmount = 0;
        final int maxTriesAmount = 5;
        final long timeout = 5000;

        while (triesAmount < maxTriesAmount && !mBox.ping(opponentNodeName, timeout)) {
            //System.err.println("pang, attempt no. " + (triesAmount + 1));
            triesAmount++;
        }
        if (triesAmount >= maxTriesAmount) throw new ConnectionSetUpException();

        mBox.send(mBoxName, opponentNodeName, mBox.self());

        OtpErlangObject pid = mBox.receive();

        if (pid instanceof OtpErlangPid) {
            mBox.link((OtpErlangPid) pid);
        }
        else throw new ConnectionSetUpException();
    }

    public OtpErlangObject receive() throws OtpErlangExit, OtpErlangDecodeException {
        return mBox.receive();
    }

    public void send(OtpErlangObject msg) {
        mBox.send(mBoxName, opponentNodeName, msg);
    }
}
