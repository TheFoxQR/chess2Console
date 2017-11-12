package com.thefoxqr.chess2;

public class Pawn extends Piece
{
    public Pawn(Piece p) {
        this.type = p.getType();
        this.isWhite = p.getColor().equals("white") ? true : false;
        this.cost  = p.getCost();
        this.position = p.getSquare();
    }

    public Pawn(Square pos, String color) {
        this.type = 'P';
        this.isWhite = color == "white" ? true : false;
        this.cost = 1;
        this.position = pos;
    }

    public boolean canMove(Square endPosition) {
        String end = endPosition.getPosition();
        String start = position.getPosition();

        if (isWhite) {
            if ((end.charAt(0) - start.charAt(0)) == 1 && end.charAt(1) == start.charAt(1)) return true;
            else return false;
        }
        else {
            if ((start.charAt(0) - end.charAt(0)) == 1 && end.charAt(1) == start.charAt(1)) return true;
            else return false;
        }
    }
}
