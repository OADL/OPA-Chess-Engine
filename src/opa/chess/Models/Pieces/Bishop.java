package opa.chess.Models.Pieces;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.Location;

import java.util.ArrayList;

import static opa.chess.Enums.PieceType.BISHOP;

public class Bishop extends Piece {

    public Bishop(Location location, boolean color, int x, int y) {
        super(location, color, x, y, BISHOP);
    }

    @Override
    public boolean checkMove(int x2, int y2, ArrayList<Piece> pieces) {
        if (x2 == this.X && y2 == this.Y) { //checks if not moved
            return false;
        }
        if (x2 < 0 || x2 > 7 || y2 > 7 || y2 < 0) { //checks if out of boundary
            return false;
        }
        if (Math.abs(y2 - this.Y) == Math.abs(x2 - this.X)) {
            CommonMethods.en_passant = false;
            return (!blocked(x2, y2, pieces));
        }
        return false;
    }

    @Override
    protected boolean blocked(int x2, int y2, ArrayList<Piece> pieces) {
        int dX = x2 > this.X ? 1 : -1;
        int dY = y2 > this.Y ? 1 : -1;
        for (int i = 1; i < Math.abs(x2 - this.X); ++i) {
            if (pieceOnSquare(this.X + i * dX, this.Y + i * dY, pieces)) {
                return true;
            }
        }
        for (Piece piece : pieces) {
            if (piece.getX() == x2 && piece.getY() == y2) {
                if (piece.getColor() == this.color) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Piece clone() {
        return new Bishop(this.location, this.color, this.X, this.Y).setFirstMove(this.firstMove);
    }
}
