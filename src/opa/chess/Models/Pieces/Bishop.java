package opa.chess.Models.Pieces;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.Color;
import opa.chess.Enums.MoveType;
import opa.chess.Models.Board;
import opa.chess.Models.Square;

import static opa.chess.Enums.PieceType.BISHOP;

public class Bishop extends Piece {

    public Bishop(Color color) {
        super(color, BISHOP);
    }

    @Override
    public MoveType checkMove(Square source, Square destination, Board board) {
        if (destination.getX() == source.getX() && destination.getY() == source.getY()) { //checks if not moved
            return MoveType.INVALID;
        }
        if (destination.getX() < 0 || destination.getX() > 7 || destination.getY() > 7 || destination.getY() < 0) { //checks if out of boundary
            return MoveType.INVALID;
        }
        if (Math.abs(destination.getY() - source.getY()) == Math.abs(destination.getX() - source.getX())) {
            return (!blocked(destination.getX(), destination.getY(), source, board.getSquares()))? MoveType.NORMAL : MoveType.INVALID;
        }
        return MoveType.INVALID;
    }

    @Override
    protected boolean blocked(int x2, int y2, Square square, Square[][] squares) {
        int dX = x2 > square.getX() ? 1 : -1;
        int dY = y2 > square.getY() ? 1 : -1;
        for (int i = 1; i < Math.abs(x2 - square.getX()); ++i) {
            if (pieceOnSquare(square.getX() + i * dX, square.getY() + i * dY, squares)) {
                return true;
            }
        }
        return super.blocked(x2, y2, square, squares);
    }

    @Override
    public int evaluate(Square square) {
        int value = 330;
        int Y = square.getY();
        int X = square.getX();
        if (Y == 0 || Y == 7) {
            if (X == 0 || X == 7) {
                value -= 20;
            } else {
                value -= 10;
            }
        } else if (color == Color.WHITE /*down*/) {
            if (Y == 1) {
                if (X == 0 || X == 7) {
                    value -= 10;
                } else {
                    value += 0;
                }
            } else if (Y == 2) {
                if (X == 0 || X == 7) {
                    value -= 10;
                } else if (X == 1 || X == 6) {
                    value += 0;
                } else if (X == 2 || X == 5) {
                    value += 5;
                } else if (X == 3 || X == 4) {
                    value += 10;
                }
            } else if (Y == 3) {
                if (X == 0 || X == 7) {
                    value -= 10;
                } else if (X == 1 || X == 2 || X == 5 || X == 6) {
                    value += 5;
                } else if (X == 3 || X == 4) {
                    value += 10;
                }
            } else if (Y == 4) {
                if (X == 0 || X == 7) {
                    value -= 10;
                } else if (X == 1 || X == 6) {
                    value += 0;
                } else {
                    value += 10;
                }
            } else if (Y == 5) {
                if (X == 0 || X == 7) {
                    value -= 10;
                } else {
                    value += 10;
                }
            } else if (Y == 6) {
                if (X == 0 || X == 7) {
                    value -= 10;
                } else if (X == 1 || X == 6) {
                    value += 5;
                } else {
                    value += 0;
                }
            }
        } else if (Y == 6) {
            if (X == 0 || X == 7) {
                value -= 10;
            } else {
                value += 0;
            }
        } else if (Y == 5) {
            if (X == 0 || X == 7) {
                value -= 10;
            } else if (X == 1 || X == 6) {
                value += 0;
            } else if (X == 2 || X == 5) {
                value += 5;
            } else if (X == 3 || X == 4) {
                value += 10;
            }
        } else if (Y == 4) {
            if (X == 0 || X == 7) {
                value -= 10;
            } else if (X == 1 || X == 2 || X == 5 || X == 6) {
                value += 5;
            } else if (X == 3 || X == 4) {
                value += 10;
            }
        } else if (Y == 3) {
            if (X == 0 || X == 7) {
                value -= 10;
            } else if (X == 1 || X == 6) {
                value += 0;
            } else {
                value += 10;
            }
        } else if (Y == 2) {
            if (X == 0 || X == 7) {
                value -= 10;
            } else {
                value += 10;
            }
        } else if (Y == 1) {
            if (X == 0 || X == 7) {
                value -= 10;
            } else if (X == 1 || X == 6) {
                value += 5;
            } else {
                value += 0;
            }
        }
        return value;
    }

    @Override
    public Piece clone() {
        return new Bishop(this.color).setFirstMove(this.firstMove);
    }

    @Override
    public String toString() {
        return (this.color == Color.BLACK)? "\u2657" : "\u265D";
    }
}
