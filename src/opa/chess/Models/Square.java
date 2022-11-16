package opa.chess.Models;

import opa.chess.Models.Pieces.Piece;

public class Square {
    private Piece piece;
    private int X;
    private int Y;

    public Square(Piece piece, int x, int y) {
        this.piece = piece;
        X = x;
        Y = y;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public Square clone() {
        return new Square((this.piece != null)?this.piece.clone():null, this.X, this.Y);
    }

    public boolean equals(Square square) {
        return (this.X == square.getX() && this.Y == square.getY());
    }
}
