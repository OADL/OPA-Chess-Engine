package opa.chess.Models.Pieces;

import opa.chess.Enums.Color;
import opa.chess.Enums.MoveType;
import opa.chess.Enums.PieceType;
import opa.chess.Models.Board;
import opa.chess.Models.Move;
import opa.chess.Models.Square;

import java.util.List;

import static opa.chess.Config.CommonMethods.isOutOfBounds;

public abstract class Piece {

    protected PieceType type;
    protected Color color;    //False White True black
    protected boolean firstMove;

    protected Piece(Color color, PieceType type) {
        this.firstMove = true;
        this.color = color;
        this.type = type;
    }

    public PieceType getType() {
        return type;
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

    public Piece setColor(Color color) {
        this.color = color;
        return this;
    }

    public Piece setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
        return this;
    }

    protected boolean pieceOnSquare(int x, int y, Square[][] squares) {
        return squares[x][y].getPiece() != null;
    }

    public boolean canEat(Piece piece) {
        return piece.getColor() != this.color;
    }

    public abstract MoveType checkMove(Square source, Square destination, Board board);

    protected boolean blocked(int x2, int y2, Square square, Square[][] squares) {
        Piece piece = squares[x2][y2].getPiece();
        return (piece != null && piece.getColor() == this.color);
    };

    public abstract int evaluate(Square square);

    public abstract List<Move> nextMoves(Square square, Board board);

    protected Move getMove(Square square, Board board, int newX, int newY) {
        if(isOutOfBounds(newX,newY)) return null;
        Board tempBoard = board.copy();
        Move move = new Move(square.getPiece(), square, board.findSquare(newX, newY), MoveType.NORMAL);
        if (tempBoard.processMove(move)) {
            return move;
        }
        return null;
    }

    public abstract Piece clone();

    public abstract String toString();

    public boolean equals(Piece piece) {
        return (this.type == piece.getType() && this.color == piece.getColor());
    }
}
