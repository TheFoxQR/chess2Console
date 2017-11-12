package com.thefoxqr.chess2;
// import java.io.IOException;
import java.util.Scanner;
import java.io.File;
// import java.io.FileNotFoundException;

public class FileParser implements Parser
{
    private String path;
    private Scanner fileScanner;
    private Game game;
    private StringBuilder whiteMove = new StringBuilder(""), blackMove = new StringBuilder("");
//    private int counter = 1;
    private String result = "unknown";
    private ParseFlags parseflags = new ParseFlags();

    public FileParser(String file_path) {
        this.path = file_path;
        try {
            fileScanner = new Scanner(new File(path));
        }
        catch(Exception e) {
            System.out.println("File not found.");
        }
        findNextMove();
    }

    public FileParser(String file_path, Game game) {
        this.path = file_path;
        this.game = game;
        try {
            fileScanner = new Scanner(new File(path));
        }
        catch(Exception e) {
            System.out.println("File not found.");
        }
        findNextMove();
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public ParseFlags getParseFlags() {
        return this.parseflags;
    }

    public void findNextMove() {
        String marker = " ";
        while (marker.charAt(marker.length() - 1) != '.' && fileScanner.hasNext()) {
            marker = fileScanner.next();
        }
    }

    public void goBack() {
        color = color.equals("white") ? "black" : "white";
    }

    public void readNextMove() {
        if (fileScanner.hasNext()) whiteMove = new StringBuilder(fileScanner.next());
        if (fileScanner.hasNext()) blackMove = new StringBuilder(fileScanner.next());
        findNextMove();
    }

    String color = "white";
    public Move fillNextSubMove(Move xmove) {
        if (color.equals("white")) {
//            System.out.println("Read next move.");
            readNextMove();
        }
        parseMove(color);
        char[] type = {'^','-'};
        Square sq = null;
        Piece[] pieces = null;
        if (!parseflags.getResult() && !parseflags.getKingsideCastle() && !parseflags.getQueensideCastle()) {
            sq = game.board.getSquare(parseflags.getEndPosition());
            pieces = game.findRelevantPieces(color, parseflags.getPieceType(), parseflags.getEndPosition());
            if (parseflags.getCapture() ^ parseflags.getCheck()) {
                if (parseflags.getCapture()) type[0] = 'x';
                if (parseflags.getCheck()) type[0] = '+';
            }
            if (parseflags.getCapture() && parseflags.getCheck()) type[0] = '*';
        }
        else if (parseflags.getKingsideCastle()) {
            type[0] = 'c';
            type[1] = 'k';
        }
        else if (parseflags.getQueensideCastle()) {
            type[0] = 'c';
            type[1] = 'q';
        }

        if (parseflags.getResult()) type[0] = '-';
        if (parseflags.getPieceType() == 'P'){
            if (!parseflags.getCapture()) {
                int i = 0;
                while (pieces[i].getPosition().indexOf(parseflags.getEndPosition().charAt(0)) != 0) i++;
                if (i != 0) pieces[0] = pieces[i];
            }
            else {
                int i = 0;
                while (pieces[i].getPosition().indexOf(parseflags.getEndPosition().charAt(0)) != -1) i++;
                if (i != 0) pieces[0] = pieces[i];
            }
        }
        if (parseflags.getAmbiguity()) {
            int i = 0;
            while (pieces[i].getPosition().indexOf(parseflags.getClarification()) != 0) i++;
            if (i != 0) pieces[0] = pieces[i];
        }
        Piece p = pieces != null ? pieces[0] : null;
        if (color.equals("white")) {
            xmove.setWhiteMove(new SubMove(p, sq, type));
            color = "black";
        }
        else {
            xmove.setBlackMove(new SubMove(p, sq, type));
            color = "white";
        }
        return xmove;
    }

    public void parseMove(String color) {
        StringBuilder parsee = color.equals("white") ? whiteMove : blackMove;
        parseflags.reset();
//        System.out.println(parsee);

        if (parsee.indexOf("O-O") != -1) {
            parseflags.setKingsideCastle(true);
            parsee.delete(parsee.indexOf("O-O"), parsee.indexOf("O-O") + 3);
        }
        else if (parsee.indexOf("O-O-O") != -1) {
            parseflags.setQueensideCastle(true);
            parsee.delete(parsee.indexOf("O-O-O"), parsee.indexOf("O-O-O") + 5);
        }
        else if (parsee.indexOf("1/2-1/2") != -1) {
            parseflags.setResult(true);
            result = "1/2-1/2";
            parsee.delete(parsee.indexOf("1/2-1/2"), parsee.indexOf("1/2-1/2") + 7);
        }
        else if (parsee.indexOf("0-1") != -1) {
            parseflags.setResult(true);
            result = "0-1";
            parsee.delete(parsee.indexOf("0-1"), parsee.indexOf("0-1") + 3);
        }
        else if (parsee.indexOf("1-0") != -1) {
            parseflags.setResult(true);
            result = "1-0";
            parsee.delete(parsee.indexOf("1-0"), parsee.indexOf("1-0") + 3);
        }
        else {
            if (parsee.indexOf("+") != -1) {
                parseflags.setCheck(true);
                parsee.deleteCharAt(parsee.indexOf("+"));
            }

            if (parsee.indexOf("x") != -1) {
                parseflags.setCapture(true);
                parsee.deleteCharAt(parsee.indexOf("x"));
            }

            switch (parsee.charAt(0)) {
                case 'K':
                    parsee.deleteCharAt(0);
                    parseflags.setPieceType('K');
                    break;
                case 'Q':
                    parsee.deleteCharAt(0);
                    parseflags.setPieceType('Q');
                    break;
                case 'R':
                    parsee.deleteCharAt(0);
                    parseflags.setPieceType('R');
                    break;
                case 'B':
                    parsee.deleteCharAt(0);
                    parseflags.setPieceType('B');
                    break;
                case 'N':
                    parsee.deleteCharAt(0);
                    parseflags.setPieceType('N');
                    break;
                default:
                    parseflags.setPieceType('P');
                    break;
            }

            parseflags.setEndPosition(parsee.substring(parsee.length() - 2, parsee.length()));
            parsee.delete(parsee.indexOf(parseflags.getEndPosition()), parsee.indexOf(parseflags.getEndPosition()) + 2);

            if (parsee.length() > 0) {
                parseflags.setAmbiguity(true);
                parseflags.setClarification(new String(parsee));
            }
        }
    }

    public boolean endOfInput() {
        return !fileScanner.hasNext();
    }

    public String getResult() {
        return result;
    }
}
