package com.thefoxqr.chess2;

public class SubMove
{
    private Piece piece;
    private Square square;
    private char[] type;

    public SubMove(Piece p, Square s, char[] option) {
        this.type = option;
        switch (type[0]) {
            case '^':
            case '+':
            case 'x':
            case '*':
                makeCopy(p);
                makeCopy(s);
                break;
            case '-':
            case 'i':
            case 'e':
            case 'c':
                this.piece = null;
                this.square = null;
                break;
        }
    }

    public void setPiece(Piece p) {
        this.piece = p;
    }

    public void setSquare(Square s) {
        this.square = s;
    }

    public char[] getType() {
        return this.type;
    }

    public void setType(char[] c) {
        type[0] = c[0];
        type[1] = c[1];
    }

    public void setType(String c) {
        type[0] = c.charAt(0);
        type[1] = c.charAt(1);
    }

    public Piece getPiece() {
        return this.piece;
    }

    public Square getSquare() {
        return this.square;
    }

    public void makeCopy(Piece p) {
        switch (p.getType()) {
                case 'K':
                    piece = new King(p);
                    break;
                case 'Q':
                    piece = new Queen(p);
                    break;
                case 'R':
                    piece = new Rook(p);
                    break;
                case 'B':
                    piece = new Bishop(p);
                    break;
                case 'N':
                    piece = new Knight(p);
                    break;
                default:
                    piece = new Pawn(p);
                    break;
        }
    }

    public void makeCopy(Square s) {
        this.square = new Square(s);
    }
}


/*
 *          Type Dictionary : the first character decides what special action the submove constructor has to take. Combined, they denote actions to
 *                              taken at higher levels.
 *              - -      - no move
 *              ^ -      - no special move
 *              ^ [qnbr] - just promotion
 *              x -      - just capture
 *              x [qnbr] - capture and promotion
 *              + -      - just check
 *              + [qnbr] - check and promotion
 *              * -      - just check and capture
 *              * [qnbr] - check, capture, and promotion
 *              c k      - kingside castling
 *              c q      - queenside castling
 *              i i      - illegal input
 *              i m      - illegal because specified piece cannot move to specified place
 *              i r      - illegal because it left king in check at the end of turn.
 *              e w      - end of game, white wins.
 *              e b      - end of game, black wins.
 *              e s      - end of game, stalemate.
 */
