package com.bsps;

import com.ericsson.otp.erlang.*;

import java.io.IOException;
import java.util.*;

public class SimpleClient {

    private OtpMbox otpMbox;
    private final String clientNodeName = "simpleClient";
    private final String clientMailboxName = "sClientMailbox";
    private final String serverNodeName = "simpleServer";
    private final String serverMailboxName = "sServerMailbox";
    private final String cookie = "simple";

    private interface Describer {
        void describe(OtpErlangObject object);
    }

    private enum RequestType {
        NUMBER("number"),
        FRACTION("fraction"),
        ATOM("atom"),
        TUPLE("tuple"),
        LIST("list"),
        MAP("map"),
        PID("pid");

        private final String name;

        RequestType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private Map<Class, String> erlangObjectName;
    private Map<Class, Describer> erlangObjectDescribeAction;

    public SimpleClient() {
        erlangObjectName = new HashMap<>();
        erlangObjectDescribeAction = new HashMap<>();

        erlangObjectName.put(OtpErlangLong.class, "integral type");
        erlangObjectDescribeAction.put(OtpErlangLong.class,
                (OtpErlangObject o) -> {

                    OtpErlangLong eLong = (OtpErlangLong) o;
                    System.out.println("Value: " + eLong.longValue());
                });

        erlangObjectName.put(OtpErlangDouble.class, "floating point type");
        erlangObjectDescribeAction.put(OtpErlangDouble.class,
                (OtpErlangObject o) -> {

                    OtpErlangDouble eDouble = (OtpErlangDouble) o;
                    System.out.println("Value: " + eDouble.doubleValue());
                });

        erlangObjectName.put(OtpErlangAtom.class, "atom");
        erlangObjectDescribeAction.put(OtpErlangAtom.class,
                (OtpErlangObject o) -> {

                    OtpErlangAtom atom = (OtpErlangAtom) o;
                    System.out.println("Atom name: " + atom.toString());
                });

        erlangObjectName.put(OtpErlangTuple.class, "tuple");
        erlangObjectDescribeAction.put(OtpErlangTuple.class,
                (OtpErlangObject o) -> {

                    OtpErlangTuple tuple = (OtpErlangTuple) o;
                    System.out.print("Tuple: {");
                    int i = 0;
                    while (i < tuple.arity() - 1) {
                        System.out.print(tuple.elementAt(i).toString());
                        System.out.print(", ");
                        i++;
                    }
                    System.out.print(tuple.elementAt(i));
                    System.out.println("}");
                });

        erlangObjectName.put(OtpErlangList.class, "list");
        erlangObjectDescribeAction.put(OtpErlangList.class,
                (OtpErlangObject o) -> {

                    OtpErlangList list = (OtpErlangList) o;
                    System.out.print("List: [");
                    int i = 0;
                    while (i < list.arity() - 1) {
                        System.out.print(list.elementAt(i).toString());
                        System.out.print(", ");
                        i++;
                    }
                    System.out.print(list.elementAt(i));
                    System.out.println("]");
                });

        erlangObjectName.put(OtpErlangMap.class, "map");
        erlangObjectDescribeAction.put(OtpErlangMap.class,
                (OtpErlangObject o) -> {

                    OtpErlangMap map = (OtpErlangMap) o;
                    System.out.println("Map:");

                    for(Map.Entry<OtpErlangObject, OtpErlangObject> entry: map.entrySet()) {
                        System.out.println(entry.getKey().toString() + " => " + entry.getValue().toString());
                    }
                });

        erlangObjectName.put(OtpErlangPid.class, "pid");
        erlangObjectDescribeAction.put(OtpErlangPid.class,
                (OtpErlangObject o) -> {
                    OtpErlangPid pid = (OtpErlangPid) o;
                    System.out.println("Pid id: " + pid.id() + ", node: " + pid.node());
                });
    }

    public void start() throws IOException {

        OtpNode serverNode = new OtpNode(clientNodeName, cookie);
        otpMbox = serverNode.createMbox(clientMailboxName);

        new Thread(() -> {
            Scanner stdin = new Scanner(System.in);

            while (true) {

                String choice = null;
                while (choice == null) {

                    System.out.println("What do you need from server?");

                    for (RequestType requestType : RequestType.values()) {
                        System.out.println("..." + requestType.getName() + "?");
                    }
                    System.out.println("*** quit" );

                    choice = stdin.next();
                    if (choice.equals("quit")) System.exit(0);

                    String finalChoice = choice;
                    if (Arrays.stream(RequestType.values()).map(RequestType::getName).anyMatch(s -> s.equals(finalChoice))) {
                        System.out.println("I'll ask for " + choice + "!");
                    }
                    else {
                        System.out.println("I don't understand...");
                        choice = null;
                    }
                }

                OtpErlangObject[] params = {new OtpErlangAtom("request"), otpMbox.self(), new OtpErlangAtom(choice)};
                OtpErlangTuple msgToServer = new OtpErlangTuple(params);
                otpMbox.send(serverMailboxName, serverNodeName, msgToServer);

                try {
                    System.out.println("Wait for server response...");
                    OtpErlangObject msg = otpMbox.receive();

                    try {
                        System.out.println("I have received: " + erlangObjectName.get(msg.getClass()) + "!");
                        erlangObjectDescribeAction.get(msg.getClass()).describe(msg);
                    }
                    catch (NullPointerException e) {
                        System.out.println("I have received unsupported Erlang type... It looks that: " + msg.toString());
                    }
                    System.out.println();

                } catch (OtpErlangExit otpErlangExit) {
                    otpErlangExit.printStackTrace();
                    System.exit(1);
                } catch (OtpErlangDecodeException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}