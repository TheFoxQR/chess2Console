package com.thefoxqr.chess2;
import java.io.Serializable;

public class DataDump implements Serializable {
    private String moveType;
    private boolean checked = false;
    private boolean captured = false;
    private String playedBy = "white";
    private char[][] board;
    private char[] whiteOut;
    private char[] blackOut;

    public void setMoveType(String type) {
        this.moveType = type;
    }

    public void setChecked(boolean checkState) {
        this.checked = checkState;
    }

    public void setCaptured(boolean captureState) {
        this.captured = captureState;
    }

    public void setPlayedBy(String color) {
        this.playedBy = color;
    }

    public void setBoard(char[][] boardState) {
        this.board = boardState;
    }

    public void setOutStack(char[] outStack, String color) {
        if (color.equals("white")) {
            this.whiteOut = outStack;
        } else {
            this.blackOut = outStack;
        }
    }

    public void setOutStack(Piece[] outStack, String color) {
        char[] c = new char[outStack.length];
        for (int i = 0; i < c.length; i++) c[i] = outStack[i].getType();
        if (color.equals("white")) {
            this.whiteOut = c;
        } else {
            this.blackOut = c;
        }
    }

    public String getMoveType() {
        return this.moveType;
    }

    public boolean getChecked() {
        return this.checked;
    }

    public boolean getCaptured() {
        return this.captured;
    }

    public String getPlayedBy() {
        return this.playedBy;
    }

    public char[][] getBoard() {
        return this.board;
    }

    public char[] getOutStack(String color) {
        if (color.equals("white"))
            return this.whiteOut;
        else if (color.equals("black"))
            return this.blackOut;
        else
            return new String("Error - Illegal color").toCharArray();
    }

}
