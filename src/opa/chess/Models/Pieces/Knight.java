package opa.chess.Models.Pieces;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.Color;
import opa.chess.Enums.MoveType;
import opa.chess.Models.Board;
import opa.chess.Models.Square;

import java.util.ArrayList;

import static opa.chess.Enums.PieceType.KNIGHT;

public class Knight extends Piece {

    public Knight(Color color) {
        super(color, KNIGHT);
    }

    @Override
    public MoveType checkMove(Square source, Square destination, Board board) {
        if (destination.getX() == source.getX() && destination.getY() == source.getY()) { //checks if not moved
            return MoveType.INVALID;
        }
        if (destination.getX() < 0 || destination.getX() > 7 || destination.getY() > 7 || destination.getY() < 0) { //checks if out of boundary
            return MoveType.INVALID;
        }
        if ((destination.getY() == source.getY() + 2 || destination.getY() == source.getY() - 2) && (destination.getX() == source.getX() + 1 || destination.getX() == source.getX() - 1)) {
            return (!blocked(destination.getX(), destination.getY(), source, board.getSquares()))? MoveType.NORMAL : MoveType.INVALID;
        } else if ((destination.getY() == source.getY() + 1 || destination.getY() == source.getY() - 1) && (destination.getX() == source.getX() + 2 || destination.getX() == source.getX() - 2)) {
            return (!blocked(destination.getX(), destination.getY(), source, board.getSquares()))? MoveType.NORMAL : MoveType.INVALID;
        }
        return MoveType.INVALID;
    }

    @Override
    public int evaluate(Square square) {
        int value = 320;
        int Y = square.getY();
        int X = square.getX();
        if (Y == 0 || Y == 7) {
            if (X == 0 || X == 7) {
                value -= 50;
            } else if (X == 1 || X == 6) {
                value -= 40;
            } else {
                value -= 30;
            }
        } else if (color == Color.WHITE) {
            if (Y == 1) {
                if (X == 0 || X == 7) {
                    value -= 40;
                } else if (X == 1 || X == 6) {
                    value -= 20;
                } else {
                    value += 0;
                }
            } else if (Y == 2) {
                if (X == 0 || X == 7) {
                    value -= 30;
                } else if (X == 1 || X == 6) {
                    value += 0;
                } else if (X == 2 || X == 5) {
                    value += 10;
                } else if (X == 3 || X == 4) {
                    value += 15;
                }
            } else if (Y == 3) {
                if (X == 0 || X == 7) {
                    value -= 30;
                } else if (X == 1 || X == 6) {
                    value += 5;
                } else if (X == 2 || X == 5) {
                    value += 15;
                } else if (X == 3 || X == 4) {
                    value += 20;
                }
            } else if (Y == 4) {
                if (X == 0 || X == 7) {
                    value -= 30;
                } else if (X == 1 || X == 6) {
                    value += 0;
                } else if (X == 2 || X == 5) {
                    value += 15;
                } else if (X == 3 || X == 4) {
                    value += 20;
                }
            } else if (Y == 5) {
                if (X == 0 || X == 7) {
                    value -= 30;
                } else if (X == 1 || X == 6) {
                    value += 5;
                } else if (X == 2 || X == 5) {
                    value += 10;
                } else if (X == 3 || X == 4) {
                    value += 15;
                }
            } else if (Y == 6) {
                if (X == 0 || X == 7) {
                    value -= 40;
                } else if (X == 1 || X == 6) {
                    value -= 20;
                } else if (X == 2 || X == 5) {
                    value += 0;
                } else if (X == 3 || X == 4) {
                    value += 5;
                }
            }
        } else if (Y == 6) {
            if (X == 0 || X == 7) {
                value -= 40;
            } else if (X == 1 || X == 6) {
                value -= 20;
            } else {
                value += 0;
            }
        } else if (Y == 5) {
            if (X == 0 || X == 7) {
                value -= 30;
            } else if (X == 1 || X == 6) {
                value += 0;
            } else if (X == 2 || X == 5) {
                value += 10;
            } else if (X == 3 || X == 4) {
                value += 15;
            }
        } else if (Y == 4) {
            if (X == 0 || X == 7) {
                value -= 30;
            } else if (X == 1 || X == 6) {
                value += 5;
            } else if (X == 2 || X == 5) {
                value += 15;
            } else if (X == 3 || X == 4) {
                value += 20;
            }
        } else if (Y == 3) {
            if (X == 0 || X == 7) {
                value -= 30;
            } else if (X == 1 || X == 6) {
                value += 0;
            } else if (X == 2 || X == 5) {
                value += 15;
            } else if (X == 3 || X == 4) {
                value += 20;
            }
        } else if (Y == 2) {
            if (X == 0 || X == 7) {
                value -= 30;
            } else if (X == 1 || X == 6) {
                value += 5;
            } else if (X == 2 || X == 5) {
                value += 10;
            } else if (X == 3 || X == 4) {
                value += 15;
            }
        } else if (Y == 1) {
            if (X == 0 || X == 7) {
                value -= 40;
            } else if (X == 1 || X == 6) {
                value -= 20;
            } else if (X == 2 || X == 5) {
                value += 0;
            } else if (X == 3 || X == 4) {
                value += 5;
            }
        }
        return value;
    }

    @Override
    public Piece clone() {
        return new Knight(this.color).setFirstMove(this.firstMove);
    }

    @Override
    public String toString() {
        return (this.color == Color.BLACK)? "\u2658" : "\u265E";
    }
}
