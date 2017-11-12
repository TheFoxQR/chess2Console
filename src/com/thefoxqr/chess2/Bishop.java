package com.thefoxqr.chess2;

public class Bishop extends Piece
{
    public Bishop(Piece p) {
        this.type = p.getType();
        this.isWhite = p.getColor().equals("white") ? true : false;
        this.cost  = p.getCost();
        this.position = p.getSquare();
    }

    public Bishop(Square pos, String color) {
        this.type = 'B';
        this.isWhite = color == "white" ? true : false;
        this.cost = 3;
        this.position = pos;
    }

    public boolean canMove(Square endPosition) {
        String end = endPosition.getPosition();
        String start = position.getPosition();

        if (((int)start.charAt(0) + (int)start.charAt(1)) == ((int)end.charAt(0) + (int)end.charAt(1))) return true;
        else if (((int)start.charAt(0) - (int)start.charAt(1)) == ((int)end.charAt(0) - (int)end.charAt(1))) return true;
        else return false;
    }
}
