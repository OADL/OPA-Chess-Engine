package opa.chess.Models.Pieces;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.Location;

import java.util.ArrayList;

import static opa.chess.Enums.Location.UP;
import static opa.chess.Enums.PieceType.PAWN;

public class Pawn extends Piece {

    public Pawn(Location location, boolean color, int x, int y) {
        super(location, color, x, y, PAWN);
    }

    @Override
    public boolean checkMove(int x2, int y2, ArrayList<Piece> pieces) {
        if (x2 == this.X && y2 == this.Y) { //checks if not moved
            return false;
        }
        if (x2 < 0 || x2 > 7 || y2 > 7 || y2 < 0) { //checks if out of boundary
            return false;
        }
        if (this.location == UP) { // UP going down
            if ((y2 == this.Y + 1) && x2 == this.X) {// check if this is a 1 step forward
                if (!blocked(x2, y2, pieces)) {
                    CommonMethods.en_passant = false;
                    return true;
                } else {
                    return false;
                }
            } else if ((y2 == this.Y + 2) && x2 == this.X) { //check if this is a 2 step forward

                if (this.firstMove) { // check if this is the first move
                    if (!blocked(x2, y2, pieces)) {
                        CommonMethods.en_passant = true;
                        return (true);
                    }
                }
            } else if ((y2 == this.Y + 1) && (x2 == this.X + 1 || x2 == this.X - 1)) {//diagonal move
                Piece adjpiece = CommonMethods.getPieceOnSquare(x2, this.Y, pieces);
                if (adjpiece != null) {
                    if (adjpiece.getType() == PAWN && CommonMethods.en_passant && adjpiece.isFirstMove() && adjpiece.getColor() != this.color) {
                        return true;
                    }
                }
                if (canEat(x2, y2, pieces) != null) {//check if an opponents piece can be eaten by this move
                    CommonMethods.en_passant = false;
                    return true;
                }
            }
        } else { // DOWN going up
            if ((y2 == this.Y - 1) && x2 == this.X) {// check if this is a 1 step forward
                if (!blocked(x2, y2, pieces)) {
                    CommonMethods.en_passant = false;
                    return true;
                } else {
                    return false;
                }

            } else if ((y2 == this.Y - 2) && x2 == this.X) { //check if this is a 2 step forward

                if (this.firstMove) { // check if this is the first move
                    if (!blocked(x2, y2, pieces)) {
                        CommonMethods.en_passant = true;
                        return (true);
                    }
                }
            } else if ((y2 == this.Y - 1) && (x2 == this.X + 1 || x2 == this.X - 1)) {//diagonal move
                Piece adjpiece = CommonMethods.getPieceOnSquare(x2, this.Y, pieces);
                if (adjpiece != null) {
                    if (adjpiece.getType() == PAWN && CommonMethods.en_passant && adjpiece.isFirstMove() && adjpiece.getColor() != this.color) {
                        return true;
                    }
                }
                if (canEat(x2, y2, pieces) != null) { //check if an opponents piece can be eaten by this move
                    CommonMethods.en_passant = false;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean blocked(int x2, int y2, ArrayList<Piece> pieces) {
        int dY = y2 > this.Y ? 1 : -1;
        for (int i = 1; i <= Math.abs(y2 - this.Y); i++) {
            if (pieceOnSquare(this.X, this.Y + i * dY, pieces)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Piece clone() {
        return new Pawn(this.location, this.color, this.X, this.Y).setFirstMove(this.firstMove);
    }
}
