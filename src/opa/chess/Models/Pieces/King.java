package opa.chess.Models.Pieces;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.Color;
import opa.chess.Enums.Location;

import java.util.ArrayList;

import static opa.chess.Enums.PieceType.*;

public class King extends Piece {

    public King(Location location, Color color, int x, int y) {
        super(location, color, x, y, KING);
    }

    @Override
    public boolean checkMove(int x2, int y2, ArrayList<Piece> pieces) {
        if (x2 == this.X && y2 == this.Y) { //checks if not moved
            return false;
        }
        if (x2 < 0 || x2 > 7 || y2 > 7 || y2 < 0) { //checks if out of boundary
            return false;
        }
        if ((x2 == 2 || x2 == 6) && (y2 == 0 || y2 == 7)) {//all possible castling possitions
            if (this.firstMove) {//checks if this is the first move for the King
                if (((this.color == Color.WHITE) && CommonMethods.white_king_checked) || ((this.color == Color.BLACK) && CommonMethods.black_king_checked))//if checked cant castle
                {
                    return false;
                }
                int rookX = x2 > this.X ? 7 : 0;//get the rooks position
                Piece rook = CommonMethods.getPieceOnSquare(rookX, y2, pieces);
                if (rook != null && rook.getType() == ROOK && rook.isFirstMove()) {//checks if the rooks hasn't moved yet
                    for (int i = 0; i < Math.abs(rookX - this.X) - 1; i++) { //checks if there is nothing between them
                        if (rookX == 7) {
                            if (blocked(rookX - 1 - i, y2, pieces)) {
                                return false;
                            }
                        } else {
                            if (blocked(rookX + 1 + i, y2, pieces)) {
                                return false;
                            }
                        }
                    }
                    CommonMethods.castling = true;
                    return true;
                }
            }
        }
        if ((Math.abs(y2 - this.Y) == 1 || Math.abs(y2 - this.Y) == 0) && (Math.abs(x2 - this.X) == 1 || Math.abs(x2 - this.X) == 0)) {
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
        return new King(this.location, this.color, this.X, this.Y).setFirstMove(this.firstMove);
    }

    @Override
    public String toString() {
        return (this.color == Color.BLACK)? "\u2654" : "\u265A";
    }
}
