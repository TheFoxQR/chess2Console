package com.thefoxqr.chess2;

public class King extends Piece
{
    public King(Piece p) {
        this.type = p.getType();
        this.isWhite = p.getColor().equals("white") ? true : false;
        this.cost  = p.getCost();
        this.position = p.getSquare();
    }

    public King(Square pos, String color) {
        this.type = 'K';
        this.isWhite = color == "white" ? true : false;
        this.cost = 16;
        this.position = pos;
    }

    public boolean canMove(Square endPosition) {
        String end = endPosition.getPosition();
        String start = position.getPosition();

        if ((end.charAt(0) - start.charAt(0)) <= 1 && (end.charAt(1) - start.charAt(1)) <= 1) return true;
        else return false;
    }
}
