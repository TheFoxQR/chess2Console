package com.thefoxqr.chess2;
import java.util.LinkedList;

public class MoveGenerator {
    private Game game;
    private Piece[] stack;
    private boolean[] canMove;

    public MoveGenerator(Game g) {
        this.game = g;
    }

    public boolean hasLegalMoves(String color) {
        stack = game.getInStack(color);
        canMove = new boolean[stack.length];
        for (int i = 0; i < stack.length; i++) canMove[i] = true;
        boolean hasLegalMoves = false;
        // test each piece for all available moves. first go
        // check the king, and all pinned pieces. then, check
        // all the remaining pieces for any movable pieces.
        checkKing();
        checkPins();
        checkCastles(color);
        checkRest();
        if (game.isChecked(color)) {
            // find hotspots.
            String[] hotspots = findHotspots(color);
            // for (String s : hotspots) System.out.print(" H: " + s);;
            // figure out if any piece can move to hotspots.
            String oppColor = (color.equals("white"))? "black": "white";
            Piece p;
            for (int i = 0; i < canMove.length; i++) {
                if (canMove[i]) {
                    // canMove[i] = false;
                    p = stack[i];
                    canMove[i] = game.canMoveTo(p, hotspots[0]);
                    for (int j = 1; j < hotspots.length; j++) {
                        canMove[i] = canMove[i] || game.canMoveTo(p, hotspots[j]);
                    }
                }
            }
        }
        // System.out.println("Stack length: " + stack.length);
        // for (Piece p : stack) System.out.print(" " + p.getType() + p.getPosition());
        // System.out.println();
        for (boolean b : canMove) {
            // System.out.print(" " + (b? "1": "0") + "  ");
            hasLegalMoves = hasLegalMoves || b;
        }
        // System.out.println();
        return hasLegalMoves;
    }

    // check if the king can move.
    private boolean checkKing() {
        Piece k = stack[0], p;
        boolean canKingMove = false;
        int index = 0;
        for (index = 0; index < stack.length; index++) {
            if (stack[index].getType() == 'K') {
                k = stack[index];
                break;
            }
        }
        String pos = k.getPosition();
        char[] rankAndFile = new char[2];
        char f = pos.charAt(0), r = pos.charAt(1);
        boolean up = (r < '8') ? true : false, down = (r > '1') ? true : false,
                left = (f > 'a') ? true : false, right = (f < 'h') ? true : false;

        // up represents the square directly above the king.
        if (up) {
            rankAndFile[0] = f;
            rankAndFile[1] = (char)(r + 1);
            p = game.board.getPieceAt(rankAndFile[0], rankAndFile[1]);
            // if square is empty or has a piece not of the players color, check
            // if the king would be checked there.
            if (p == null || !p.getColor().equals(k.getColor())) canKingMove = !game.checkChecked(k.getColor(), new String(rankAndFile));
        }
        if (down && !canKingMove) {
            rankAndFile[0] = f;
            rankAndFile[1] = (char)(r - 1);
            p = game.board.getPieceAt(rankAndFile[0], rankAndFile[1]);
            if (p == null || !p.getColor().equals(k.getColor())) canKingMove = !game.checkChecked(k.getColor(), new String(rankAndFile));
        }
        if (left && !canKingMove) {
            rankAndFile[0] = (char)(f - 1);
            rankAndFile[1] = r;
            p = game.board.getPieceAt(rankAndFile[0], rankAndFile[1]);
            if (p == null || !p.getColor().equals(k.getColor())) canKingMove = !game.checkChecked(k.getColor(), new String(rankAndFile));
        }
        if (right && !canKingMove) {
            rankAndFile[0] = (char)(f + 1);
            rankAndFile[1] = r;
            p = game.board.getPieceAt(rankAndFile[0], rankAndFile[1]);
            if (p == null || !p.getColor().equals(k.getColor())) canKingMove = !game.checkChecked(k.getColor(), new String(rankAndFile));
        }
        if (up && left && !canKingMove) {
            rankAndFile[0] = (char)(f - 1);
            rankAndFile[1] = (char)(r + 1);
            p = game.board.getPieceAt(rankAndFile[0], rankAndFile[1]);
            if (p == null || !p.getColor().equals(k.getColor())) canKingMove = !game.checkChecked(k.getColor(), new String(rankAndFile));
        }
        if (up && right && !canKingMove) {
            rankAndFile[0] = (char)(f + 1);
            rankAndFile[1] = (char)(r + 1);
            p = game.board.getPieceAt(rankAndFile[0], rankAndFile[1]);
            if (p == null || !p.getColor().equals(k.getColor())) canKingMove = !game.checkChecked(k.getColor(), new String(rankAndFile));
        }
        if (down && left && !canKingMove) {
            rankAndFile[0] = (char)(f - 1);
            rankAndFile[1] = (char)(r - 1);
            p = game.board.getPieceAt(rankAndFile[0], rankAndFile[1]);
            if (p == null || !p.getColor().equals(k.getColor())) canKingMove = !game.checkChecked(k.getColor(), new String(rankAndFile));
        }
        if (down && right && !canKingMove) {
            rankAndFile[0] = (char)(f + 1);
            rankAndFile[1] = (char)(r - 1);
            p = game.board.getPieceAt(rankAndFile[0], rankAndFile[1]);
            if (p == null || !p.getColor().equals(k.getColor())) canKingMove = !game.checkChecked(k.getColor(), new String(rankAndFile));
        }
        canMove[index] = canKingMove;
        // System.out.println("canKingMove: " + canKingMove/* + ", up: " + up + ", down: " + down + ", left: " + left + ", right: " + right*/);
        return canKingMove;
    }

    // check for pinned pieces, and remove them from the stack.
    private boolean checkPins() {
        // find the king.
        Piece k = stack[0];
        for (int index = 0; index < stack.length; index++) {
            if (stack[index].getType() == 'K') {
                k = stack[index];
                break;
            }
        }
        String pos = k.getPosition();
        String color = k.getColor();
        boolean doPinsExist = false;
        // iterate through all the rook vectors. see xRayRook(String, String, int).
        doPinsExist = xRayRook(pos, color, 0);
        if (!doPinsExist) doPinsExist = xRayRook(pos, color, 1);
        if (!doPinsExist) doPinsExist = xRayRook(pos, color, 2);
        if (!doPinsExist) doPinsExist = xRayRook(pos, color, 3);
        // iterate through all the bishop vectors. see xRayBishop(String, int).
        if (!doPinsExist) doPinsExist = xRayBishop(pos, color, 0);
        if (!doPinsExist) doPinsExist = xRayBishop(pos, color, 1);
        if (!doPinsExist) doPinsExist = xRayBishop(pos, color, 2);
        if (!doPinsExist) doPinsExist = xRayBishop(pos, color, 3);
        // System.out.println("doPinsExist: " + doPinsExist);
        return !doPinsExist;
    }

    private boolean checkCastles(String color) {
        ParseFlags pf = new ParseFlags();
        pf.setColor(color);
        pf.setKingsideCastle(true);
        boolean canCastle = game.checkMovementLegality(pf);
        pf.reset();
        pf.setColor(color);
        pf.setQueensideCastle(true);
        canCastle = canCastle || game.checkMovementLegality(pf);
        // System.out.println("Can Castle: " + canCastle);
        return canCastle;
    }

    private boolean checkRest() {
        for (int i = 0; i < stack.length; i++) {
            if (canMove[i]) {
                Piece p = stack[i], x;
                String color = p.getColor();
                String oppColor = color.equals("white")? "black": "white";
                String position = p.getPosition();
                char f = position.charAt(0), r = position.charAt(1);
                switch (p.getType()) {
                    case 'P':
                        // uni-directional movement, direction depends on color.
                        // en-passant not covered.
                        int pawnInc = p.getColor().equals("white")? 1: -1;
                        x = game.board.getPieceAt(f, (char)(r + pawnInc));
                        if (x == null) canMove[i] = true;
                        else canMove[i] = false;
                        if (!canMove[i] && (char)(f + 1) <= 'h') {
                            x = game.board.getPieceAt((char)(f + 1), (char)(r + pawnInc));
                            if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        if (!canMove[i] && (char)(f - 1) >= 'a') {
                            x = game.board.getPieceAt((char)(f - 1), (char)(r + pawnInc));
                            if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        if (!canMove[i] && r == '2' && color.equals("white")) {
                            x = game.board.getPieceAt((char)(f + 1), (char)(r + (2 * pawnInc)));
                            if (x == null) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        if (!canMove[i] && r == '7' && color.equals("black")) {
                            x = game.board.getPieceAt((char)(f + 1), (char)(r + (2 * pawnInc)));
                            if (x == null) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        break;
                    case 'N':
                        // check all 8 squares, if any is null or has oppColor pieces, canMove = true.
                        char f1, r1;
                        f1 = (char)(f - 2); r1 = (char)(r - 1);
                        if (f1 >= 'a' && r1 >= '1') {
                            x = game.board.getPieceAt(f1, r1);
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        f1 = (char)(f - 1); r1 = (char)(r - 2);
                        if (!canMove[i] && f1 >= 'a' && r1 >= '1') {
                            x = game.board.getPieceAt(f1, r1);
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        f1 = (char)(f + 1); r1 = (char)(r + 2);
                        if (!canMove[i] && f1 <= 'h' && r1 <= '8') {
                            x = game.board.getPieceAt(f1, r1);
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        f1 = (char)(f + 2); r1 = (char)(r + 1);
                        if (!canMove[i] && f1 <= 'h' && r1 <= '8') {
                            x = game.board.getPieceAt(f1, r1);
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        f1 = (char)(f - 2); r1 = (char)(r + 1);
                        if (!canMove[i] && f1 >= 'a' && r1 <= '8') {
                            x = game.board.getPieceAt(f1, r1);
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        f1 = (char)(f - 1); r1 = (char)(r + 2);
                        if (!canMove[i] && f1 >= 'a' && r1 <= '8') {
                            x = game.board.getPieceAt(f1, r1);
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        f1 = (char)(f + 2); r1 = (char)(r - 1);
                        if (!canMove[i] && f1 <= 'h' && r1 >= '1') {
                            x = game.board.getPieceAt(f1, r1);
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        f1 = (char)(f + 1); r1 = (char)(r - 2);
                        if (!canMove[i] && f1 <= 'h' && r1 >= '1') {
                            x = game.board.getPieceAt(f1, r1);
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        break;
                    case 'R':
                        // check rook ray vectors. if any has null or oppColor pieces, canMove = true.
                        if ((char)(f + 1) <= 'h') {
                            x = game.board.getPieceAt((char)(f + 1), r);
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        if ((char)(f - 1) >= 'a') {
                            x = game.board.getPieceAt((char)(f - 1), r);
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        if ((char)(r + 1) <= '8') {
                            x = game.board.getPieceAt(f, (char)(r + 1));
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        if ((char)(r - 1) >= '1') {
                            x = game.board.getPieceAt(f, (char)(r - 1));
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        break;
                    case 'B':
                        // check bishop ray vectors. if any has null or oppColor pieces, canMove = true.
                        if ((char)(f + 1) <= 'h' && (char)(r + 1) <= '8') {
                            x = game.board.getPieceAt((char)(f + 1), (char)(r + 1));
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        if ((char)(f - 1) >= 'a' && (char)(r + 1) <= '8') {
                            x = game.board.getPieceAt((char)(f - 1), (char)(r + 1));
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        if ((char)(f + 1) <= 'h' && (char)(r - 1) >= '1') {
                            x = game.board.getPieceAt((char)(f + 1), (char)(r - 1));
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        if ((char)(f - 1) >= 'a' && (char)(r - 1) >= '1') {
                            x = game.board.getPieceAt((char)(f - 1), (char)(r - 1));
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        break;
                    case 'Q':
                        // check rook & bishop ray vectors. if any has null or oppColor pieces, canMove = true.
                        if ((char)(f + 1) <= 'h') {
                            x = game.board.getPieceAt((char)(f + 1), r);
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        if ((char)(f - 1) >= 'a') {
                            x = game.board.getPieceAt((char)(f - 1), r);
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        if ((char)(r + 1) <= '8') {
                            x = game.board.getPieceAt(f, (char)(r + 1));
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        if ((char)(r - 1) >= '1') {
                            x = game.board.getPieceAt(f, (char)(r - 1));
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        if ((char)(f + 1) <= 'h' && (char)(r + 1) <= '8') {
                            x = game.board.getPieceAt((char)(f + 1), (char)(r + 1));
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        if ((char)(f - 1) >= 'a' && (char)(r + 1) <= '8') {
                            x = game.board.getPieceAt((char)(f - 1), (char)(r + 1));
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        if ((char)(f + 1) <= 'h' && (char)(r - 1) >= '1') {
                            x = game.board.getPieceAt((char)(f + 1), (char)(r - 1));
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        if ((char)(f - 1) >= 'a' && (char)(r - 1) >= '1') {
                            x = game.board.getPieceAt((char)(f - 1), (char)(r - 1));
                            if (x == null) canMove[i] = true;
                            else if (x != null && x.getColor().equals(oppColor)) canMove[i] = true;
                            else canMove[i] = false;
                        }
                        break;
                    default:
                        // the king has already been checked.
                        break;
                }
            }
        }
        return true;
    }

    // go check the rook vectors. if the white pinned piece is a rook
    // or queen, it is not considered a pin. if the vector is 0 or 2,
    // pawns become a special case.
    //          0
    //      3   K   1
    //          2
    private boolean xRayRook(String position, String color, int vector) {
        int incF = 0, incR = 0;
        if (vector == 0) incR += 1;
        else if (vector == 2) incR -= 1;
        else;
        if (vector == 1) incF += 1;
        else if (vector == 3) incF -= 1;
        else;
        int vectorState = 2;
        Piece p, pin = null;
        for (int f = position.charAt(0) + incF, r = position.charAt(1) + incR; f <= 'h' && f >= 'a' && r <= '8' && r >= '1' && vectorState > 0; f += incF, r += incR) {
            // System.out.print("f: " + (char)f + ", r: " + (char)r + ", vecState: " + vectorState + "; ");
            p = game.board.getPieceAt((char)f, (char)r);
            // if (p != null) System.out.println("--$ " + p.getType() + ", " + vectorState);
            // else System.out.println("--$ " + "null" + ", " + vectorState);
            if (vectorState == 2 && p != null) {
                if (p.getColor().equals(color)) {
                    if (p.getType() != 'R' && p.getType() != 'Q' && p.getType() != 'P') pin = p;
                    if (p.getType() == 'P' && (vector == 0 || vector == 2)) {
                        int pawnInc = 1;
                        if (color.equals("black")) pawnInc = -1;
                        // add condition to check if pawn can move out of rank.
                        // (viable capture by the pawn exists).
                        Piece x = game.board.getPieceAt((char)(f + 1), (char)(r + pawnInc));
                        if (x != null && !x.getColor().equals(color)) pin = p;
                        x = game.board.getPieceAt((char)(f - 1), (char)(r + pawnInc));
                        if (x != null && !x.getColor().equals(color)) pin = p;
                        // yet to add en-passant conditions for pins.
                    }
                    vectorState -= 1;
                }
                else vectorState -= 2;
            }
            else if (vectorState == 1 && p != null) {
                if (p.getColor().equals(color)) {
                    pin = null;
                    vectorState -= 1;
                }
                else {
                    incF = 0; incR = 0;
                    vectorState += 2;
                }
            }
            else if (vectorState == 3 && p != null) {
                // System.out.println("--> f: " + (char)f + ", r: " + (char)r);
                Piece x = game.board.getPieceAt((char)f, (char)r);
                // System.out.println("Pinner: " + x.getType());
                if (x.getType() != 'R' && x.getType() != 'Q') pin = null;
                vectorState -= 3;
            }
            else;
        }
        if (vectorState != 0) pin = null;
        // if (pin != null) System.out.println("pin: " + pin.getType() + ", " + pin.getPosition());
        // else System.out.println("null");
        if (pin != null) {
            for (int i = 0; i < stack.length; i++) {
                if (pin == stack[i]) {
                    canMove[i] = false;
                    break;
                }
            }
            return true;
        }
        else return false;
    }

    //      3       0
    //          K
    //      2       1
    private boolean xRayBishop(String position, String color, int vector) {
        int incF = 1, incR = 1;
        if (vector == 1 || vector == 2) incR -= 2;
        if (vector == 2 || vector == 3) incF -= 2;
        int vectorState = 2;
        Piece p, pin = null;
        for (int f = position.charAt(0) + incF, r = position.charAt(1) + incR; f <= 'h' && f >= 'a' && r <= '8' && r >= '1' && vectorState > 0; f += incF, r += incR) {
            // System.out.print("f: " + (char)f + ", r: " + (char)r + ", vecState: " + vectorState + "; ");
            p = game.board.getPieceAt((char)f, (char)r);
            // if (p != null) System.out.println("--$ " + p.getType() + ", " + vectorState);
            // else System.out.println("--$ " + "null" + ", " + vectorState);
            if (vectorState == 2 && p != null) {
                if (p.getColor().equals(color)) {
                    if (p.getType() != 'B' && p.getType() != 'Q') pin = p;
                    vectorState -= 1;
                }
                else vectorState -= 2;
            }
            else if (vectorState == 1 && p != null) {
                if (p.getColor().equals(color)) {
                    pin = null;
                    vectorState -= 1;
                }
                else {
                    incF = 0; incR = 0;
                    vectorState += 2;
                }
            }
            else if (vectorState == 3 && p != null) {
                // System.out.println("--> f: " + (char)f + ", r: " + (char)r);
                Piece x = game.board.getPieceAt((char)f, (char)r);
                // System.out.println("Pinner: " + x.getType());
                if (x.getType() != 'B' && x.getType() != 'Q') pin = null;
                else if (pin.getType() == 'P') {
                    if (pin.getColor().equals("white")) {
                        if (vector == 3 || vector == 0) {
                            String pinPos = pin.getPosition();
                            String pinnerPos = x.getPosition();
                            if (Math.abs(pinPos.charAt(0) - pinnerPos.charAt(0)) == 1 && Math.abs(pinPos.charAt(1) - pinnerPos.charAt(1)) == 1) pin = null;
                        }
                    }
                    else {
                        if (vector == 2 || vector == 1) {
                            String pinPos = pin.getPosition();
                            String pinnerPos = x.getPosition();
                            if (Math.abs(pinPos.charAt(0) - pinnerPos.charAt(0)) == 1 && Math.abs(pinPos.charAt(1) - pinnerPos.charAt(1)) == 1) pin = null;
                        }
                    }
                }
                vectorState -= 3;
            }
            else;
        }
        if (vectorState != 0) pin = null;
        // if (pin != null) System.out.println("pin: " + pin.getType() + ", " + pin.getPosition());
        // else System.out.println("null");
        if (pin != null) {
            for (int i = 0; i < stack.length; i++) {
                if (pin == stack[i]) {
                    canMove[i] = false;
                    break;
                }
            }
            return true;
        }
        else return false;
    }

    private String[] findHotspots(String color) {
        // System.out.println("Looking for hotspots...");
        LinkedList<String> hotspots = new LinkedList<String>();
        // find pieces that are giving a check.
        LinkedList<Piece> checkers = new LinkedList<Piece>();
        String oppColor = color.equals("white") ? "black" : "white";
        String kingsPosition = game.board.getSquareOf(color, 'K').getPosition();
        Piece[] p;
        p = game.findRelevantPieces(oppColor, 'Q', kingsPosition);
        for (int i = 0; i < p.length; i++) checkers.add(p[i]);
        p = game.findRelevantPieces(oppColor, 'N', kingsPosition);
        for (int i = 0; i < p.length; i++) checkers.add(p[i]);
        p = game.findRelevantPieces(oppColor, 'B', kingsPosition);
        for (int i = 0; i < p.length; i++) checkers.add(p[i]);
        p = game.findRelevantPieces(oppColor, 'R', kingsPosition);
        for (int i = 0; i < p.length; i++) checkers.add(p[i]);
        p = game.findRelevantPieces(oppColor, 'P', kingsPosition);
        if (p != null)
            for (int i = 0; i < p.length; i++)
                if (p[i].getPosition().charAt(0) != kingsPosition.charAt(0))
                    checkers.add(p[i]);
        // find figure out attack vectors.
        p = checkers.toArray(new Piece[0]);

        // System.out.print("-> Checkers: ");
        // for (Piece s: p) System.out.print(s.getType() + s.getPosition());
        // System.out.println();
        // System.out.print("-> Hotspots: ");
        int incF = 0, incR = 0;
        for (int i = 0; i < p.length; i++) {
            // System.out.print("-K" + kingsPosition + p[i].getType() + p[i].getPosition() + "-");
            if (p[i].getType() == 'R') {
                if (kingsPosition.charAt(0) == p[i].getPosition().charAt(0)) {
                    incF = 0;
                    if (kingsPosition.charAt(1) < p[i].getPosition().charAt(1)) {
                        incR = -1;
                    }
                    else {
                        incR = 1;
                    }
                }
                else if (kingsPosition.charAt(1) == p[i].getPosition().charAt(1)) {
                    incR = 0;
                    if (kingsPosition.charAt(0) < p[i].getPosition().charAt(0)) {
                        incF = -1;
                    }
                    else {
                        incF = 1;
                    }
                }
                // for loop goes here.
                char[] c = new char[2];
                for (char k = p[i].getPosition().charAt(0), j = p[i].getPosition().charAt(1); (char)k != kingsPosition.charAt(0) || (char)j != kingsPosition.charAt(1); k += incF, j += incR) {
                    c[0] = k;
                    c[1] = j;
                    // System.out.print("r." + new String(c) + ", ");
                    hotspots.add(new String(c));
                }
            }
            else if (p[i].getType() == 'B') {
                if (kingsPosition.charAt(0) < p[i].getPosition().charAt(0)) {
                    incF = -1;
                    if (kingsPosition.charAt(1) < p[i].getPosition().charAt(1)) {
                        incR = -1;
                    }
                    else {
                        incR = 1;
                    }
                }
                else if (kingsPosition.charAt(1) > p[i].getPosition().charAt(1)) {
                    incR = 1;
                    if (kingsPosition.charAt(0) < p[i].getPosition().charAt(0)) {
                        incF = -1;
                    }
                    else {
                        incF = 1;
                    }
                }
                // for loop goes here.
                char[] c = new char[2];
                for (char k = p[i].getPosition().charAt(0), j = p[i].getPosition().charAt(1); (char)k != kingsPosition.charAt(0) || (char)j != kingsPosition.charAt(1); k += incF, j += incR) {
                    c[0] = k;
                    c[1] = j;
                    // System.out.print("b." + new String(c) + ", ");
                    hotspots.add(new String(c));
                }
            }
            else if (p[i].getType() == 'Q') {
                // System.out.print("q");
                if (kingsPosition.charAt(0) == p[i].getPosition().charAt(0)) {
                    incF = 0;
                    // System.out.print("1");
                    if (kingsPosition.charAt(1) < p[i].getPosition().charAt(1)) {
                        incR = -1;
                        // System.out.print("1");
                    }
                    else {
                        incR = 1;
                        // System.out.print("2");
                    }
                }
                else if (kingsPosition.charAt(1) == p[i].getPosition().charAt(1)) {
                    incR = 0;
                    // System.out.print("2");
                    if (kingsPosition.charAt(0) < p[i].getPosition().charAt(0)) {
                        incF = -1;
                        // System.out.print("1");
                    }
                    else {
                        incF = 1;
                        // System.out.print("2");
                    }
                }
                else if (kingsPosition.charAt(0) < p[i].getPosition().charAt(0)) {
                    incF = -1;
                    // System.out.print("3");
                    if (kingsPosition.charAt(1) < p[i].getPosition().charAt(1)) {
                        incR = -1;
                        // System.out.print("1");
                    }
                    else {
                        incR = 1;
                        // System.out.print("2");
                    }
                }
                else if (kingsPosition.charAt(1) > p[i].getPosition().charAt(1)) {
                    incR = 1;
                    // System.out.print("4");
                    if (kingsPosition.charAt(0) < p[i].getPosition().charAt(0)) {
                        incF = -1;
                        // System.out.print("1");
                    }
                    else {
                        incF = 1;
                        // System.out.print("2");
                    }
                }

                // for loop goes here.
                // System.out.print("F(" + incF + ")R(" + incR + ")");
                char[] c = new char[2];
                for (char k = p[i].getPosition().charAt(0), j = p[i].getPosition().charAt(1); (char)k != kingsPosition.charAt(0) || (char)j != kingsPosition.charAt(1); k += incF, j += incR) {
                    c[0] = k;
                    c[1] = j;
                    // System.out.print("q." + new String(c) + ", ");
                    hotspots.add(new String(c));
                }
            }
            else {
                // System.out.print("e." + p[i].getPosition() + ", ");
                hotspots.add(p[i].getPosition());
            }
        }
        System.out.println();
        return hotspots.toArray(new String[0]);
    }
}
