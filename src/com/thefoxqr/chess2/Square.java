package com.thefoxqr.chess2;

public class Square
{
    private char file;
    private char rank;
    private Piece piece;
    private boolean isWhite;

    public Square(Square sq) {
        this.file = sq.getPosition().charAt(0);
        this.rank = sq.getPosition().charAt(1);
        this.piece = sq.getPiece();
        this.isWhite = sq.getColor() == "white" ? true : false;
    }

    public Square(char file, char rank, String color) {
        this.file = file;
        this.rank = rank;
        this.piece = null;
        this.isWhite = color == "white" ? true : false;
    }

    public boolean setPosition(String pos) {
        char f = pos.charAt(0);
        char r = pos.charAt(1);
        return setPosition(f, r);
    }

    public boolean setPosition(char file, char rank) {
        if (file >= 'a' && file <= 'h' && rank >= '1' && rank <= '8') {
            this.file = file;
            this.rank = rank;
        }
        else return false;
        return true;
    }

    public String getPosition() {
        char[] x = new char[2];
        x[0] = this.file;
        x[1] = this.rank;
        return new String(x);
    }

    public boolean setPiece(Piece p) {
        this.piece = p;
        return true;
    }

    public void resetPiece() {
        this.piece = null;
    }

    public Piece getPiece() {
        return this.piece;
    }

    public char getPieceType() {
        if (this.piece == null) return ' ';
        return piece.getType();
    }

    public String getPieceColor() {
        if (this.piece == null) return " ";
        return piece.getColor();
    }

    public String getColor() {
        if (isWhite) return "white";
        else return "black";
    }
}
