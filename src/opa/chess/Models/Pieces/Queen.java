package opa.chess.Models.Pieces;

import opa.chess.Enums.Color;
import opa.chess.Enums.MoveType;
import opa.chess.Models.Board;
import opa.chess.Models.Move;
import opa.chess.Models.Square;

import java.util.List;

import static opa.chess.Enums.PieceType.QUEEN;

public class Queen extends Piece implements StraightPiece,DiagonalPiece {

    public Queen(Color color) {
        super(color, QUEEN);
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
        } else if (destination.getY() != source.getY() && destination.getX() == source.getX()) {
            return (!blocked(destination.getX(), destination.getY(), source, board.getSquares()))? MoveType.NORMAL : MoveType.INVALID;
        } else if (destination.getY() == source.getY()) {
            return (!blocked(destination.getX(), destination.getY(), source, board.getSquares()))? MoveType.NORMAL : MoveType.INVALID;
        }
        return MoveType.INVALID;
    }

    @Override
    protected boolean blocked(int x2, int y2, Square square, Square[][] squares) {
        int dX = x2 > square.getX() ? 1 : -1;
        int dY = y2 > square.getY() ? 1 : -1;
        if (x2 == square.getX()) {
            for (int i = 1; i < Math.abs(y2 - square.getY()); i++) {
                if (pieceOnSquare(square.getX(), square.getY() + i * dY, squares)) {

                    return true;
                }
            }
        } else if (y2 == square.getY()) {
            for (int i = 1; i < Math.abs(x2 - square.getX()); i++) {
                if (pieceOnSquare(square.getX() + i * dX, square.getY(), squares)) {
                    return true;
                }
            }
        } else {
            for (int i = 1; i < Math.abs(x2 - square.getX()); ++i) {
                if (pieceOnSquare(square.getX() + i * dX, square.getY() + i * dY, squares)) {
                    return true;
                }
            }
        }
        return super.blocked(x2, y2, square, squares);
    }

    @Override
    public int evaluate(Square square) {
        int value = 900;
        int Y = square.getY();
        int X = square.getX();
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
    public List<Move> nextMoves(Square square, Board board) {
        List<Move> moves = possibleStraightMoves(true,square,board);
        moves.addAll(possibleStraightMoves(false,square,board));
        moves.addAll(possibleDiagonalMoves(true,square,board));
        moves.addAll(possibleDiagonalMoves(false,square,board));
        return moves;
    }

    @Override
    public Piece clone() {
        return new Queen(this.color).setFirstMove(this.firstMove);
    }

    @Override
    public String toString() {
        return (this.color == Color.BLACK)? "\u2655" : "\u265B";
    }
}
