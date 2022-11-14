package opa.chess.Models.Pieces;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.Location;

import java.util.ArrayList;

import static opa.chess.Enums.PieceType.KNIGHT;

public class Knight extends Piece {

    public Knight(Location location, boolean color, int x, int y) {
        super(location, color, x, y, KNIGHT);
    }

    @Override
    public boolean checkMove(int x2, int y2, ArrayList<Piece> pieces) {
        if (x2 == this.X && y2 == this.Y) { //checks if not moved
            return false;
        }
        if (x2 < 0 || x2 > 7 || y2 > 7 || y2 < 0) { //checks if out of boundary
            return false;
        }
        if ((y2 == this.Y + 2 || y2 == this.Y - 2) && (x2 == this.X + 1 || x2 == this.X - 1)) {
            CommonMethods.en_passant = false;
            return (!blocked(x2, y2, pieces));
        } else if ((y2 == this.Y + 1 || y2 == this.Y - 1) && (x2 == this.X + 2 || x2 == this.X - 2)) {
            CommonMethods.en_passant = false;
            return (!blocked(x2, y2, pieces));
        }
        return false;
    }

    @Override
    protected boolean blocked(int x2, int y2, ArrayList<Piece> pieces) {
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
        return new Knight(this.location, this.color, this.X, this.Y).setFirstMove(this.firstMove);
    }
}
