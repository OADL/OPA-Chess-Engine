package opa.chess.Models;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.MoveType;
import opa.chess.Enums.PieceType;
import opa.chess.Models.Pieces.*;

public class Move {
    private final Square source;
    private final Square destination;
    private Piece piece;
    private MoveType moveType;
    private PieceType promotion;

    public Move(String move) {
        if (move == null || move.length() < 4) throw new RuntimeException("WrongMove");

        int X1 = CommonMethods.convert(move.charAt(0)),
            Y1 = CommonMethods.convert(move.charAt(1)),
            X2 = CommonMethods.convert(move.charAt(2)),
            Y2 = CommonMethods.convert(move.charAt(3));

        if(X2 > 7 || X2 < 0 || Y2 > 7 || Y2 < 0) throw new RuntimeException("WrongMove");

        this.source = new Square(null, X1, Y1);
        this.destination = new Square(null, X2, Y2);
        if(move.length() == 5){
            moveType = MoveType.PROMOTION;
            setPromotion(move.charAt(4));
        }
    }

    public Move(Piece piece, Square source, Square destination, MoveType moveType) {
        this.piece = piece;
        this.source = source;
        this.destination = destination;
        this.moveType = moveType;
    }

    public Move(Piece piece, Square source, Square destination, MoveType moveType, PieceType promotion) {
        this.piece = piece;
        this.source = source;
        this.destination = destination;
        this.moveType = moveType;
        this.promotion = promotion;
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

    public PieceType getPromotion() {
        return promotion;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    public void setPromotion(PieceType promotion) {
        this.promotion = promotion;
    }

    private void setPromotion(char promotion) {
        switch (promotion) {
            case 'q', 'Q' -> this.promotion = PieceType.QUEEN;
            case 'r', 'R' -> this.promotion = PieceType.ROOK;
            case 'n', 'N' -> this.promotion = PieceType.KNIGHT;
            case 'b', 'B' -> this.promotion = PieceType.BISHOP;
            default -> this.promotion = null;
        };
    }

    @Override
    public String toString() {
        String move = "";
        move += (char) (source.getX() + 97);
        move += 8 - destination.getX();
        move += (char) (source.getY() + 97);
        move += 8 - destination.getY();

        if(moveType == MoveType.PROMOTION){
            switch (promotion) {
                case QUEEN -> move += 'q';
                case ROOK -> move += 'r';
                case KNIGHT -> move += 'n';
                case BISHOP -> move += 'b';
            }
        }
        return move;
    }
}