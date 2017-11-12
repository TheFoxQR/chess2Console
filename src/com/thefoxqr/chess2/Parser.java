package com.thefoxqr.chess2;

public interface Parser {
    public abstract void setGame(Game game);
    public abstract ParseFlags getParseFlags();
    public abstract void readNextMove();
    public abstract Move fillNextSubMove(Move xmove);
    public abstract boolean endOfInput();
    public abstract void goBack();
}
