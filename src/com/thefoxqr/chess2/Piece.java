package com.thefoxqr.chess2;

public abstract class Piece
{
    protected Square position;
    protected boolean isWhite;
    protected int cost;
    protected char type;

    public boolean setColor(String color) {
        if (color.equals("white") || color.equals("black")) isWhite = color.equals("white") ? true : false;
        else return false;
        return true;
    }

    public abstract boolean canMove(Square endPosition);

    public void move(Square endPosition) {
        if (canMove(endPosition)) this.position = endPosition;
    }

    public void put(Square endPosition) {
        this.position = endPosition;
    }

    public char getType() {
        return this.type;
    }

    public String getColor() {
        if (isWhite) return "white";
        else return "black";
    }

    public void remove() {
        this.position = null;
    }

    public int getCost() {
        return this.cost;
    }

    public Square getSquare() {
        return this.position;
    }

    public String getPosition() {
        return this.position.getPosition();
    }
}
