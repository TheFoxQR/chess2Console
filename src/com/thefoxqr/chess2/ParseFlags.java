package com.thefoxqr.chess2;

public class ParseFlags {
    private boolean isCheck = false;
    private boolean isCapture = false;
    private boolean isKCastle = false;
    private boolean isQCastle = false;
    private boolean isResult = false;
    private boolean isAmbigous = false;
    private boolean isPromotion = false;
    private String color = "";
    private char promotedTo = '\0';
    private String clarification = null;
    private char pieceType = '\0';
    private String  endPosition   = null;

    public void reset() {
        isCheck = false;
        isCapture = false;
        isKCastle = false;
        isQCastle = false;
        isResult = false;
        isAmbigous = false;
        isPromotion = false;
        color = "";
        promotedTo = '\0';
        clarification = null;
        pieceType = '\0';
        endPosition = null;
    }

    public void setColor(String c) {
        this.color = new String(c);
    }

    public String getColor() {
        return this.color;
    }

    public void setPromotion(boolean b) {
        this.isPromotion = b;
    }

    public void setPromotedTo(char c) {
        this.promotedTo = c;
    }

    public void setPieceType(char t) {
        this.pieceType = t;
    }

    public void setEndPosition(String s) {
        this.endPosition = new String(s);
    }

    public void setClarification(String s) {
        this.clarification = s;
    }

    public void setCheck(boolean bool) {
        this.isCheck = bool;
    }

    public void setCapture(boolean bool) {
        this.isCapture = bool;
    }

    public void setKingsideCastle(boolean bool) {
        this.isKCastle = bool;
    }

    public void setQueensideCastle(boolean bool) {
        this.isQCastle = bool;
    }

    public void setResult(boolean bool) {
        this.isResult = bool;
    }

    public void setAmbiguity(boolean bool) {
        this.isAmbigous = bool;
    }

    public boolean getCheck() {
        return this.isCheck;
    }

    public boolean getCapture() {
        return this.isCapture;
    }

    public boolean getKingsideCastle() {
        return this.isKCastle;
    }

    public boolean getQueensideCastle() {
        return this.isQCastle;
    }

    public boolean getResult() {
        return this.isResult;
    }

    public boolean getAmbiguity() {
        return this.isAmbigous;
    }

    public String getClarification() {
        return this.clarification;
    }

    public boolean getPromotion() {
        return this.isPromotion;
    }

    public char getPromotedTo() {
        return this.promotedTo;
    }

    public char getPieceType() {
        return this.pieceType;
    }

    public boolean getCastle() {
        return (isKCastle || isQCastle);
    }

    public String getEndPosition() {
        return this.endPosition;
    }
}
