import java.nio.channels.*;
import java.nio.*;
import java.net.*;
import java.io.*;
import com.thefoxqr.chess2.*;

public class GameServer implements Runnable
{
    String ok = " [   \033[0;32mOk\033[0m   ] ";
    String err = " [  \033[0;31mError\033[0m ] ";
    String std = " [   \033[1;33mStd\033[0m  ] ";
    ServerSocketChannel ssc;
    SocketChannel[] client = new SocketChannel[2];
    ByteBuffer[] buff = new ByteBuffer[client.length];
    boolean[] exit = new boolean[client.length];
    ObjectOutputStream[] oos = new ObjectOutputStream[2];
    ByteBuffer clientIn = null;
    boolean dumpPulled = false;
    String roundType = "multicast";

    StringInputParser parser = new StringInputParser();
    GameMaster gm = new GameMaster(parser);
    DataDump dump = new DataDump();
    Display display = new Display();

    private boolean canNotExit() {
        boolean canExit = true;
        for (int i = 0; i < exit.length; i++) canExit = canExit && exit[i];
        canExit = canExit || gm.isGameOver();
        return !canExit;
    }

    private void allocateStates() {
        // check if dump was pulled. if yes - state -> 2 2
        // (extra condition: check if server dump equals game dump.)
        buff[0].clear();
        buff[1].clear();
        if (dumpPulled || gm.isGameOver()) {
            roundType = "multicast";
            for (int i = 0; i < client.length; i++) buff[i].putInt(2);
            // dumpPulled = false;
        }
        // otherwise state allocation takes place according to playedBy in server dump
        else {
            if (dump.getPlayedBy().equals("white")) {
                roundType = "poll0";
                buff[0].putInt(1);
                buff[1].putInt(0);
            }
            else {
                roundType = "poll1";
                buff[0].putInt(0);
                buff[1].putInt(1);
            }
        }
        System.out.println(std + roundType);
        buff[0].flip();
        try {
            while (buff[0].hasRemaining()) client[0].write(buff[0]);
        } catch (Exception e) {
            System.out.println(err + "IOException while writing to client 0.");
        }
        buff[1].flip();
        try {
            while (buff[1].hasRemaining()) client[1].write(buff[1]);
        } catch (Exception e) {
            System.out.println(err + "IOException while writing to client 1.");
        }
    }

    public void start() {
        try {
            ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(5171));
            ssc.configureBlocking(true);
            System.out.println(ok + "Server Channel now open.");
        } catch (Exception e) {
            System.out.println(err + "Server Channel could not be opened.");
            return;
        }

        for (int i = 0; i < client.length; i++) {
            try {
                client[i] = ssc.accept();
                buff[i] = ByteBuffer.allocate(10);
                oos[i] = new ObjectOutputStream(client[i].socket().getOutputStream());
                exit[i] = false;
                System.out.println(ok + "Client connected successfully.");
            } catch (Exception e) {
                System.out.println(err + "Client could not connect to server.");
            }
        }
        clientIn = ByteBuffer.allocate(50);
        dump = gm.getInitDump();
        // dumpPulled = true;
    }

    public void run() {
        this.start();
        System.out.println(std + "Server started.");

        while (this.canNotExit()) {
            // allocate states
            allocateStates();
            System.out.println(std + "States allocated.");
            // switch case for execution.
            switch (roundType) {
                case "multicast":
                    dumpPulled = false;
                    try {
                        oos[0].writeObject(dump);
                        oos[0].flush();
                        oos[1].writeObject(dump);
                        oos[1].flush();
                        System.out.println(ok + "Dump sent.");
                    } catch (Exception e) {
                        System.out.println(err + "Dump could not be sent.");
                    }
                    break;
                case "poll0":
                    clientIn.clear();
                    int x1 = 0;
                    try {
                        x1 = client[0].read(clientIn);
                        System.out.println(std + "x: " + x1);
                        // do {
                        // } while (x != 0);
                    } catch (Exception e) {
                        System.out.println(err + "IOException while reading from client 0.");
                    }
                    // clientIn.clear();
                    byte[] b1 = new byte[x1];
                    // clientIn.get(b1);
                    System.arraycopy(clientIn.array(), 0, b1, 0, x1);
                    // System.out.println(err + b1[0] + b1[1]);
                    String s1 = new String(b1);
                    System.out.println(std + "Client 0 sent: " + s1 + dump.getMoveType());
                    switch (s1) {
                        case "exit":
                            exit[0] = true;
                            break;
                        default:
                            if (exit[0]) exit[0] = false;
                            parser.setMove(s1);
                            break;
                    }
                    break;
                case "poll1":
                    clientIn.clear();
                    int x2 = 0;
                    try {
                        x2 = client[1].read(clientIn);
                        System.out.println(std + "x: " + x2);
                        // do {
                        // } while (x != 0);
                    } catch (Exception e) {
                        System.out.println(err + "IOException while reading from client 0.");
                    }
                    byte[] b2 = new byte[x2];
                    // clientIn.get(b2);
                    System.arraycopy(clientIn.array(), 0, b2, 0, x2);
                    String s2 = new String(b2);
                    System.out.println(std + "Client 1 sent: " + s2 + dump.getMoveType());
                    switch (s2) {
                        case "exit":
                            exit[1] = true;
                            break;
                        default:
                            if (exit[1]) exit[1] = false;
                            parser.setMove(s2);
                            break;
                    }
                    break;
                default:
                    System.out.println(err + "Malformed roundType.");
            }

            if (roundType.equals("poll0") || roundType.equals("poll1")) {
                gm.run();
                dump = gm.getDump();
                dumpPulled = true;
                // display.drawBoard(dump);
                // display.showMoveLog(dump.getMoveType());
            }
        }

        this.end();
    }

    public void end() {
        try {
            for (int i = 0; i < client.length; i++) client[i].close();
            ssc.close();
            System.out.println(ok + "Channels successfully closed.");
        } catch (Exception e) {
            System.out.println(err + "Channels could not be closed.");
            return;
        }
    }
}
