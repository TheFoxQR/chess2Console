package com.thefoxqr.chess2;
import java.util.LinkedList;

public class Game
{
    public Board board;
    private LinkedList<Piece> whiteOut = new LinkedList<Piece>();
    private LinkedList<Piece> blackOut = new LinkedList<Piece>();
    private LinkedList<Piece> whiteIn = new LinkedList<Piece>();
    private LinkedList<Piece> blackIn = new LinkedList<Piece>();
    private boolean[][] castleHasMoved = {{true, true, true}, {true, true, true}};

    public Game() {
        board = new Board();
        Piece p;
        Square initPos;

        // get all white pawns in place.
        for (int i = 0; i < 8; i++) {
            initPos = board.getSquare((char)('a' + i), '2');
            p = new Pawn(initPos, "white");
            initPos.setPiece(p);
        }
        // get the white king in place.
        initPos = board.getSquare("e1");
        p = new King(initPos, "white");
        initPos.setPiece(p);
        // get the white queen in place.
        initPos = board.getSquare("d1");
        p = new Queen(initPos, "white");
        initPos.setPiece(p);
        // get the white rooks in place.
        initPos = board.getSquare("a1");
        p = new Rook(initPos, "white");
        initPos.setPiece(p);
        initPos = board.getSquare("h1");
        p = new Rook(initPos, "white");
        initPos.setPiece(p);
        // get the white bishops in place.
        initPos = board.getSquare("c1");
        p = new Bishop(initPos, "white");
        initPos.setPiece(p);
        initPos = board.getSquare("f1");
        p = new Bishop(initPos, "white");
        initPos.setPiece(p);
        // get the white knights in place.
        initPos = board.getSquare("b1");
        p = new Knight(initPos, "white");
        initPos.setPiece(p);
        initPos = board.getSquare("g1");
        p = new Knight(initPos, "white");
        initPos.setPiece(p);

        // get all black pawns in place.
        for (int i = 0; i < 8; i++) {
            initPos = board.getSquare((char)('a' + i), '7');
            p = new Pawn(initPos, "black");
            initPos.setPiece(p);
        }
        // get the black king in place.
        initPos = board.getSquare("e8");
        p = new King(initPos, "black");
        initPos.setPiece(p);
        // get the black queen in place.
        initPos = board.getSquare("d8");
        p = new Queen(initPos, "black");
        initPos.setPiece(p);
        // get the black rooks in place.
        initPos = board.getSquare("a8");
        p = new Rook(initPos, "black");
        initPos.setPiece(p);
        initPos = board.getSquare("h8");
        p = new Rook(initPos, "black");
        initPos.setPiece(p);
        // get the black bishops in place.
        initPos = board.getSquare("c8");
        p = new Bishop(initPos, "black");
        initPos.setPiece(p);
        initPos = board.getSquare("f8");
        p = new Bishop(initPos, "black");
        initPos.setPiece(p);
        // get the black knights in place.
        initPos = board.getSquare("b8");
        p = new Knight(initPos, "black");
        initPos.setPiece(p);
        initPos = board.getSquare("g8");
        p = new Knight(initPos, "black");
        initPos.setPiece(p);

        // set castleHasMoved to false;
        for (int i = 0; i < 3; i++) for (int j = 0; j < 2; j++) castleHasMoved[j][i] = false;
    }

    private void makeInStacks() {
        whiteIn.clear();
        blackIn.clear();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = board.getPieceAt((char)('a' + i), (char)('1' + j));
                if (p != null) {
                    if (p.getColor().equals("white")) whiteIn.add(p);
                    else blackIn.add(p);
                }
            }
        }
    }

    public Piece[] getInStack(String color) {
        makeInStacks();
        LinkedList<Piece> stack = new LinkedList<Piece>();
        // System.out.println("WI: " + whiteIn.size() + ", BI: " + blackIn.size());
        if (color.equals("white")) stack = whiteIn;
        else if (color.equals("black")) stack = blackIn;
        else;
        return stack.toArray(new Piece[0]);
    }

    public void setHasMoved(String position) {
        int x = (position.charAt(1) == '1' ? 0 : 1), y = 0;
        if (position.charAt(0) == 'e') y = 1;
        else if (position.charAt(0) == 'h') y = 2;
        castleHasMoved[x][y] = true;
    }

    public void resetHasMoved(String position) {
        int x = (position.charAt(1) == '1' ? 0 : 1), y = 0;
        if (position.charAt(0) == 'e') y = 1;
        else if (position.charAt(0) == 'h') y = 2;
        castleHasMoved[x][y] = false;
    }

    private void printBitmap(byte[] bitmap) {
        System.out.println();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print((((bitmap[i] & (byte)Math.pow(2, j)) != 0)? "0": "1") + " ");
            }
            System.out.println();
        }
    }

    public Piece[] findRelevantPieces(String color, char pieceType, String position) {
        LinkedList<Piece> pieces = new LinkedList<Piece>();
        byte[] bitmap = board.getBitMap(color, pieceType);
        // printBitmap(bitmap);

        byte[] bitmaph = board.makeBitMap(color, pieceType, position);
        // printBitmap(bitmaph);

        for (int i = 0; i < 8; i++) bitmap[i] = (byte)(bitmap[i] & bitmaph[i]);
        // printBitmap(bitmap);

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if ((bitmap[i] & (byte)Math.pow(2, j)) != 0)
                    pieces.add(board.getPieceAt((char)('a' + j), (char)('8' - i)));
        return pieces.toArray(new Piece[0]);
    }

    public void addPieceToOutStack(Piece p) {
        if (p.getColor().equals("white")) whiteOut.add(p);
        else if (p.getColor().equals("black")) blackOut.add(p);
        else;
    }

    public Piece removePieceFromOutStack(String color) {
        Piece p = null;
        if (color.equals("white")) p = whiteOut.removeLast();
        else blackOut.removeLast();
        return p;
    }

    public Piece[] getOutStack(String color) {
        LinkedList<Piece> stack = new LinkedList<Piece>();
        if (color.equals("white")) stack = whiteOut;
        else if (color.equals("black")) stack = blackOut;
        else;
        return stack.toArray(new Piece[0]);
    }

    // returns true if the king of said color is in Check
    public boolean isChecked(String color) {
        String oppColor = color.equals("white") ? "black" : "white";
        String kingsPosition = board.getSquareOf(color, 'K').getPosition();
        Boolean queenCap = findRelevantPieces(oppColor, 'Q', kingsPosition).length != 0;
        Boolean knightCap = findRelevantPieces(oppColor, 'N', kingsPosition).length != 0;
        Boolean bishopCap = findRelevantPieces(oppColor, 'B', kingsPosition).length != 0;
        Boolean rookCap = findRelevantPieces(oppColor, 'R', kingsPosition).length != 0;
        Boolean pawnCap = false;
        Piece[] p = findRelevantPieces(oppColor, 'P', kingsPosition);
        if (p != null)
            for (int i = 0; i < p.length; i++)
                if (p[i].getPosition().charAt(0) != kingsPosition.charAt(0))
                    pawnCap = true;
        if (queenCap || knightCap || bishopCap || rookCap || pawnCap) return true;
        else return false;
    }

    // returns if the king of said color would be in check if it was at said position.
    public boolean checkChecked(String color, String position) {
        String oppColor = color.equals("white") ? "black" : "white";
        String kingsPosition = position;
        Boolean queenCap = findRelevantPieces(oppColor, 'Q', kingsPosition).length != 0;
        Boolean knightCap = findRelevantPieces(oppColor, 'N', kingsPosition).length != 0;
        Boolean bishopCap = findRelevantPieces(oppColor, 'B', kingsPosition).length != 0;
        Boolean rookCap = findRelevantPieces(oppColor, 'R', kingsPosition).length != 0;
        Boolean pawnCap = false;
        Piece[] p = findRelevantPieces(oppColor, 'P', kingsPosition);
        if (p != null)
            for (int i = 0; i < p.length; i++)
                if (p[i].getPosition().charAt(0) != kingsPosition.charAt(0))
                    pawnCap = true;
        if (queenCap || knightCap || bishopCap || rookCap || pawnCap) return true;
        else return false;
    }

    // checks if the parsed string will result in a legal move.
    public boolean checkMovementLegality(ParseFlags pf) {
        boolean isLegal = true;
        if (pf.getKingsideCastle()) {
            if (isChecked(pf.getColor())) {
                isLegal = false;
            }
            else if (castleHasMoved[(pf.getColor().equals("white") ? 0 : 1)][1]
                     || castleHasMoved[(pf.getColor().equals("white") ? 0 : 1)][2]) {
                isLegal = false;
            }
            else if (board.getPieceAt('f', (pf.getColor().equals("white") ? '1' : '8')) != null
                     || board.getPieceAt('g', (pf.getColor().equals("white") ? '1' : '8')) != null) {
                isLegal = false;
            }
            else {
                char[] position = {'f', (pf.getColor().equals("white") ? '1' : '8')};
                if (checkChecked(pf.getColor(), new String(position))) {
                    isLegal = false;
                }
                position[0] = 'g';
                if (checkChecked(pf.getColor(), new String(position))) {
                    isLegal = false;
                }
            }
        } else if (pf.getQueensideCastle()) {
            if (isChecked(pf.getColor())) {
                isLegal = false;
            }
            else if (castleHasMoved[(pf.getColor().equals("white") ? 0 : 1)][1]
                     || castleHasMoved[(pf.getColor().equals("white") ? 0 : 1)][0]) {
                isLegal = false;
            }
            else if (board.getPieceAt('c', (pf.getColor().equals("white") ? '1' : '8')) != null
                     || board.getPieceAt('d', (pf.getColor().equals("white") ? '1' : '8')) != null) {
                isLegal = false;
            }
            else {
                char[] position = {'c', (pf.getColor().equals("white") ? '1' : '8')};
                if (checkChecked(pf.getColor(), new String(position))) {
                    isLegal = false;
                }
                position[0] = 'd';
                if (checkChecked(pf.getColor(), new String(position))) {
                    isLegal = false;
                }
            }
        } else {
            Piece[] p = findRelevantPieces(pf.getColor(), pf.getPieceType(), pf.getEndPosition());
            if (p.length == 0)
                isLegal = false;
            if (!pf.getCapture() && board.getPieceAt(pf.getEndPosition()) != null) isLegal = false;
            else {
                if (pf.getPieceType() == 'P') {
                    isLegal = false;
                    if (pf.getCapture()) {
                        for (int i = 0; i < p.length; i++) {
                            if (p[i].getPosition().indexOf(pf.getEndPosition().charAt(0)) != 0) isLegal = true;
                        }
                    } else {
                        for (int i = 0; i < p.length; i++) {
                            if (p[i].getPosition().indexOf(pf.getEndPosition().charAt(0)) != -1) isLegal = true;
                        }
                    }
                }
            }
        }
        return isLegal;
    }

    public boolean canMoveTo(Piece p, String endPosition) {
        char type = p.getType();
        String color = p.getColor();
        Piece x[] = findRelevantPieces(color, type, endPosition);
        if (x.length != 0) {
            if (type == 'P') {
                for (int i = 0; i < x.length; i++)
                    if (x[i].getPosition().charAt(0) == endPosition.charAt(0))
                        x[i] = null;
            }
            boolean b = false;
            for (int i = 0; i < x.length; i++) {
                if (p == x[i]) {
                    b = true;
                }
            }
            return b;
        }
        else return false;
    }
}


/*
 *                  board.square[1][7] -> bitmap[0] & (byte)Math.pow(2, 6)
 */
