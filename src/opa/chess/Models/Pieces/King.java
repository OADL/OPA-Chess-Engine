package opa.chess.Models.Pieces;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.Color;
import opa.chess.Models.Board;

import java.util.ArrayList;

import static opa.chess.Enums.PieceType.*;

public class King extends Piece {

    public King(Color color, int x, int y) {
        super(color, x, y, KING);
    }

    @Override
    public boolean checkMove(int x2, int y2, Board board) {
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
                Piece rook = board.findPiece(rookX, y2);
                if (rook != null && rook.getType() == ROOK && rook.isFirstMove()) {//checks if the rooks hasn't moved yet
                    for (int i = 0; i < Math.abs(rookX - this.X) - 1; i++) { //checks if there is nothing between them
                        if (rookX == 7) {
                            if (blocked(rookX - 1 - i, y2, board.getPieces())) {
                                return false;
                            }
                        } else {
                            if (blocked(rookX + 1 + i, y2, board.getPieces())) {
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
            return (!blocked(x2, y2, board.getPieces()));
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
    public int evaluate() {
        int value = 20000;
        if (color == Color.WHITE /*down*/) {
            if (Y == 0 || Y == 1 || Y == 2 || Y == 3) {
                if (X == 0 || X == 7) {
                    value -= 30;
                } else if (X == 3 || X == 4) {
                    value -= 50;
                } else {
                    value -= 40;
                }
            } else if (Y == 4) {
                if (X == 0 || X == 7) {
                    value -= 20;
                } else if (X == 3 || X == 4) {
                    value -= 40;
                } else {
                    value -= 30;
                }
            } else if (Y == 5) {
                if (X == 0 || X == 7) {
                    value -= 10;
                } else {
                    value -= 20;
                }
            } else if (Y == 6) {
                if (X == 0 || X == 7) {
                    value += 20;
                } else {
                    value += 0;
                }
            } else if (Y == 7) {
                if (X == 0 || X == 7) {
                    value += 20;
                } else if (X == 1 || X == 6) {
                    value += 30;
                } else if (X == 2 || X == 5) {
                    value += 10;
                } else {
                    value += 0;
                }
            }
        } else if (Y == 7 || Y == 6 || Y == 5 || Y == 4) {
            if (X == 0 || X == 7) {
                value -= 30;
            } else if (X == 3 || X == 4) {
                value -= 50;
            } else {
                value -= 40;
            }
        } else if (Y == 3) {
            if (X == 0 || X == 7) {
                value -= 20;
            } else if (X == 3 || X == 4) {
                value -= 40;
            } else {
                value -= 30;
            }
        } else if (Y == 2) {
            if (X == 0 || X == 7) {
                value -= 10;
            } else {
                value -= 20;
            }
        } else if (Y == 1) {
            if (X == 0 || X == 7) {
                value += 20;
            } else {
                value += 0;
            }
        } else if (Y == 0) {
            if (X == 0 || X == 7) {
                value += 20;
            } else if (X == 1 || X == 6) {
                value += 30;
            } else if (X == 2 || X == 5) {
                value += 10;
            } else {
                value += 0;
            }
        }
        return value;
    }

    @Override
    public Piece clone() {
        return new King(this.color, this.X, this.Y).setFirstMove(this.firstMove);
    }

    @Override
    public String toString() {
        return (this.color == Color.BLACK)? "\u2654" : "\u265A";
    }
}
