package com.thefoxqr.chess2;

public class Move
{
    private String comment;
    private String type;
    private SubMove white;
    private SubMove black;

    public void setComment(String s) {
        this.comment = s;
    }

    public void setType(String s) {
        this.type = s;
    }

    public void setWhiteMove(SubMove submove) {
        white = submove;
    }

    public void setBlackMove(SubMove submove) {
        black = submove;
    }

    public SubMove getWhiteMove() {
        return white;
    }

    public SubMove getBlackMove() {
        return black;
    }

    public String getComment() {
        return this.comment;
    }

    public String getType() {
        return this.type;
    }
}
