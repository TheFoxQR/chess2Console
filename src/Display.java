import com.thefoxqr.chess2.*;

public class Display
{

    public void drawBoard(DataDump dump) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        String color1 = "\033[1;37m";
        String color2 = "\033[0;32m";
        String noColor = "\033[0m";
        String color3, color4;
        System.out.print("    ");
        for(int k = 0; k < 58; k++) System.out.print("_");
        System.out.println();
        for (int i = 7; i >= 0; i--) {
            System.out.print("    ");
            color3 = (i % 2 == 0)? color1: color2;
            for(int k = 0; k < 58; k++) {
                if (k == 0 || k == 57) System.out.print(noColor);
                else if (((k - 1) % 7) == 0 && i % 2 == 1) {
                    color3 = color3.equals(color2)? color1: color2;
                    System.out.print(color3);
                }
                else if (((k - 1) % 7) == 0 && i % 2 == 0) {
                    color3 = color3.equals(color1)? color2: color1;
                    System.out.print(color3);
                }
                System.out.print("-");
            }
            System.out.println();
            System.out.print("    ");
            System.out.print(noColor);
            // color3 = color3.equals(color1)? color2: color1;
            for (int j = 0; j < 8; j++) {
                color3 = color3.equals(color1)? color2: color1;
                System.out.print("|" +  color3 + "|");
                if (((i + j) % 2) == 0) System.out.print("*");
                else System.out.print(" ");
                System.out.print("    ");
            }
            System.out.print("|" + noColor + "|");
            if (i == 4) {
                System.out.print(noColor + "            Black Out: ");
                char[] stack = dump.getOutStack("black");
                for (int x = 0; x < stack.length; x++) System.out.print(stack[x]);
            }
            if (i == 3) {
                System.out.print(noColor + "                Flags: " + (dump.getChecked()? "Check ": "") + (dump.getCaptured()? "Captured ": ""));
            }
            System.out.println();
            System.out.print("    ");
            char[][] board = dump.getBoard();
            for (int j = 0; j < 8; j++) {
                color3 = color3.equals(color1)? color2: color1;
                System.out.print("|" + color3 + "| ");
                System.out.print(" " + noColor + (board[j][i] == 0? " ": (board[j][i] > 90? (color1 + (char)(board[j][i] - 32)):(color2 + board[j][i]))) + color3);
                System.out.print("  ");
            }
            System.out.print("|" + noColor + "|    " + (i + 1) + "     ");
            if (i == 4) {
                System.out.print("         Turn: " + dump.getPlayedBy());
            }
            if (i == 3) {
                System.out.print(noColor + "    White Out: ");
                char[] stack = dump.getOutStack("white");
                for (int x = 0; x < stack.length; x++) System.out.print(stack[x]);
            }
            System.out.println();
            System.out.print("    ");
            System.out.print(noColor);
            // color3 = color3.equals(color1)? color2: color1;
            for(int k = 0; k < 56; k++) {
                // color3 = color3.equals(color1)? color2: color1;
                if (k % 7 == 0) {
                    color3 = color3.equals(color1)? color2: color1;
                    System.out.print("|" + color3 + "|");
                    k++;
                }
                else System.out.print("_");
            }
            System.out.println("|" + noColor + "|");
        }
        System.out.print(noColor + "    ");
        for(int k = 0; k < 58; k++) System.out.print("-");
        System.out.println("\n");
        System.out.print("    ");
        for (int i = 0; i < 8; i++) {
            System.out.print("    " + (char)('a' + i) + "  ");
        }
        System.out.println("\n");
    }

    public void showMoveLog(String s) {
        switch (s) {
            case "ii":
            System.out.println("Illegal Input!\nThe input could not be parsed.\nPlease refer to pgn format.");
            break;
            case "im":
            System.out.println("Illegal Move!\nThe specified movement is not possible.");
            break;
            case "ir":
            System.out.println("Illegal Move!\nThe specified move leaves your king in check.");
            break;
            case "ew":
            System.out.println("Checkmate - White wins!");
            break;
            case "eb":
            System.out.println("Checkmate - Black wins!");
            break;
            case "es":
            System.out.println("Stalemate");
            break;
        }
    }
}
