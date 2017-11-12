import java.nio.channels.*;
import java.nio.*;
import java.net.*;
import java.io.*;
import com.thefoxqr.chess2.*;

public class Player implements Runnable
{
    String ok = " [   \033[0;32mOk\033[0m   ] ";
    String err = " [  \033[0;31mError\033[0m ] ";
    String std = " [   \033[1;33mStd\033[0m  ] ";
    ByteBuffer buff = ByteBuffer.allocate(10);
    ByteBuffer stdInBuff = ByteBuffer.allocate(50);
    SocketChannel server;
    ObjectInputStream ois;
    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    int state = 0, bytesRead = 0;

    DataDump dump;
    Display display = new Display();

    private void readState() {
        buff.clear();
        try {
            do {
                bytesRead = server.read(buff);
                // System.out.println(std + "bytesRead: " + bytesRead);
                buff.clear();
            } while (bytesRead == 0);
        } catch (Exception e) {
            System.out.println(err + "IOException while reading from server.");
        }
        state = buff.getInt();
        if (bytesRead == -1) state = 0;
        // System.out.println(std + "state: " + state);
    }

    public void start() {
        try {
            server = SocketChannel.open();
            server.connect(new InetSocketAddress(InetAddress.getLocalHost(), 5171));
            ois = new ObjectInputStream(server.socket().getInputStream());
            System.out.println(ok + "Connected to server.");
        } catch (Exception e) {
            System.out.println(err + "Could not connect to server.");
            return;
        }
    }

    public void run() {
        this.start();

        int x = 0;
        while (bytesRead != -1) {
            // read state from sever.
            readState();
            // System.out.println(ok + "Read state: " + state + ": " + bytesRead);
            // switch case for execution.
            switch (state) {
                case 0:
                    break;
                case 1:
                    System.out.print("Enter a move: ");
                    stdInBuff.clear();
                    try {
                        stdInBuff.put(stdIn.readLine().getBytes());
                        stdInBuff.flip();
                        server.write(stdInBuff);
                    } catch (Exception e) {
                        System.out.println(err + "IOException while writing to server.");
                    }
                    break;
                case 2:
                    // read Object
                    try {
                        dump = (DataDump)ois.readObject();
                        display.drawBoard(dump);
                        display.showMoveLog(dump.getMoveType());
                    } catch (Exception e) {
                        System.out.println(err + "Dump could not be recieved.");
                    }
                    break;
            }
        }

        this.end();
    }

    public void end() {
        try {
            server.close();
            System.out.println(ok + "Connection successfully closed.");
        } catch (Exception e) {
            System.out.println(err + "Could not close connection.");
            return;
        }
    }
}
