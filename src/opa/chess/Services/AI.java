package opa.chess.Services;

import opa.chess.Enums.Color;
import opa.chess.Models.Board;
import opa.chess.Config.CommonMethods;
import opa.chess.Models.Pieces.Piece;

import java.util.ArrayList;

public class AI {

    public String alphaBeta(int player, Board b, int alpha, int beta, int depth) {
        String best_move = null;
        if (Thread.currentThread().isInterrupted()) {
            return best_move;
        }
        if (CommonMethods.stop) {
            return best_move;
        }
        Board temp_board = b.copy();
        int temp_player = CommonMethods.player;
        boolean temp_castl = CommonMethods.castling;
        boolean temp_en_pt = CommonMethods.en_passant;
        boolean temp_w_k_c = CommonMethods.white_king_checked;
        boolean temp_w_b_c = CommonMethods.black_king_checked;
        CommonMethods.player = player;
        ArrayList<String> moves = b.nextMoves(player);
        CommonMethods.player = temp_player;
        if (moves.isEmpty()) {
            return null;
        }
        for (String m : moves) {
            CommonMethods.player = temp_player;
            CommonMethods.en_passant = temp_en_pt;
            CommonMethods.castling = temp_castl;
            CommonMethods.white_king_checked = temp_w_k_c;
            CommonMethods.black_king_checked = temp_w_b_c;
            temp_board = b.copy();
            if (best_move == null) {
                best_move = m;
            }
            int score;
            CommonMethods.player = player;
            temp_board.applyMove(m);
            CommonMethods.player = temp_player;
            if (CommonMethods.player == player) {
                if (depth <= 0) {
                    score = evaluate(temp_board, player);
                } else {
                    String returned;
                    if (player == 1) {
                        returned = alphaBeta(2, temp_board, alpha, beta, depth - 1);
                        CommonMethods.player = 2;
                        temp_board.applyMove(returned);
                        CommonMethods.player = temp_player;
                        score = evaluate(temp_board, player);
                    } else {
                        returned = alphaBeta(1, temp_board, alpha, beta, depth - 1);
                        CommonMethods.player = 1;
                        temp_board.applyMove(returned);
                        CommonMethods.player = temp_player;
                        score = evaluate(temp_board, player);
                    }
                }
                if ((CommonMethods.player == 2 && CommonMethods.white_king_checked) || (CommonMethods.player == 1 && CommonMethods.black_king_checked)) {
                    CommonMethods.player = temp_player;
                    CommonMethods.en_passant = temp_en_pt;
                    CommonMethods.castling = temp_castl;
                    CommonMethods.white_king_checked = temp_w_k_c;
                    CommonMethods.black_king_checked = temp_w_b_c;
                    best_move = m;
                    return best_move;
                }
                if (score > alpha) {
                    best_move = m;
                    alpha = score;
                }
                if (beta <= alpha) {
                    CommonMethods.player = temp_player;
                    CommonMethods.en_passant = temp_en_pt;
                    CommonMethods.castling = temp_castl;
                    CommonMethods.white_king_checked = temp_w_k_c;
                    CommonMethods.black_king_checked = temp_w_b_c;
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
                        CommonMethods.player = 2;
                        temp_board.applyMove(returned);
                        CommonMethods.player = temp_player;
                        score = evaluate(temp_board, player);
                    } else {
                        returned = alphaBeta(1, temp_board, alpha, beta, depth - 1);
                        CommonMethods.player = 1;
                        temp_board.applyMove(returned);
                        CommonMethods.player = temp_player;
                        score = evaluate(temp_board, player);
                    }
                }
                if ((CommonMethods.player == 1 && CommonMethods.white_king_checked) || (CommonMethods.player == 2 && CommonMethods.black_king_checked)) {
                    CommonMethods.player = temp_player;
                    CommonMethods.en_passant = temp_en_pt;
                    CommonMethods.castling = temp_castl;
                    CommonMethods.white_king_checked = temp_w_k_c;
                    CommonMethods.black_king_checked = temp_w_b_c;
                    best_move = m;
                    return best_move;
                }
                if (score < beta) {
                    best_move = m;
                    beta = score;
                }
                if (beta <= alpha) {
                    CommonMethods.player = temp_player;
                    CommonMethods.en_passant = temp_en_pt;
                    CommonMethods.castling = temp_castl;
                    CommonMethods.white_king_checked = temp_w_k_c;
                    CommonMethods.black_king_checked = temp_w_b_c;
                    return best_move;
                }
            }
            if (Thread.currentThread().isInterrupted()) {
                return best_move;
            }
            if (CommonMethods.stop) {
                return best_move;
            }
        }
        CommonMethods.player = temp_player;
        CommonMethods.en_passant = temp_en_pt;
        CommonMethods.castling = temp_castl;
        CommonMethods.white_king_checked = temp_w_k_c;
        CommonMethods.black_king_checked = temp_w_b_c;
        return best_move;
    }

    public int evaluate(Board b, int playerTurn) {
        int sumWhite = 0, sumBlack = 0;
        for (Piece piece: b.getPieces()) {
            int pieceValue = piece.evaluate();
            if (piece.getColor() == Color.WHITE) {
                sumBlack += pieceValue;
            } else {
                sumWhite += pieceValue;
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
