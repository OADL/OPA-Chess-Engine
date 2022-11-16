package opa.chess.Models.Pieces;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.Color;
import opa.chess.Models.Board;

import java.util.ArrayList;

import static opa.chess.Enums.PieceType.KNIGHT;

public class Knight extends Piece {

    public Knight(Color color, int x, int y) {
        super(color, x, y, KNIGHT);
    }

    @Override
    public boolean checkMove(int x2, int y2, Board board) {
        if (x2 == this.X && y2 == this.Y) { //checks if not moved
            return false;
        }
        if (x2 < 0 || x2 > 7 || y2 > 7 || y2 < 0) { //checks if out of boundary
            return false;
        }
        if ((y2 == this.Y + 2 || y2 == this.Y - 2) && (x2 == this.X + 1 || x2 == this.X - 1)) {
            CommonMethods.en_passant = false;
            return (!blocked(x2, y2, board.getPieces()));
        } else if ((y2 == this.Y + 1 || y2 == this.Y - 1) && (x2 == this.X + 2 || x2 == this.X - 2)) {
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
        int value = 320;
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
        return new Knight(this.color, this.X, this.Y).setFirstMove(this.firstMove);
    }

    @Override
    public String toString() {
        return (this.color == Color.BLACK)? "\u2658" : "\u265E";
    }
}
