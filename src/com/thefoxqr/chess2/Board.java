package com.thefoxqr.chess2;

public class Board
{
    private Square[][] square = new Square[8][8];

    public Board() {
        String color = "black";
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                square[i][j] = new Square((char)('a' + i), (char)('1' + j), color);
                color = color == "white" ? "black" : "white";
            }
            color = color == "white" ? "black" : "white";
        }
    }

    public char[][] getAbstraction() {
        char[][] board = new char[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = square[i][j].getPiece();
                if (p != null) {
                    board[i][j] = p.getType();
                    if (p.getColor().equals("white")) board[i][j] += 32;
                }
            }
        }
        return board;
    }

    public Square getSquare(String position) {
        char file = (char)(position.charAt(0) - 'a');
        char rank = (char)(position.charAt(1) - '1');
        return square[file][rank];
    }

    public Square getSquare(char f, char r) {
        char file = (char)(f - 'a');
        char rank = (char)(r - '1');
        return square[file][rank];
    }

    public Piece getPieceAt(String position) {
        char file = (char)(position.charAt(0) - 'a');
        char rank = (char)(position.charAt(1) - '1');
        return square[file][rank].getPiece();
    }

    public Piece getPieceAt(char f, char r) {
        char file = (char)(f - 'a');
        char rank = (char)(r - '1');
        return square[file][rank].getPiece();
    }

    public Square getSquareOf(String color, char pieceType) {
        Square sq = null;
        int i = 0, j = 0;
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                if (color.equals(square[i][j].getPieceColor()) && pieceType == square[i][j].getPieceType()) sq = square[i][j];
            }
        }
        return sq;
    }

    public byte[] getBitMap(String color, char option) {
        byte[] bitmap = new byte[8];
        if (color.equals("all")) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (square[i][j].getPieceColor().equals("white") && (option == square[i][j].getPieceType() || option == 'A')) {
                        bitmap[7 - j] += (byte)(Math.pow(2, i));
                    }
                }
            }
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (square[i][j].getPieceColor().equals("black") && (option == square[i][j].getPieceType() || option == 'A')) {
                        bitmap[7 - j] += (byte)(Math.pow(2, i));
                    }
                }
            }
        }
        else {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (color.equals(square[i][j].getPieceColor()) && (option == square[i][j].getPieceType() || option == 'A')) {
                        bitmap[7 - j] += (byte)(Math.pow(2, i));
                    }
                }
            }
        }
        return bitmap;
    }

    public byte[] makeBitMap(String color, char option, String position) {
        byte[] bitmap = new byte[8];
        String oppColor = color.equals("white") ? "black" : "white";
        byte file = (byte)position.charAt(0);
        byte rank = (byte)position.charAt(1);
        file = (byte)(file - 'a');
        rank = (byte)(0x07 - (byte)(rank - '1'));
        switch (option) {
            case 'N':
                if (file - 0x02 >= 0x00 && rank + 0x01 <= 0x07) bitmap[rank + 0x01] += (byte)Math.pow(2, file - 0x02);
                if (file + 0x02 <= 0x07 && rank + 0x01 <= 0x07) bitmap[rank + 0x01] += (byte)Math.pow(2, file + 0x02);
                if (file - 0x02 >= 0x00 && rank - 0x01 >= 0x00) bitmap[rank - 0x01] += (byte)Math.pow(2, file - 0x02);
                if (file + 0x02 <= 0x07 && rank - 0x01 >= 0x00) bitmap[rank - 0x01] += (byte)Math.pow(2, file + 0x02);
                if (file - 0x01 >= 0x00 && rank + 0x02 <= 0x07) bitmap[rank + 0x02] += (byte)Math.pow(2, file - 0x01);
                if (file + 0x01 <= 0x07 && rank + 0x02 <= 0x07) bitmap[rank + 0x02] += (byte)Math.pow(2, file + 0x01);
                if (file - 0x01 >= 0x00 && rank - 0x02 >= 0x00) bitmap[rank - 0x02] += (byte)Math.pow(2, file - 0x01);
                if (file + 0x01 <= 0x07 && rank - 0x02 >= 0x00) bitmap[rank - 0x02] += (byte)Math.pow(2, file + 0x01);
                break;
            case 'K':
                if (file - 0x01 >= 0x00 && rank + 0x01 <= 0x07) bitmap[rank + 0x01] += (byte)Math.pow(2, file - 0x01);
                if (file + 0x01 <= 0x07 && rank + 0x01 <= 0x07) bitmap[rank + 0x01] += (byte)Math.pow(2, file + 0x01);
                if (file - 0x01 >= 0x00 && rank - 0x01 >= 0x00) bitmap[rank - 0x01] += (byte)Math.pow(2, file - 0x01);
                if (file + 0x01 <= 0x07 && rank - 0x01 >= 0x00) bitmap[rank - 0x01] += (byte)Math.pow(2, file + 0x01);
                if (rank + 0x01 <= 0x07) bitmap[rank + 0x01] += (byte)Math.pow(2, file);
                if (rank - 0x01 >= 0x00) bitmap[rank - 0x01] += (byte)Math.pow(2, file);
                if (file - 0x01 >= 0x00) bitmap[rank] += (byte)Math.pow(2, file - 0x01);
                if (file + 0x01 <= 0x07) bitmap[rank] += (byte)Math.pow(2, file + 0x01);
                break;
            case 'Q':
                color = color.equals("white") ? "black" : "white";
                oppColor = color.equals("white") ? "black" : "white";
                for (byte i = 0x01; rank + i <= 0x07; i++) {
                    if (square[file][0x07 - (rank + i)].getPieceColor().equals(color)) break;
                    else {
                        bitmap[rank + i] += (byte)Math.pow(2, file);
                        if (square[file][0x07 - (rank + i)].getPieceColor().equals(oppColor)) break;
                    }
                }
                for (byte i = 0x01; rank - i >= 0x00; i++) {
                    if (square[file][0x07 - (rank - i)].getPieceColor().equals(color)) break;
                    else {
                        bitmap[rank - i] += (byte)Math.pow(2, file);
                        if (square[file][0x07 - (rank - i)].getPieceColor().equals(oppColor)) break;
                    }
                }
                for (byte i = 0x01; file + i <= 0x07; i++) {
                    if (square[file + i][0x07 - (rank)].getPieceColor().equals(color)) break;
                    else {
                        bitmap[rank] += (byte)Math.pow(2, file + i);
                        if (square[file + i][0x07 - (rank)].getPieceColor().equals(oppColor)) break;
                    }
                }
                for (byte i = 0x01; file - i >= 0x00; i++) {
                    if (square[file - i][0x07 - (rank)].getPieceColor().equals(color)) break;
                    else {
                        bitmap[rank] += (byte)Math.pow(2, file - i);
                        if (square[file - i][0x07 - (rank)].getPieceColor().equals(oppColor)) break;
                    }
                }
                for (byte i = 0x01; file - i >= 0x00 && rank + i <= 0x07; i++) {
                    if (square[file - i][0x07 - (rank + i)].getPieceColor().equals(color)) break;
                    else {
                        bitmap[rank + i] += (byte)Math.pow(2, file - i);
                        if (square[file - i][0x07 - (rank + i)].getPieceColor().equals(oppColor)) break;
                    }
                }
                for (byte i = 0x01; file + i <= 0x07 && rank + i <= 0x07; i++) {
                    if (square[file + i][0x07 - (rank + i)].getPieceColor().equals(color)) break;
                    else {
                        bitmap[rank + i] += (byte)Math.pow(2, file + i);
                        if (square[file + i][0x07 - (rank + i)].getPieceColor().equals(oppColor)) break;
                    }
                }
                for (byte i = 0x01; file - i >= 0x00 && rank - i >= 0x00; i++) {
                    if (square[file - i][0x07 - (rank - i)].getPieceColor().equals(color)) break;
                    else {
                        bitmap[rank - i] += (byte)Math.pow(2, file - i);
                        if (square[file - i][0x07 - (rank - i)].getPieceColor().equals(oppColor)) break;
                    }
                }
                for (byte i = 0x01; file + i <= 0x07 && rank - i >= 0x00; i++) {
                    if (square[file + i][0x07 - (rank - i)].getPieceColor().equals(color)) break;
                    else {
                        bitmap[rank - i] += (byte)Math.pow(2, file + i);
                        if (square[file + i][0x07 - (rank - i)].getPieceColor().equals(oppColor)) break;
                    }
                }
                break;
            case 'R':
                color = color.equals("white") ? "black" : "white";
                oppColor = color.equals("white") ? "black" : "white";
                for (byte i = 0x01; rank + i <= 0x07; i++) {
                    if (square[file][0x07 - (rank + i)].getPieceColor().equals(color)) break;
                    else {
                        bitmap[rank + i] += (byte)Math.pow(2, file);
                        if (square[file][0x07 - (rank + i)].getPieceColor().equals(oppColor)) break;
                    }
                }
                for (byte i = 0x01; rank - i >= 0x00; i++) {
                    if (square[file][0x07 - (rank - i)].getPieceColor().equals(color)) break;
                    else {
                        bitmap[rank - i] += (byte)Math.pow(2, file);
                        if (square[file][0x07 - (rank - i)].getPieceColor().equals(oppColor)) break;
                    }
                }
                for (byte i = 0x01; file + i <= 0x07; i++) {
                    if (square[file + i][0x07 - (rank)].getPieceColor().equals(color)) break;
                    else {
                        bitmap[rank] += (byte)Math.pow(2, file + i);
                        if (square[file + i][0x07 - (rank)].getPieceColor().equals(oppColor)) break;
                    }
                }
                for (byte i = 0x01; file - i >= 0x00; i++) {
                    if (square[file - i][0x07 - (rank)].getPieceColor().equals(color)) break;
                    else {
                        bitmap[rank] += (byte)Math.pow(2, file - i);
                        if (square[file - i][0x07 - (rank)].getPieceColor().equals(oppColor)) break;
                    }
                }
                break;
            case 'B':
                color = color.equals("white") ? "black" : "white";
                oppColor = color.equals("white") ? "black" : "white";
                for (byte i = 0x01; file - i >= 0x00 && rank + i <= 0x07; i++) {
                    if (square[file - i][0x07 - (rank + i)].getPieceColor().equals(color)) break;
                    else {
                        bitmap[rank + i] += (byte)Math.pow(2, file - i);
                        if (square[file - i][0x07 - (rank + i)].getPieceColor().equals(oppColor)) break;
                    }
                }
                for (byte i = 0x01; file + i <= 0x07 && rank + i <= 0x07; i++) {
                    if (square[file + i][0x07 - (rank + i)].getPieceColor().equals(color)) break;
                    else {
                        bitmap[rank + i] += (byte)Math.pow(2, file + i);
                        if (square[file + i][0x07 - (rank + i)].getPieceColor().equals(oppColor)) break;
                    }
                }
                for (byte i = 0x01; file - i >= 0x00 && rank - i >= 0x00; i++) {
                    if (square[file - i][0x07 - (rank - i)].getPieceColor().equals(color)) break;
                    else {
                        bitmap[rank - i] += (byte)Math.pow(2, file - i);
                        if (square[file - i][0x07 - (rank - i)].getPieceColor().equals(oppColor)) break;
                    }
                }
                for (byte i = 0x01; file + i <= 0x07 && rank - i >= 0x00; i++) {
                    if (square[file + i][0x07 - (rank - i)].getPieceColor().equals(color)) break;
                    else {
                        bitmap[rank - i] += (byte)Math.pow(2, file + i);
                        if (square[file + i][0x07 - (rank - i)].getPieceColor().equals(oppColor)) break;
                    }
                }
                break;
            case 'P':
                if (color.equals("white")) {
                    if (rank + 0x01 <= 0x07) bitmap[rank + 0x01] += (byte)Math.pow(2, file);
                    if (rank + 0x01 <= 0x07) bitmap[rank + 0x01] += (byte)Math.pow(2, file - 0x01);
                    if (rank + 0x01 <= 0x07) bitmap[rank + 0x01] += (byte)Math.pow(2, file + 0x01);
                    if (rank == 0x04) bitmap[rank + 0x02] += (byte)Math.pow(2, file);
                }
                else {
                    if (rank - 0x01 >= 0x00) bitmap[rank - 0x01] += (byte)Math.pow(2, file);
                    if (rank - 0x01 >= 0x00) bitmap[rank - 0x01] += (byte)Math.pow(2, file - 0x01);
                    if (rank - 0x01 >= 0x00) bitmap[rank - 0x01] += (byte)Math.pow(2, file + 0x01);
                    if (rank == 0x03) bitmap[rank - 0x02] += (byte)Math.pow(2, file);
                }
                break;
            default:
        }
        return bitmap;
    }
}



/*
 *                        i-j
 *                  byte        7  6  5  4  3  2  1  0
 *                   0         07 17 27 37 47 57 67 77
 *                   1         06 16 26 36 46 56 66 76
 *                   2         05 15 25 35 45 55 65 75
 *                   3         04 14 24 34 44 54 64 74
 *                   4         03 13 23 33 43 53 63 73
 *                   5         02 12 22 32 42 52 62 72
 *                   6         01 11 21 31 41 51 61 71
 *                   7         00 10 20 30 40 50 60 70
 */
