package opa.chess.Models;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.MoveType;
import opa.chess.Models.Pieces.Piece;

public class Move {
    private final Piece piece;
    private final Square source;
    private final Square destination;
    private final MoveType moveType;

    public Move(Piece piece, Square source, Square destination, MoveType moveType) {
        this.piece = piece;
        this.source = source;
        this.destination = destination;
        this.moveType = moveType;
    }

    public Piece getPiece() {
        return piece;
    }

    public Square getSource() {
        return source;
    }

    public Square getDestination() {
        return destination;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    @Override
    public String toString() {
        String move = "";
        move += (char) (source.getX() + 97);
        move += 8 - destination.getX();
        move += (char) (source.getY() + 97);
        move += 8 - destination.getY();
        return move;
    }
}