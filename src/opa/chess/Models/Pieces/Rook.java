package opa.chess.Models.Pieces;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.Color;
import opa.chess.Enums.MoveType;
import opa.chess.Models.Board;
import opa.chess.Models.Square;

import java.util.ArrayList;

import static opa.chess.Enums.PieceType.ROOK;

public class Rook extends Piece {

    public Rook(Color color) {
        super(color, ROOK);
    }

    @Override
    public MoveType checkMove(Square source, Square destination, Board board) {
        if (destination.getX() == source.getX() && destination.getY() == source.getY()) { //checks if not moved
            return MoveType.INVALID;
        }
        if (destination.getX() < 0 || destination.getX() > 7 || destination.getY() > 7 || destination.getY() < 0) { //checks if out of boundary
            return MoveType.INVALID;
        }
        if (destination.getY() != source.getY() && destination.getX() == source.getX()) {
            CommonMethods.en_passant = false;
            return (!blocked(destination.getX(), destination.getY(), source, board.getSquares()))? MoveType.NORMAL: MoveType.INVALID;
        } else if (destination.getY() == source.getY()) {
            CommonMethods.en_passant = false;
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
        }
        return super.blocked(x2, y2, square, squares);
    }

    @Override
    public int evaluate(Square square) {
        int value = 500;
        int Y = square.getY();
        int X = square.getX();
        if (color == Color.WHITE /*down*/) {
            if (Y == 0) {
                value += 0;
            } else if (Y == 1) {
                if (X == 0 || X == 7) {
                    value += 5;
                } else {
                    value += 10;
                }
            } else if (Y == 7) {
                if (X == 3 || X == 4) {
                    value += 5;
                } else {
                    value += 0;
                }
            } else if (X == 0 || X == 7) {
                value -= 5;
            } else {
                value += 0;
            }
        } else if (Y == 7) {
            value += 0;
        } else if (Y == 6) {
            if (X == 0 || X == 7) {
                value += 5;
            } else {
                value += 10;
            }
        } else if (Y == 0) {
            if (X == 3 || X == 4) {
                value += 5;
            } else {
                value += 0;
            }
        } else if (X == 0 || X == 7) {
            value -= 5;
        } else {
            value += 0;
        }

        return value;
    }

    @Override
    public Piece clone() {
        return new Rook(this.color).setFirstMove(this.firstMove);
    }

    @Override
    public String toString() {
        return (this.color == Color.BLACK)? "\u2656" : "\u265C";
    }
}
