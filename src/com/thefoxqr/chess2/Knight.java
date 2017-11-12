package com.thefoxqr.chess2;

public class Knight extends Piece
{
    public Knight(Piece p) {
        this.type = p.getType();
        this.isWhite = p.getColor().equals("white") ? true : false;
        this.cost  = p.getCost();
        this.position = p.getSquare();
    }

    public Knight(Square pos, String color) {
        this.type = 'N';
        this.isWhite = color == "white" ? true : false;
        this.cost = 3;
        this.position = pos;
    }

    public boolean canMove(Square endPosition) {
        String end = endPosition.getPosition();
        String start = position.getPosition();

        if (Math.abs(start.charAt(0) - end.charAt(0)) == 1 && Math.abs(start.charAt(1) - end.charAt(1)) == 2) return true;
        else if (Math.abs(start.charAt(0) - end.charAt(0)) == 2 && Math.abs(start.charAt(1) - end.charAt(1)) == 1) return true;
        else return false;
    }
}
