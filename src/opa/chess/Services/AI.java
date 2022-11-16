package opa.chess.Services;

import opa.chess.Config.Configurations;
import opa.chess.Enums.Color;
import opa.chess.Models.Board;
import opa.chess.Models.Pieces.Piece;
import opa.chess.Models.Square;

import java.util.ArrayList;

public class AI {

    public String alphaBeta(int player, Board b, int alpha, int beta, int depth) {
        String best_move = null;
        if (Thread.currentThread().isInterrupted()) {
            return best_move;
        }
        if (Configurations.stop) {
            return best_move;
        }
        int temp_player = Configurations.player;
        Configurations.player = player;
        ArrayList<String> moves = b.nextMoves(player);
        Configurations.player = temp_player;
        if (moves.isEmpty()) {
            return null;
        }
        for (String m : moves) {
            Configurations.player = temp_player;
            Board temp_board = b.copy();
            if (best_move == null) {
                best_move = m;
            }
            int score;
            Configurations.player = player;
            temp_board.handleMove(m);
            Configurations.player = temp_player;
            if (Configurations.player == player) {
                if (depth <= 0) {
                    score = evaluate(temp_board, player);
                } else {
                    String returned;
                    if (player == 1) {
                        returned = alphaBeta(2, temp_board, alpha, beta, depth - 1);
                        Configurations.player = 2;
                        temp_board.handleMove(returned);
                        Configurations.player = temp_player;
                        score = evaluate(temp_board, player);
                    } else {
                        returned = alphaBeta(1, temp_board, alpha, beta, depth - 1);
                        Configurations.player = 1;
                        temp_board.handleMove(returned);
                        Configurations.player = temp_player;
                        score = evaluate(temp_board, player);
                    }
                }
                if ((Configurations.player == 2 && b.isKingChecked(Color.WHITE)) || (Configurations.player == 1 && b.isKingChecked(Color.BLACK))) {
                    Configurations.player = temp_player;
                    best_move = m;
                    return best_move;
                }
                if (score > alpha) {
                    best_move = m;
                    alpha = score;
                }
                if (beta <= alpha) {
                    Configurations.player = temp_player;
                    best_move = m;
                    return best_move;
                }
            } else {
                if (depth <= 0) {
                    score = evaluate(temp_board, player);
                } else {
                    String returned;
                    if (player == 1) {
                        returned = alphaBeta(2, temp_board, alpha, beta, depth - 1);
                        Configurations.player = 2;
                        temp_board.handleMove(returned);
                        Configurations.player = temp_player;
                        score = evaluate(temp_board, player);
                    } else {
                        returned = alphaBeta(1, temp_board, alpha, beta, depth - 1);
                        Configurations.player = 1;
                        temp_board.handleMove(returned);
                        Configurations.player = temp_player;
                        score = evaluate(temp_board, player);
                    }
                }
                if ((Configurations.player == 1 && b.isKingChecked(Color.WHITE)) || (Configurations.player == 2 && b.isKingChecked(Color.BLACK))) {
                    Configurations.player = temp_player;
                    best_move = m;
                    return best_move;
                }
                if (score < beta) {
                    best_move = m;
                    beta = score;
                }
                if (beta <= alpha) {
                    Configurations.player = temp_player;
                    return best_move;
                }
            }
            if (Thread.currentThread().isInterrupted()) {
                return best_move;
            }
            if (Configurations.stop) {
                return best_move;
            }
        }
        Configurations.player = temp_player;
        return best_move;
    }

    private int evaluate(Board board, int playerTurn) {
        int sumWhite = 0, sumBlack = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square square = board.getSquares()[i][j];
                Piece piece = square.getPiece();
                if(piece == null) continue;
                int pieceValue = piece.evaluate(square);
                if (piece.getColor() == Color.WHITE) {
                    sumBlack += pieceValue;
                } else {
                    sumWhite += pieceValue;
                }
            }
        }
        if (playerTurn == 1) {
            return sumBlack - sumWhite;
        } else if (playerTurn == 2) {
            return sumWhite - sumBlack;
        }
        return -1;
    }
}
