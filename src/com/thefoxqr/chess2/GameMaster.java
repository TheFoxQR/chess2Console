package com.thefoxqr.chess2;
import java.io.*;
// import java.util.*;

public class GameMaster implements Runnable {
    private Game game;
    private Parser parser;
    private Recorder recorder;
    private boolean moveSwitch = false;
    private MoveGenerator mvg;
    private DataDump dump;
//    private int counter = 0;

    public GameMaster() {
        this.game = new Game();
        this.parser = new FileParser("/projects/Chess2/pgn.txt", game);
        this.recorder = new Recorder();
        this.mvg = new MoveGenerator(game);
    }

    public GameMaster(Parser parser) {
        this.game = new Game();
        this.parser = parser;
        parser.setGame(this.game);
        this.recorder = new Recorder();
        this.mvg = new MoveGenerator(game);
    }

//     public void drawConsoleBoard() {
//         try {
//             String outString;
//             outString = "\033[H\033[2J";
//             out.write(outString.getBytes());
//             out.flush();
//             Square sq;
//             for (int i = 7; i >= 0; i--) {
//                 outString = "    ";
//                 out.write(outString.getBytes());
//                 outString = "";
//                 for(int k = 0; k < 49; k++) outString = outString + "-";
//                 outString = outString + "\n";
//                 out.write(outString.getBytes());
//                 outString = "    ";
//                 out.write(outString.getBytes());
//                 for (int j = 0; j < 8; j++) {
//                     sq = game.board.getSquare((char)('a' + j), (char)('1' + i));
//                     out.write('|');
//                     if (sq.getColor().equals("white")) out.write(' ');
//                     else out.write('*');
//                     outString = "    ";
//                     out.write(outString.getBytes());
// //                    outString = " " + sq.getPosition() + " ";
// //                    out.write(outString.getBytes());
//                 }
//                 out.write('|');
//                 if (i == 4) {
//                     outString = "            Black Out: ";
//                     Piece[] pOut = game.getOutStack("black");
//                     for (int x = 0; x < pOut.length; x++) outString = outString + pOut[x].getType();
//                     out.write(outString.getBytes());
//                 }
//                 if (i == 3) {
//                     outString = "                Check: " + game.isChecked((!moveSwitch ? "white" : "black"));
//                     out.write(outString.getBytes());
//                 }
//                 out.write('\n');
//                 outString = "    ";
//                 out.write(outString.getBytes());
//                 for (int j = 0; j < 8; j++) {
//                     sq = game.board.getSquare((char)('a' + j), (char)('1' + i));
//                     out.write('|');
//                     out.write(' ');
//                     outString = " " + sq.getPieceType();
//                     out.write(outString.getBytes());
//                     if (sq.getPieceColor().equals("black")) out.write('\"');
//                     else if (sq.getPieceColor().equals("white")) out.write(' ');
//                     else System.out.print(" ");
//                     out.write(' ');
//                 }
//                 out.write('|');
//                 outString = "  " + (i + 1) + "     ";
//                 out.write(outString.getBytes());
//                 if (i == 4) {
//                     outString = "         Turn: " + (moveSwitch ? "black" : "white");
//                     out.write(outString.getBytes());
//                 }
//                 if (i == 3) {
//                     outString = "    White Out: ";
//                     Piece[] pOut = game.getOutStack("white");
//                     for (int x = 0; x < pOut.length; x++) outString = outString + pOut[x].getType();
//                     out.write(outString.getBytes());
//                 }
//                 out.write('\n');
//             }
//             outString = "    ";
//             out.write(outString.getBytes());
//             outString = "";
//             for(int k = 0; k < 49; k++) outString = outString + "-";
//             outString = outString + "\n\n";
//             out.write(outString.getBytes());
//             outString = "    ";
//             out.write(outString.getBytes());
//             for (int i = 0; i < 8; i++) {
//                 outString = "   " + (char)('a' + i) + "  ";
//                 out.write(outString.getBytes());
//             }
//             outString = "\n\n";
//             out.write(outString.getBytes());
//         }
//         catch (Exception e) {}
//     }

    public boolean checkEndLegality() {
        return !game.isChecked((moveSwitch ? "black" : "white"));
    }

    private Move newMove;
    private boolean isIllegal = false;
    private boolean isGameOver = false;
    private boolean setHasMoved = false;
    SubMove sm = null;

    public void run() {
        if (!isIllegal) {
            if (!mvg.hasLegalMoves((moveSwitch ? "black" : "white"))) {
                endGame(!moveSwitch);
                // break;
            }
            // drawConsoleBoard();
            if (!moveSwitch) newMove = new Move();
        }

        if (!isGameOver) {
            isIllegal = false;
            setHasMoved = false;
            newMove = parser.fillNextSubMove(newMove);

            sm = !moveSwitch ? newMove.getWhiteMove() : newMove.getBlackMove();
            makeSubMove(sm);
            if (!checkEndLegality()) {
                isIllegal = true;
                sm.setType("ir");
            }

            if (isIllegal) rollbackSubMove(sm);
            else {
                if (moveSwitch) recorder.addMove(newMove);
                moveSwitch = moveSwitch ? false : true;
            }
        }

        dump = packData(new String(sm.getType()));
    }

    public void endGame(boolean isWhite) {
        // String result;
        char[] result = new char[2];
        result[0] = 'e';
        if (game.isChecked(isWhite? "white": "black")) result[1] = (!isWhite? 'w': 'b');
        else result[1] = 's';
        sm = new SubMove(null, null, result);
        isGameOver = true;
        // System.out.println(result);
    }

    public void rollbackSubMove(SubMove sm) {
        char[] type = sm.getType();
        switch (type[1]) {
            case 'i':
                break;
            case 'm':
                break;
            default:
                if (type[0] == 'c')
                    break;
                parser.goBack();
                Square startSq = game.board.getSquare(sm.getPiece().getPosition());
                Square endSq = game.board.getSquare(sm.getSquare().getPosition());
                Piece piece = endSq.getPiece();
                endSq.resetPiece();
                if (setHasMoved)
                    game.resetHasMoved(startSq.getPosition());
                // transform piece in case of promotion.
                switch (type[0]) {
                    case 'q':
                    case 'n':
                    case 'b':
                    case 'r':
                        piece = new Pawn(piece.getSquare(), piece.getColor());
                        break;
                }
                piece.put(startSq);
                startSq.setPiece(piece);
                if (type[0] == 'x' || type[0] == '*') {
                    piece = game.removePieceFromOutStack((!moveSwitch ? "black" : "white"));
                    piece.put(endSq);
                    endSq.setPiece(piece);
                }
                break;
        }
    }

    public void makeSubMove(SubMove subMove) {
        char[] type = subMove.getType();
        switch (type[0]) {
            case 'i':
                isIllegal = true;
                switch (type[1]) {
                    case 'i':
                    case 'm':
                }
            case '-':
                break;
            case 'c':
                switch (type[1]) {
                    case 'k':
                        char rank1 = !moveSwitch ? '1' : '8';
                        char fileKSource1 = 'e', fileRDest1 = 'f', fileKDest1 = 'g', fileRSource1 = 'h';
                        Square KSource1 = game.board.getSquare(fileKSource1, rank1);
                        Square KDest1 = game.board.getSquare(fileKDest1, rank1);
                        Square RSource1 = game.board.getSquare(fileRSource1, rank1);
                        Square RDest1 = game.board.getSquare(fileRDest1, rank1);
                        Piece king1 = KSource1.getPiece();
                        Piece rook1 = RSource1.getPiece();
                        KSource1.resetPiece();
                        RSource1.resetPiece();
                        king1.put(KDest1);
                        rook1.put(RDest1);
                        KDest1.setPiece(king1);
                        RDest1.setPiece(rook1);
                        break;
                    case 'q':
                        char rank2 = !moveSwitch ? '1' : '8';
                        char fileKSource2 = 'e', fileRDest2 = 'd', fileKDest2 = 'c', fileRSource2 = 'a';
                        Square KSource2 = game.board.getSquare(fileKSource2, rank2);
                        Square KDest2 = game.board.getSquare(fileKDest2, rank2);
                        Square RSource2 = game.board.getSquare(fileRSource2, rank2);
                        Square RDest2 = game.board.getSquare(fileRDest2, rank2);
                        Piece king2 = KSource2.getPiece();
                        Piece rook2 = RSource2.getPiece();
                        KSource2.resetPiece();
                        RSource2.resetPiece();
                        king2.put(KDest2);
                        rook2.put(RDest2);
                        KDest2.setPiece(king2);
                        RDest2.setPiece(rook2);
                        break;
                }
                break;
            case '*':
            case 'x':
                game.addPieceToOutStack(game.board.getPieceAt(subMove.getSquare().getPosition()));
            case '+':
            case '^':
            default:
                Square startSq = game.board.getSquare(subMove.getPiece().getPosition());
                Square endSq = game.board.getSquare(subMove.getSquare().getPosition());
                Piece piece = startSq.getPiece();
                startSq.resetPiece();
                if (piece.getType() == 'R') {
                    String position = startSq.getPosition();
                    if (position.equals("a8") || position.equals("h8") || position.equals("a1") || position.equals("h1")) {
                        game.setHasMoved(position);
                        setHasMoved = true;
                    }
                }
                else if (piece.getType() == 'K') {
                    String position = startSq.getPosition();
                    if (position.equals("e8") || position.equals("e1")) {
                        game.setHasMoved(position);
                        setHasMoved = true;
                    }
                }
                // transform piece in case of promotion.
                switch (type[1]) {
                    case 'q':
                        piece = new Queen(piece.getSquare(), piece.getColor());
                        break;
                    case 'n':
                        piece = new Knight(piece.getSquare(), piece.getColor());
                        break;
                    case 'b':
                        piece = new Bishop(piece.getSquare(), piece.getColor());
                        break;
                    case 'r':
                        piece = new Rook(piece.getSquare(), piece.getColor());
                        break;
                }
                piece.put(endSq);
                endSq.setPiece(piece);
        }
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public DataDump packData(String moveType) {
        DataDump dump = new DataDump();
        dump.setMoveType(moveType);
        dump.setBoard(game.board.getAbstraction());
        dump.setChecked(game.isChecked((!moveSwitch ? "white" : "black")));
        dump.setCaptured((moveType.charAt(0) == 'x' || moveType.charAt(0) == '*')? true: false);
        dump.setPlayedBy((moveSwitch ? "black" : "white"));
        dump.setOutStack(game.getOutStack("white"), "white");
        dump.setOutStack(game.getOutStack("black"), "black");
        return dump;
    }


    public DataDump getDump() {
        return this.dump;
    }

    public DataDump getInitDump() {
        DataDump dump = new DataDump();
        dump.setMoveType("^-");
        dump.setBoard(game.board.getAbstraction());
        dump.setChecked(false);
        dump.setCaptured(false);
        dump.setPlayedBy("white");
        dump.setOutStack(game.getOutStack("white"), "white");
        dump.setOutStack(game.getOutStack("black"), "black");
        return dump;
    }
}
