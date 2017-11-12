import java.net.*;
import java.io.*;
import java.util.*;
import com.thefoxqr.chess2.*;

public class ServerOld
{
    public static void main(String[] argv) {
        InputStream[] serverIn = new InputStream[2], clientIn = new InputStream[2];
        InputStream chess2In = null;
        PrintWriter chess2Out = null;
        PrintWriter[] clientOut = new PrintWriter[2];
        Socket[] client = new Socket[2];
        Socket gameClient, game;
        boolean[] exitFlag = {false, false};
        ServerSocket serverSocket = null, gameSocket = null;
        byte clientCount = 0;
        ObjectOutputStream[] ois = new ObjectOutputStream[2];

        try {
            // make server socket.
            serverSocket = new ServerSocket(5178);
            System.out.println(" [ OK  ] Client Server started.");

        } catch(Exception e) {
            System.out.println("[ ERR ] Client Server could not start.");
            return;
        }
        try {
            // make server socket.
            gameSocket = new ServerSocket(5179);
            System.out.println(" [ OK  ] Game Server started.");

        } catch(Exception e) {
            System.out.println("[ ERR ] Game Server could not start.");
            return;
        }
        try {
            game = new Socket(InetAddress.getLocalHost(), 5179);
            gameClient = gameSocket.accept();
            chess2In = game.getInputStream();
            chess2Out = new PrintWriter(gameClient.getOutputStream(), true);
            System.out.println(" [ OK  ] Game client initialized.");
        } catch (Exception e) {
            System.out.println("[ ERR ] Game client could not connect to server.");
            return;
        }

        // wait for both clients to connect.
        while (clientCount < 2) {
            try {
                client[clientCount] = serverSocket.accept();
                clientIn[clientCount] = client[clientCount].getInputStream();
                clientOut[clientCount] = new PrintWriter(client[clientCount].getOutputStream(), true);
                ois[clientCount] = new ObjectOutputStream(client[clientCount].getOutputStream());
                System.out.println(" [ OK  ] Client connected successfully.");
            } catch (Exception e) {
                System.out.println("[ ERR ] Client could not connect to server.");
                return;
            }
            clientCount++;
        }

        // determine precedence.
        // start and run game until game ends.
        DataDump dump;
        TextInputParser parser = new TextInputParser(chess2In);
        GameMaster gm = new GameMaster(parser);

        dump = gm.getInitDump();
        try {
            ois[0].writeObject(dump);
            ois[0].flush();
            ois[1].writeObject(dump);
            ois[1].flush();
            System.out.println(" [ OK  ] Dump sent.");
        } catch (Exception e) {
            System.out.println(" [ ERR ] Error sending dump.");
        }

        boolean switcher = dump.getPlayedBy().equals("white");
        serverIn[0] = clientIn[0];
        while ((!exitFlag[0] || !exitFlag[1]) && !gm.isGameOver()) {
            serverIn[0] = switcher? clientIn[0]: clientIn[1];
            serverIn[1] = !switcher? clientIn[0]: clientIn[1];

            try {
                System.out.print(" [ OK  ] Sending input request...  ");
                if (switcher && !exitFlag[0]) {
                    clientOut[0].println("inp");
                    clientOut[0].flush();
                } else if (!switcher && !exitFlag[1]) {
                    clientOut[1].println("inp");
                    clientOut[1].flush();
                }
                System.out.println("sent.");
            } catch(Exception e) {
                System.out.println(" [ ERR ] Write error.");
                return;
            }

            String s = "";
            try {
                System.out.println(" [ OK  ] Waiting for input...  ");
                char c;
                while ((c = (char)serverIn[0].read()) != '\n') {
                    s = s + c;
                }

                System.out.println(" [ STD ] s: " + s);
                switch (s) {
                    case "exit":
                    if (switcher) {
                        exitFlag[0] = true;
                        clientOut[0].println("end");
                        clientOut[0].flush();
                        switcher = false;
                    } else {
                        exitFlag[1] = true;
                        clientOut[1].println("end");
                        clientOut[1].flush();
                        switcher = true;
                    }
                    break;
                    default:
                    chess2Out.println(s);
                    gm.run();
                    dump = gm.getDump();
                }

                // System.out.println((switcher? "white": "black") + ": " + s);
                serverIn[1].skip(serverIn[1].available());
            } catch (Exception e) {
                System.out.println(" [ ERR ] Read error.");
                return;
            }

            try {
                ois[0].writeObject(dump);
                ois[0].flush();
                ois[1].writeObject(dump);
                ois[1].flush();
                System.out.println(" [ OK  ] Dump sent.");
            } catch (Exception e) {
                System.out.println(" [ ERR ] Error sending dump.");
            }

            // System.out.print(dump.getPlayedBy());
            if (!exitFlag[0] && !exitFlag[1]) switcher = dump.getPlayedBy().equals("white");
        }

        // endgame stuff.
        try {
            client[0].close();
            client[1].close();
            serverSocket.close();
        } catch (Exception e) {
            System.out.println(" [ ERR ] Could not close streams.");
            return;
        }
    }
}
