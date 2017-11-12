package com.thefoxqr.chess2;
import java.util.LinkedList;

public class Recorder {
    private LinkedList<Move> moveList = new LinkedList<Move>();

    public void addMove(Move move) {
        moveList.add(move);
    }

    public Move[] getMoveList() {
        return moveList.toArray(new Move[0]);
    }

    public Move getLastMove() {
        return moveList.getLast();
    }
}
