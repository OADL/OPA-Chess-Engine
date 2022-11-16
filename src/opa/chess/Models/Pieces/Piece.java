package opa.chess.Models.Pieces;

import opa.chess.Enums.Color;
import opa.chess.Enums.PieceType;
import opa.chess.Models.Board;

import java.util.ArrayList;

public abstract class Piece {

    protected PieceType type;
    protected Color color;    //False White True black
    protected boolean firstMove;
    protected int X;
    protected int Y;

    protected Piece(Color color, int x, int y, PieceType type) {
        this.firstMove = true;
        this.color = color;
        this.X = x;
        this.Y = y;
        this.type = type;
    }

    public PieceType getType() {
        return type;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public Color getColor() {
        return color;
    }

    public boolean isFirstMove() {
        return this.firstMove;
    }

    public Piece setType(PieceType type) {
        this.type = type;
        return this;
    }

    public Piece setX(int X) {
        this.X = X;
        return this;
    }

    public Piece setY(int Y) {
        this.Y = Y;
        return this;
    }

    public Piece setColor(Color color) {
        this.color = color;
        return this;
    }

    public Piece setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
        return this;
    }

    protected boolean pieceOnSquare(int x, int y, ArrayList<Piece> pieces) {
        for (Piece piece : pieces) {
            if (piece.getX() == x && piece.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public boolean canEat(Piece piece) {
        return piece.getColor() != this.color;
    }

    public abstract boolean checkMove(int x2, int y2, Board board);

    protected abstract boolean blocked(int x2, int y2, ArrayList<Piece> pieces);

    public abstract int evaluate();

    public abstract Piece clone();

    public abstract String toString();
}
