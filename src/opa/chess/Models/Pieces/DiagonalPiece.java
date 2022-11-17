package opa.chess.Models.Pieces;

import opa.chess.Enums.MoveType;
import opa.chess.Models.Board;
import opa.chess.Models.Move;
import opa.chess.Models.Square;

import java.util.ArrayList;
import java.util.List;

import static opa.chess.Config.CommonMethods.isOutOfBounds;

public interface DiagonalPiece {

    default List<Move> possibleDiagonalMoves(boolean isLeft, Square square, Board board) {
        List<Move> moves = new ArrayList<>();
        Board tempBoard;
        Move move;
        int tempX;
        int tempY;
        tempX = (isLeft)? square.getX() - 1 : square.getX() + 1;
        tempY = square.getY() - 1;
        while (true) {
            if(isLeft) {
                if (tempX <= -1) {
                    tempX = square.getX() + 1;
                    tempY = square.getY() + 1;
                }
                if (tempX >= 8) break;
            }else{
                if (tempY <= -1) {
                    tempY = square.getY() + 1;
                    tempX = square.getX() - 1;
                }
                if (tempY >= 8) break;
            }
            if(isOutOfBounds(tempX,tempY)) break;
            move = new Move(square.getPiece(), square, board.findSquare(tempX,tempY), MoveType.NORMAL);
            if ((isLeft && tempX < square.getX()) || (!isLeft && tempY < square.getY())) {
                tempBoard = board.copy();
                if (tempBoard.processMove(move)) {
                    moves.add(move);
                }
                tempX= (isLeft)? tempX-1 : tempX+1;
                tempY--;
            } else if ((isLeft && tempX > square.getX()) || (!isLeft && tempY > square.getY())) {
                tempBoard = board.copy();
                if (tempBoard.processMove(move)) {
                    moves.add(move);
                }
                tempX= (isLeft)? tempX+1 : tempX-1;
                tempY++;
            }
        }
        return moves;
    }

}
