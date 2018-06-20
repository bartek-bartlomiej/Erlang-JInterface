package com.bsps;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.ericsson.otp.erlang.OtpErlangExit;

public class ErlangComunication implements IChoiceReceiver, IChoiceSender {

    private ErlangConnection connection;

    public ErlangComunication(ErlangConnection connection) {
        this.connection = connection;
    }

    @Override
    public Choice receiveChoice() {
        Choice choice = null;

        try {
            OtpErlangAtom msg = (OtpErlangAtom) connection.receive();
            choice = Choice.findByName(msg.atomValue());
        } catch (OtpErlangDecodeException e) {
            System.exit(1);
        } catch (OtpErlangExit otpErlangExit) {
            choice = Choice.QUIT;
        }

        return choice;
    }

    @Override
    public void sendChoice(Choice choice) {
        connection.send(new OtpErlangAtom(choice.getName()));
    }
}
