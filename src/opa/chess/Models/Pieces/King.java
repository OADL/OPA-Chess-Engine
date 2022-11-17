package opa.chess.Models.Pieces;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.Color;
import opa.chess.Enums.MoveType;
import opa.chess.Models.Board;
import opa.chess.Models.Move;
import opa.chess.Models.Square;

import java.util.ArrayList;
import java.util.List;

import static opa.chess.Enums.PieceType.KING;
import static opa.chess.Enums.PieceType.ROOK;

public class King extends Piece {

    private boolean checked;

    public King(Color color) {
        super(color, KING);
        checked = false;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public MoveType checkMove(Square source, Square destination, Board board) {
        if (destination.getX() == source.getX() && destination.getY() == source.getY()) { //checks if not moved
            return MoveType.INVALID;
        }
        if (destination.getX() < 0 || destination.getX() > 7 || destination.getY() > 7 || destination.getY() < 0) { //checks if out of boundary
            return MoveType.INVALID;
        }
        if ((destination.getX() == 2 || destination.getX() == 6) && (destination.getY() == 0 || destination.getY() == 7)) {//all possible castling positions
            if (this.firstMove) {//checks if this is the first move for the King
                if (isChecked()) return MoveType.INVALID;
                int rookX = destination.getX() > source.getX() ? 7 : 0;//get the rooks position
                Piece rook = board.findSquare(rookX, destination.getY()).getPiece();
                if (rook != null && rook.getType() == ROOK && rook.isFirstMove()) {//checks if the rooks hasn't moved yet
                    for (int i = 0; i < Math.abs(rookX - source.getX()) - 1; i++) { //checks if there is nothing between them
                        if (rookX == 7) {
                            if (blocked(rookX - 1 - i, destination.getY(), source, board.getSquares())) {
                                return MoveType.INVALID;
                            }
                        } else {
                            if (blocked(rookX + 1 + i, destination.getY(), source, board.getSquares())) {
                                return MoveType.INVALID;
                            }
                        }
                    }
                    return MoveType.CASTLING;
                }
            }
        }
        if ((Math.abs(destination.getY() - source.getY()) == 1 || Math.abs(destination.getY() - source.getY()) == 0) && (Math.abs(destination.getX() - source.getX()) == 1 || Math.abs(destination.getX() - source.getX()) == 0)) {
            return (!blocked(destination.getX(), destination.getY(), source, board.getSquares()))? MoveType.NORMAL : MoveType.INVALID;
        }
        return MoveType.INVALID;
    }

    @Override
    public int evaluate(Square square) {
        int value = 20000;
        int Y = square.getY();
        int X = square.getX();
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
    public List<Move> nextMoves(Square square, Board board) {
        List<Move> moves = new ArrayList<>();
        int x = square.getX();
        int y = square.getY();
        Move move = getMove(square, board, x - 1, y - 1);
        if(move != null) moves.add(move);
        move = getMove(square, board, x - 1, y + 1);
        if(move != null) moves.add(move);
        move = getMove(square, board, x - 1, y);
        if(move != null) moves.add(move);
        move = getMove(square, board, x, y - 1);
        if(move != null) moves.add(move);
        move = getMove(square, board, x, y + 1);
        if(move != null) moves.add(move);
        move = getMove(square, board, x + 1, y - 1);
        if(move != null) moves.add(move);
        move = getMove(square, board, x + 1, y + 1);
        if(move != null) moves.add(move);
        move = getMove(square, board, x + 1, y);
        if(move != null) moves.add(move);
        if ((square.getPiece().getColor() == Color.WHITE && y == 7) || (square.getPiece().getColor() == Color.BLACK && y == 0)) {
            move = getMove(square, board, 2, y);
            if(move != null) moves.add(move);
            move = getMove(square, board, 6, y);
            if(move != null) moves.add(move);
        }
        return moves;
    }

    @Override
    public Piece clone() {
        return new King(this.color).setFirstMove(this.firstMove);
    }

    @Override
    public String toString() {
        return (this.color == Color.BLACK)? "\u2654" : "\u265A";
    }
}
