package opa.chess.Models.Pieces;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.Color;
import opa.chess.Enums.Location;
import opa.chess.Enums.PieceType;

import java.util.ArrayList;

public abstract class Piece {

    protected PieceType type;
    protected Location location; //False Down  True UP
    protected Color color;    //False White True black
    protected boolean firstMove;
    protected int X;
    protected int Y;

    protected Piece(Location location, Color color, int x, int y, PieceType type) {
        this.firstMove = true;
        this.location = location;
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

    public Location getLocation() {
        return location;
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

    public Piece setLocation(Location location) {
        this.location = location;
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

    public Piece canEat(int destX, int destY, ArrayList<Piece> pieces) {
        Piece returned = CommonMethods.getPieceOnSquare(destX, destY, pieces);
        if (returned != null) {
            if (returned.getColor() != this.color) {
                return returned;
            }
        }
        return null;
    }

    public abstract boolean checkMove(int x2, int y2, ArrayList<Piece> pieces);

    protected abstract boolean blocked(int x2, int y2, ArrayList<Piece> pieces);

    public abstract Piece clone();

    public abstract String toString();
}
