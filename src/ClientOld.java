import com.thefoxqr.chess2.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class ClientOld
{
    public static void main(String[] argv) {
        try {
            // connect to the server.
            Socket socket = new Socket(InetAddress.getLocalHost(), 5178);
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            DataDump dump;
            Display display = new Display();

            // print what it shows.
            dump = (DataDump)ois.readObject();
            display.drawBoard(dump);

            String s = null;
            while (!(s = in.readLine()).equals("end")) {
                if (s.equals("inp")) System.out.print("Enter a move: ");
                out.println(stdIn.readLine());
                dump = (DataDump)ois.readObject();
                display.drawBoard(dump);
                display.showMoveLog(dump.getMoveType());
                s = null;
            }

            // pause for input, and loop.
            socket.close();
        } catch(Exception e) {}
    }
}
