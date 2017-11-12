package com.thefoxqr.chess2;

public class Rook extends Piece
{
    public Rook(Piece p) {
        this.type = p.getType();
        this.isWhite = p.getColor().equals("white") ? true : false;
        this.cost  = p.getCost();
        this.position = p.getSquare();
    }

    public Rook(Square pos, String color) {
        this.type = 'R';
        this.isWhite = color == "white" ? true : false;
        this.cost = 5;
        this.position = pos;
    }

    public boolean canMove(Square endPosition) {
        String end = endPosition.getPosition();
        String start = position.getPosition();

        if (start.charAt(0) == end.charAt(0)) return true;
        else if (start.charAt(1) == end.charAt(1)) return true;
        else return false;
    }
}
