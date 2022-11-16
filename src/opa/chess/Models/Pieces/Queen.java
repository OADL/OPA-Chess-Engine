package opa.chess.Models.Pieces;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.Color;
import opa.chess.Models.Board;

import java.util.ArrayList;

import static opa.chess.Enums.PieceType.QUEEN;

public class Queen extends Piece {

    public Queen(Color color, int x, int y) {
        super(color, x, y, QUEEN);
    }

    @Override
    public boolean checkMove(int x2, int y2, Board board) {
        if (x2 == this.X && y2 == this.Y) { //checks if not moved
            return false;
        }
        if (x2 < 0 || x2 > 7 || y2 > 7 || y2 < 0) { //checks if out of boundary
            return false;
        }
        if (Math.abs(y2 - this.Y) == Math.abs(x2 - this.X)) {
            CommonMethods.en_passant = false;
            return (!blocked(x2, y2, board.getPieces()));
        } else if (y2 != this.Y && x2 == this.X) {
            CommonMethods.en_passant = false;
            return (!blocked(x2, y2, board.getPieces()));
        } else if (y2 == this.Y) {
            CommonMethods.en_passant = false;
            return (!blocked(x2, y2, board.getPieces()));
        }
        return false;
    }

    @Override
    protected boolean blocked(int x2, int y2, ArrayList<Piece> pieces) {
        int dX = x2 > this.X ? 1 : -1;
        int dY = y2 > this.Y ? 1 : -1;
        if (x2 == this.X) {
            for (int i = 1; i < Math.abs(y2 - this.Y); i++) {
                if (pieceOnSquare(this.X, this.Y + i * dY, pieces)) {

                    return true;
                }
            }
        } else if (y2 == this.Y) {
            for (int i = 1; i < Math.abs(x2 - this.X); i++) {
                if (pieceOnSquare(this.X + i * dX, this.Y, pieces)) {
                    return true;
                }
            }
        } else {
            for (int i = 1; i < Math.abs(x2 - this.X); ++i) {
                if (pieceOnSquare(this.X + i * dX, this.Y + i * dY, pieces)) {
                    return true;
                }
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
    public int evaluate() {
        int value = 900;
        if (Y == 0 || Y == 7) {
            if (X == 0 || X == 7) {
                value -= 20;
            } else if (X == 1 || X == 2 || X == 5 || X == 6) {
                value -= 10;
            } else {
                value -= 5;
            }
        }
        if (color == Color.WHITE /*down*/) {
            if (Y == 1) {
                if (X == 0 || X == 7) {
                    value -= 10;
                } else {
                    value -= 0;
                }
            } else if (Y == 2) {
                if (X == 0 || X == 7) {
                    value -= 10;
                } else if (X == 1 || X == 6) {
                    value += 0;
                } else {
                    value += 5;
                }
            } else if (Y == 3) {
                if (X == 0 || X == 7) {
                    value -= 5;
                } else if (X == 1 || X == 6) {
                    value += 0;
                } else {
                    value += 5;
                }
            } else if (Y == 4) {
                if (X == 7) {
                    value -= 5;
                } else if (X == 2 || X == 3 || X == 4 || X == 5) {
                    value += 5;
                } else {
                    value += 0;
                }
            } else if (Y == 5) {
                if (X == 0 || X == 7) {
                    value -= 10;
                } else if (X == 6) {
                    value += 0;
                } else {
                    value += 5;
                }
            } else if (Y == 6) {
                if (X == 0 || X == 7) {
                    value -= 10;
                } else if (X == 2) {
                    value += 5;
                } else {
                    value += 0;
                }
            }
        } else if (Y == 6) {
            if (X == 0 || X == 7) {
                value -= 10;
            } else {
                value -= 0;
            }
        } else if (Y == 5) {
            if (X == 0 || X == 7) {
                value -= 10;
            } else if (X == 1 || X == 6) {
                value += 0;
            } else {
                value += 5;
            }
        } else if (Y == 4) {
            if (X == 0 || X == 7) {
                value -= 5;
            } else if (X == 1 || X == 6) {
                value += 0;
            } else {
                value += 5;
            }
        } else if (Y == 3) {
            if (X == 0) {
                value -= 5;
            } else if (X == 2 || X == 3 || X == 4 || X == 5) {
                value += 5;
            } else {
                value += 0;
            }
        } else if (Y == 2) {
            if (X == 0 || X == 7) {
                value -= 10;
            } else if (X == 1) {
                value += 0;
            } else {
                value += 5;
            }
        } else if (Y == 1) {
            if (X == 0 || X == 7) {
                value -= 10;
            } else if (X == 5) {
                value += 5;
            } else {
                value += 0;
            }
        }
        return value;
    }

    @Override
    public Piece clone() {
        return new Queen(this.color, this.X, this.Y).setFirstMove(this.firstMove);
    }

    @Override
    public String toString() {
        return (this.color == Color.BLACK)? "\u2655" : "\u265B";
    }
}
