package opa.chess.Models.Pieces;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.Color;
import opa.chess.Enums.MoveType;
import opa.chess.Models.Board;
import opa.chess.Models.Move;
import opa.chess.Models.Square;

import static opa.chess.Enums.PieceType.PAWN;

public class Pawn extends Piece {

    public Pawn(Color color) {
        super(color, PAWN);
    }

    @Override
    public MoveType checkMove(Square source, Square destination, Board board) {
        if (destination.getX() == source.getX() && destination.getY() == source.getY()) { //checks if not moved
            return MoveType.INVALID;
        }
        if (destination.getX() < 0 || destination.getX() > 7 || destination.getY() > 7 || destination.getY() < 0) { //checks if out of boundary
            return MoveType.INVALID;
        }
        if (this.color == Color.BLACK) { // UP going down
            if ((destination.getY() == source.getY() + 1) && destination.getX() == source.getX()) {// check if this is a 1 step forward
                if (!blocked(destination.getX(), destination.getY(), source, board.getSquares())) {
                    return (destination.getY() == 7)? MoveType.PROMOTION : MoveType.NORMAL;
                } else {
                    return MoveType.INVALID;
                }
            } else if ((destination.getY() == source.getY() + 2) && destination.getX() == source.getX()) { //check if this is a 2 step forward
                if (this.firstMove) { // check if this is the first move
                    if (!blocked(destination.getX(), destination.getY(), source, board.getSquares())) {
                        return MoveType.NORMAL;
                    }
                }
            } else if ((destination.getY() == source.getY() + 1) && (destination.getX() == source.getX() + 1 || destination.getX() == source.getX() - 1)) {//diagonal move
                if(isEnPassantMove(source, destination, board)) return MoveType.EN_PASSANT;
                if(isDiagonalEat(destination, board)) return MoveType.NORMAL;
            }
        } else { // DOWN going up
            if ((destination.getY() == source.getY() - 1) && destination.getX() == source.getX()) {// check if this is a 1 step forward
                if (!blocked(destination.getX(), destination.getY(), source, board.getSquares())) {
                    return (destination.getY() == 0)? MoveType.PROMOTION : MoveType.NORMAL;
                } else {
                    return MoveType.INVALID;
                }
            } else if ((destination.getY() == source.getY() - 2) && destination.getX() == source.getX()) { //check if this is a 2 step forward
                if (this.firstMove) { // check if this is the first move
                    if (!blocked(destination.getX(), destination.getY(), source, board.getSquares())) {
                        return MoveType.NORMAL;
                    }
                }
            } else if ((destination.getY() == source.getY() - 1) && (destination.getX() == source.getX() + 1 || destination.getX() == source.getX() - 1)) {//diagonal move
                if(isEnPassantMove(source, destination, board)) return MoveType.EN_PASSANT;
                if(isDiagonalEat(destination, board)) return MoveType.NORMAL;
            }
        }
        return MoveType.INVALID;
    }

    @Override
    protected boolean blocked(int x2, int y2, Square square, Square[][] squares) {
        int dY = y2 > square.getY() ? 1 : -1;
        for (int i = 1; i <= Math.abs(y2 - square.getY()); i++) {
            if (pieceOnSquare(square.getX(), square.getY() + i * dY, squares)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEnPassantMove(Square source, Square destination, Board board) {
        Square adjSquare = board.findSquare(destination.getX(), source.getY());
        Move lastMove = board.getLastMove();
        if (adjSquare != null && adjSquare.getPiece() != null && lastMove != null) {
            return adjSquare.getPiece().getType() == PAWN
                    && adjSquare.equals(lastMove.getDestination())
                    && adjSquare.getPiece().equals(lastMove.getPiece())
                    && Math.abs(lastMove.getSource().getY() - lastMove.getDestination().getY()) == 2
                    && adjSquare.getPiece().getColor() != this.color;
        }
        return false;
    }

    private boolean isDiagonalEat(Square destination, Board board) {
        Piece pieceToEat = board.findSquare(destination.getX(), destination.getY()).getPiece();
        return pieceToEat != null && canEat(pieceToEat); //check if an opponents piece can be eaten by this move
    }

    @Override
    public int evaluate(Square square) {
        int value = 100;
        int Y = square.getY();
        int X = square.getX();
        if (Y == 0 || Y == 7) {
            value += 0;
        } else if (color == Color.WHITE) {
            if (Y == 1) {
                value += 50;
            } else if (Y == 2) {
                if (X == 0 || X == 1 || X == 6 || X == 7) {
                    value += 10;
                } else if (X == 2 || X == 5) {
                    value += 20;
                } else if (X == 3 || X == 4) {
                    value += 30;
                }
            } else if (Y == 3) {
                if (X == 0 || X == 1 || X == 6 || X == 7) {
                    value += 5;
                } else if (X == 2 || X == 5) {
                    value += 10;
                } else if (X == 3 || X == 4) {
                    value += 25;
                }
            } else if (Y == 4) {
                if (X == 0 || X == 1 || X == 2 || X == 5 || X == 6 || X == 7) {
                    value += 0;
                } else {
                    value += 20;
                }
            } else if (Y == 5) {
                if (X == 0 || X == 7) {
                    value += 5;
                } else if (X == 1 || X == 6) {
                    value -= 5;
                } else if (X == 2 || X == 5) {
                    value -= 10;
                } else if (X == 3 || X == 4) {
                    value += 0;
                }
            } else if (Y == 6) {
                if (X == 0 || X == 7) {
                    value += 5;
                } else if (X == 1 || X == 2 || X == 5 || X == 6) {
                    value += 10;
                } else if (X == 3 || X == 4) {
                    value -= 20;
                }
            }
        } else if (Y == 6) {
            value += 50;
        } else if (Y == 5) {
            if (X == 0 || X == 1 || X == 6 || X == 7) {
                value += 10;
            } else if (X == 2 || X == 5) {
                value += 20;
            } else if (X == 3 || X == 4) {
                value += 30;
            }
        } else if (Y == 4) {
            if (X == 0 || X == 1 || X == 6 || X == 7) {
                value += 5;
            } else if (X == 2 || X == 5) {
                value += 10;
            } else if (X == 3 || X == 4) {
                value += 25;
            }
        } else if (Y == 3) {
            if (X == 0 || X == 1 || X == 2 || X == 5 || X == 6 || X == 7) {
                value += 0;
            } else {
                value += 20;
            }
        } else if (Y == 2) {
            if (X == 0 || X == 7) {
                value += 5;
            } else if (X == 1 || X == 6) {
                value -= 5;
            } else if (X == 2 || X == 5) {
                value -= 10;
            } else if (X == 3 || X == 4) {
                value += 0;
            }
        } else if (Y == 1) {
            if (X == 0 || X == 7) {
                value += 5;
            } else if (X == 1 || X == 2 || X == 5 || X == 6) {
                value += 10;
            } else if (X == 3 || X == 4) {
                value -= 20;
            }
        }
        return value;
    }

    @Override
    public Piece clone() {
        return new Pawn(this.color).setFirstMove(this.firstMove);
    }

    @Override
    public String toString() {
        return (this.color == Color.BLACK)? "\u2659" : "\u265F";
    }
}
