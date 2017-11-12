package com.thefoxqr.chess2;

public class Queen extends Piece
{
    public Queen(Piece p) {
        this.type = p.getType();
        this.isWhite = p.getColor().equals("white") ? true : false;
        this.cost  = p.getCost();
        this.position = p.getSquare();
    }

    public Queen(Square pos, String color) {
        this.type = 'Q';
        this.isWhite = color == "white" ? true : false;
        this.cost = 16;
        this.position = pos;
    }

    public boolean canMove(Square endPosition) {
        String end = endPosition.getPosition();
        String start = position.getPosition();

        if (start.charAt(0) == end.charAt(0)) return true;
        else if (start.charAt(1) == end.charAt(1)) return true;
        else if (((int)start.charAt(0) + (int)start.charAt(1)) == ((int)end.charAt(0) + (int)end.charAt(1))) return true;
        else if (((int)start.charAt(0) - (int)start.charAt(1)) == ((int)end.charAt(0) - (int)end.charAt(1))) return true;
        else return false;
    }
}
