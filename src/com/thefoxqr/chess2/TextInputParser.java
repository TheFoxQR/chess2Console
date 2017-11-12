package com.thefoxqr.chess2;
import java.io.*;
import java.util.regex.*;

public class TextInputParser implements Parser
{
    private Game game;
    private StringBuilder whiteMove = new StringBuilder(""), blackMove = new StringBuilder("");
    private String result = "unknown";
    private ParseFlags parseflags = new ParseFlags();
    String color = "white";
    BufferedReader breader;
    boolean inputEnd = false;

    public TextInputParser(InputStream in) {
        this.breader = new BufferedReader(new InputStreamReader(in));
    }

    public TextInputParser(InputStream in, Game game) {
        this.game = game;
        this.breader = new BufferedReader(new InputStreamReader(in));
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public ParseFlags getParseFlags() {
        return this.parseflags;
    }

    public void readNextMove() {
        try {
            String move = breader.readLine();
            if (color.equals("white")) whiteMove = new StringBuilder(move);
            else blackMove = new StringBuilder(move);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkStringLegality(String readMove) {
        return Pattern.matches("(?:[PKQNBR]?[a-h]?[1-8]?[x]?[a-h]{1}[1-8]{1}([=]{1}[QNBR]{1})?)?|(?:O((-O)|(O)){1,2}){1}[+#]?", readMove);
    }

    public Move fillNextSubMove(Move xmove) {
        char[] type = {'^','-'};
        Square sq = null;
        Piece[] pieces = null;
        // legality check for the string.
        readNextMove();
        if (checkStringLegality(new String(color.equals("white") ? whiteMove : blackMove))) {
            parseMove(color);
            // legality check for the move.
            if (!game.checkMovementLegality(parseflags)) {
                type[0] = 'i';
                type[1] = 'm';
            }
        }
        else {
            type[0] = 'i';
            type[1] = 'i';
        }

        // if illegal, do not go further.
        if (type[0] == 'i') {
            Piece p = pieces != null ? pieces[0] : null;
            if (color.equals("white")) xmove.setWhiteMove(new SubMove(p, sq, type));
            else xmove.setBlackMove(new SubMove(p, sq, type));
            return xmove;
        }

        // if the input move passes the first two levels of legality, go ahead.
        if (!parseflags.getResult() && !parseflags.getKingsideCastle() && !parseflags.getQueensideCastle()) {
            sq = game.board.getSquare(parseflags.getEndPosition());
            // System.out.println("Start");
            pieces = game.findRelevantPieces(color, parseflags.getPieceType(), parseflags.getEndPosition());
            // System.out.println("End");
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
        if (parseflags.getPromotion()) {
            switch (parseflags.getPromotedTo()) {
                case 'Q':
                    type[1] = 'q';
                    break;
                case 'N':
                    type[1] = 'n';
                    break;
                case 'B':
                    type[1] = 'b';
                    break;
                case 'R':
                    type[1] = 'r';
                    break;
            }
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

    public void goBack() {
        color = color.equals("white") ? "black" : "white";
    }

    public void parseMove(String color) {
        StringBuilder parsee = color.equals("white") ? whiteMove : blackMove;
        parseflags.reset();
//        System.out.println(parsee);

        parseflags.setColor(color);

        if (parsee.indexOf("O-O-O") != -1) {
            parseflags.setQueensideCastle(true);
            parsee.delete(parsee.indexOf("O-O-O"), parsee.indexOf("O-O-O") + 5);
        } else if (parsee.indexOf("OOO") != -1) {
            parseflags.setQueensideCastle(true);
            parsee.delete(parsee.indexOf("OOO"), parsee.indexOf("OOO") + 5);
        } else if (parsee.indexOf("O-O") != -1) {
            parseflags.setKingsideCastle(true);
            parsee.delete(parsee.indexOf("O-O"), parsee.indexOf("O-O") + 3);
        } else if (parsee.indexOf("OO") != -1) {
            parseflags.setKingsideCastle(true);
            parsee.delete(parsee.indexOf("OO"), parsee.indexOf("OO") + 3);
        } else {
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
                case 'P':
                    parsee.deleteCharAt(0);
                default:
                    parseflags.setPieceType('P');
                    if (parsee.indexOf("=") != -1) {
                        parseflags.setPromotion(true);
                        parseflags.setPromotedTo(parsee.charAt(parsee.indexOf("=") + 1));
                        parsee.deleteCharAt(parsee.indexOf("=") + 1);
                        parsee.deleteCharAt(parsee.indexOf("="));
                    }
                    break;
            }

            parseflags.setEndPosition(parsee.substring(parsee.length() - 2, parsee.length()));
            parsee.delete(parsee.indexOf(parseflags.getEndPosition()), parsee.indexOf(parseflags.getEndPosition()) + 2);

            if (parseflags.getPromotion()) {
                if (color.equals("white") && parseflags.getEndPosition().charAt(1) != '8') parseflags.setPromotion(false);
                else if (color.equals("black") && parseflags.getEndPosition().charAt(1) != '1') parseflags.setPromotion(false);
            }

            if (parsee.length() > 0) {
                parseflags.setAmbiguity(true);
                parseflags.setClarification(new String(parsee));
            }

            if (!parseflags.getCheck()) {
                if (parseflags.getPieceType() != 'K') parseflags.setCheck(game.isChecked(color));
                else parseflags.setCheck(game.checkChecked(color, parseflags.getEndPosition()));
            }


            if (!parseflags.getCapture()) {
                Piece p = game.board.getPieceAt(parseflags.getEndPosition());
                if (p != null && !p.getColor().equals(color)) parseflags.setCapture(true);
            }
        }
    }

    public boolean endOfInput() {
        return inputEnd;
    }

    public String getResult() {
        return result;
    }
}
