package opa.chess.Models.Pieces;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.Color;
import opa.chess.Models.Board;

import java.util.ArrayList;

import static opa.chess.Enums.PieceType.PAWN;

public class Pawn extends Piece {

    public Pawn(Color color, int x, int y) {
        super(color, x, y, PAWN);
    }

    @Override
    public boolean checkMove(int x2, int y2, Board board) {
        if (x2 == this.X && y2 == this.Y) { //checks if not moved
            return false;
        }
        if (x2 < 0 || x2 > 7 || y2 > 7 || y2 < 0) { //checks if out of boundary
            return false;
        }
        if (this.color == Color.BLACK) { // UP going down
            if ((y2 == this.Y + 1) && x2 == this.X) {// check if this is a 1 step forward
                if (!blocked(x2, y2, board.getPieces())) {
                    CommonMethods.en_passant = false;
                    return true;
                } else {
                    return false;
                }
            } else if ((y2 == this.Y + 2) && x2 == this.X) { //check if this is a 2 step forward

                if (this.firstMove) { // check if this is the first move
                    if (!blocked(x2, y2, board.getPieces())) {
                        CommonMethods.en_passant = true;
                        return (true);
                    }
                }
            } else if ((y2 == this.Y + 1) && (x2 == this.X + 1 || x2 == this.X - 1)) {//diagonal move
                Piece adjpiece = board.findPiece(x2, this.Y);
                if (adjpiece != null) {
                    if (adjpiece.getType() == PAWN && CommonMethods.en_passant && adjpiece.isFirstMove() && adjpiece.getColor() != this.color) {
                        return true;
                    }
                }
                Piece pieceToEat = board.findPiece(x2, y2);
                if (pieceToEat != null && canEat(pieceToEat)) {//check if an opponents piece can be eaten by this move
                    CommonMethods.en_passant = false;
                    return true;
                }
            }
        } else { // DOWN going up
            if ((y2 == this.Y - 1) && x2 == this.X) {// check if this is a 1 step forward
                if (!blocked(x2, y2, board.getPieces())) {
                    CommonMethods.en_passant = false;
                    return true;
                } else {
                    return false;
                }

            } else if ((y2 == this.Y - 2) && x2 == this.X) { //check if this is a 2 step forward

                if (this.firstMove) { // check if this is the first move
                    if (!blocked(x2, y2, board.getPieces())) {
                        CommonMethods.en_passant = true;
                        return (true);
                    }
                }
            } else if ((y2 == this.Y - 1) && (x2 == this.X + 1 || x2 == this.X - 1)) {//diagonal move
                Piece adjpiece = board.findPiece(x2, this.Y);
                if (adjpiece != null) {
                    if (adjpiece.getType() == PAWN && CommonMethods.en_passant && adjpiece.isFirstMove() && adjpiece.getColor() != this.color) {
                        return true;
                    }
                }
                Piece pieceToEat = board.findPiece(x2, y2);
                if (pieceToEat != null && canEat(pieceToEat)) { //check if an opponents piece can be eaten by this move
                    CommonMethods.en_passant = false;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean blocked(int x2, int y2, ArrayList<Piece> pieces) {
        int dY = y2 > this.Y ? 1 : -1;
        for (int i = 1; i <= Math.abs(y2 - this.Y); i++) {
            if (pieceOnSquare(this.X, this.Y + i * dY, pieces)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int evaluate() {
        int value = 100;
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
        return new Pawn(this.color, this.X, this.Y).setFirstMove(this.firstMove);
    }

    @Override
    public String toString() {
        return (this.color == Color.BLACK)? "\u2659" : "\u265F";
    }
}
