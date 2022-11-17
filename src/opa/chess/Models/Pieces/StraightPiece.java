package opa.chess.Models.Pieces;

import opa.chess.Enums.MoveType;
import opa.chess.Models.Board;
import opa.chess.Models.Move;
import opa.chess.Models.Square;

import java.util.ArrayList;
import java.util.List;

import static opa.chess.Config.CommonMethods.isOutOfBounds;

public interface StraightPiece {

    default List<Move> possibleStraightMoves(boolean isHorizontal, Square square, Board board){
        List<Move> moves = new ArrayList<>();
        Board tempBoard;
        int position = (isHorizontal)? square.getX(): square.getY();
        int tempPosition = position - 1;
        Move move;
        while (true) {
            if (tempPosition <= -1) {
                tempPosition = position + 1;
            }
            if (tempPosition >= 8) break;
            if((isHorizontal && isOutOfBounds(tempPosition, square.getY())) || (!isHorizontal && isOutOfBounds(square.getX(),tempPosition))) break;
            move = (isHorizontal)?
                    new Move(square.getPiece(), square, board.findSquare(tempPosition, square.getY()), MoveType.NORMAL)
                    : new Move(square.getPiece(), square, board.findSquare(square.getX(), tempPosition), MoveType.NORMAL);
            if (tempPosition < position) {
                tempBoard = board.copy();
                if (tempBoard.processMove(move)) {
                    moves.add(move);
                }
                tempPosition--;
            } else if (tempPosition > position) {
                tempBoard = board.copy();
                if (tempBoard.processMove(move)) {
                    moves.add(move);
                }
                tempPosition++;
            }
        }
        return moves;
    }

}
