package opa.chess;

import java.util.ArrayList;

public class AI {

    public String alphabeta(int player, Board b, int alpha, int beta, int depth) {
        String best_move = null;
        if (Thread.currentThread().isInterrupted()) {
            return best_move;
        }
        if (CommonMethods.stop) {
            return best_move;
        }
        Board temp_board = new Board();
        temp_board = temp_board.boardcopy(b, temp_board);
        int temp_player = CommonMethods.player;
        boolean temp_castl = CommonMethods.castling;
        boolean temp_en_pt = CommonMethods.en_passant;
        boolean temp_w_k_c = CommonMethods.white_king_checked;
        boolean temp_w_b_c = CommonMethods.black_king_checked;
        CommonMethods.player = player;
        ArrayList<String> moves = b.next_moves(player);
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
            temp_board = temp_board.boardcopy(b, temp_board);
            if (best_move == null) {
                best_move = m;
            }
            int score;
            CommonMethods.player = player;
            temp_board.apply_move(m);
            CommonMethods.player = temp_player;
            if (CommonMethods.player == player) {
                if (depth <= 0) {
                    score = evaluate(temp_board, player);
                } else {
                    String returned;
                    if (player == 1) {
                        returned = alphabeta(2, temp_board, alpha, beta, depth - 1);
                        CommonMethods.player = 2;
                        temp_board.apply_move(returned);
                        CommonMethods.player = temp_player;
                        score = evaluate(temp_board, player);
                    } else {
                        returned = alphabeta(1, temp_board, alpha, beta, depth - 1);
                        CommonMethods.player = 1;
                        temp_board.apply_move(returned);
                        CommonMethods.player = temp_player;
                        score = evaluate(temp_board, player);
                    }
                }
                if ((CommonMethods.player == 2 && CommonMethods.white_king_checked == true) || (CommonMethods.player == 1 && CommonMethods.black_king_checked == true)) {
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
                        returned = alphabeta(2, temp_board, alpha, beta, depth - 1);
                        CommonMethods.player = 2;
                        temp_board.apply_move(returned);
                        CommonMethods.player = temp_player;
                        score = evaluate(temp_board, player);
                    } else {
                        returned = alphabeta(1, temp_board, alpha, beta, depth - 1);
                        CommonMethods.player = 1;
                        temp_board.apply_move(returned);
                        CommonMethods.player = temp_player;
                        score = evaluate(temp_board, player);
                    }
                }
                if ((CommonMethods.player == 1 && CommonMethods.white_king_checked == true) || (CommonMethods.player == 2 && CommonMethods.black_king_checked == true)) {
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

    public int evaluate(Board b, int player_turn) {
        int sum_up = 0, sum_down = 0, piece_value = 0, piece_y = 0, piece_x = 0;
        for (int i = 0; i < b.pieces.size(); i++) {
            Piece p = b.pieces.get(i);
            piece_y = p.get_y();
            piece_x = p.get_x();
            piece_value = 0;
            switch (p.get_type()) {
                case "Pawn":
                    piece_value += 100;
                    if (piece_y == 0 || piece_y == 7) {
                        piece_value += 0;
                    } else if (p.get_location() == false /*down*/) {
                        if (piece_y == 1) {
                            piece_value += 50;
                        } else if (piece_y == 2) {
                            if (piece_x == 0 || piece_x == 1 || piece_x == 6 || piece_x == 7) {
                                piece_value += 10;
                            } else if (piece_x == 2 || piece_x == 5) {
                                piece_value += 20;
                            } else if (piece_x == 3 || piece_x == 4) {
                                piece_value += 30;
                            }
                        } else if (piece_y == 3) {
                            if (piece_x == 0 || piece_x == 1 || piece_x == 6 || piece_x == 7) {
                                piece_value += 5;
                            } else if (piece_x == 2 || piece_x == 5) {
                                piece_value += 10;
                            } else if (piece_x == 3 || piece_x == 4) {
                                piece_value += 25;
                            }
                        } else if (piece_y == 4) {
                            if (piece_x == 0 || piece_x == 1 || piece_x == 2 || piece_x == 5 || piece_x == 6 || piece_x == 7) {
                                piece_value += 0;
                            } else {
                                piece_value += 20;
                            }
                        } else if (piece_y == 5) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value += 5;
                            } else if (piece_x == 1 || piece_x == 6) {
                                piece_value -= 5;
                            } else if (piece_x == 2 || piece_x == 5) {
                                piece_value -= 10;
                            } else if (piece_x == 3 || piece_x == 4) {
                                piece_value += 0;
                            }
                        } else if (piece_y == 6) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value += 5;
                            } else if (piece_x == 1 || piece_x == 2 || piece_x == 5 || piece_x == 6) {
                                piece_value += 10;
                            } else if (piece_x == 3 || piece_x == 4) {
                                piece_value -= 20;
                            }
                        }
                    } else if (piece_y == 6) {
                        piece_value += 50;
                    } else if (piece_y == 5) {
                        if (piece_x == 0 || piece_x == 1 || piece_x == 6 || piece_x == 7) {
                            piece_value += 10;
                        } else if (piece_x == 2 || piece_x == 5) {
                            piece_value += 20;
                        } else if (piece_x == 3 || piece_x == 4) {
                            piece_value += 30;
                        }
                    } else if (piece_y == 4) {
                        if (piece_x == 0 || piece_x == 1 || piece_x == 6 || piece_x == 7) {
                            piece_value += 5;
                        } else if (piece_x == 2 || piece_x == 5) {
                            piece_value += 10;
                        } else if (piece_x == 3 || piece_x == 4) {
                            piece_value += 25;
                        }
                    } else if (piece_y == 3) {
                        if (piece_x == 0 || piece_x == 1 || piece_x == 2 || piece_x == 5 || piece_x == 6 || piece_x == 7) {
                            piece_value += 0;
                        } else {
                            piece_value += 20;
                        }
                    } else if (piece_y == 2) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value += 5;
                        } else if (piece_x == 1 || piece_x == 6) {
                            piece_value -= 5;
                        } else if (piece_x == 2 || piece_x == 5) {
                            piece_value -= 10;
                        } else if (piece_x == 3 || piece_x == 4) {
                            piece_value += 0;
                        }
                    } else if (piece_y == 1) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value += 5;
                        } else if (piece_x == 1 || piece_x == 2 || piece_x == 5 || piece_x == 6) {
                            piece_value += 10;
                        } else if (piece_x == 3 || piece_x == 4) {
                            piece_value -= 20;
                        }
                    }
                    break;
                case "Knight":
                    piece_value += 320;
                    if (piece_y == 0 || piece_y == 7) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 50;
                        } else if (piece_x == 1 || piece_x == 6) {
                            piece_value -= 40;
                        } else {
                            piece_value -= 30;
                        }
                    } else if (p.get_location() == false /*down*/) {
                        if (piece_y == 1) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 40;
                            } else if (piece_x == 1 || piece_x == 6) {
                                piece_value -= 20;
                            } else {
                                piece_value += 0;
                            }
                        } else if (piece_y == 2) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 30;
                            } else if (piece_x == 1 || piece_x == 6) {
                                piece_value += 0;
                            } else if (piece_x == 2 || piece_x == 5) {
                                piece_value += 10;
                            } else if (piece_x == 3 || piece_x == 4) {
                                piece_value += 15;
                            }
                        } else if (piece_y == 3) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 30;
                            } else if (piece_x == 1 || piece_x == 6) {
                                piece_value += 5;
                            } else if (piece_x == 2 || piece_x == 5) {
                                piece_value += 15;
                            } else if (piece_x == 3 || piece_x == 4) {
                                piece_value += 20;
                            }
                        } else if (piece_y == 4) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 30;
                            } else if (piece_x == 1 || piece_x == 6) {
                                piece_value += 0;
                            } else if (piece_x == 2 || piece_x == 5) {
                                piece_value += 15;
                            } else if (piece_x == 3 || piece_x == 4) {
                                piece_value += 20;
                            }
                        } else if (piece_y == 5) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 30;
                            } else if (piece_x == 1 || piece_x == 6) {
                                piece_value += 5;
                            } else if (piece_x == 2 || piece_x == 5) {
                                piece_value += 10;
                            } else if (piece_x == 3 || piece_x == 4) {
                                piece_value += 15;
                            }
                        } else if (piece_y == 6) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 40;
                            } else if (piece_x == 1 || piece_x == 6) {
                                piece_value -= 20;
                            } else if (piece_x == 2 || piece_x == 5) {
                                piece_value += 0;
                            } else if (piece_x == 3 || piece_x == 4) {
                                piece_value += 5;
                            }
                        }
                    } else if (piece_y == 6) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 40;
                        } else if (piece_x == 1 || piece_x == 6) {
                            piece_value -= 20;
                        } else {
                            piece_value += 0;
                        }
                    } else if (piece_y == 5) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 30;
                        } else if (piece_x == 1 || piece_x == 6) {
                            piece_value += 0;
                        } else if (piece_x == 2 || piece_x == 5) {
                            piece_value += 10;
                        } else if (piece_x == 3 || piece_x == 4) {
                            piece_value += 15;
                        }
                    } else if (piece_y == 4) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 30;
                        } else if (piece_x == 1 || piece_x == 6) {
                            piece_value += 5;
                        } else if (piece_x == 2 || piece_x == 5) {
                            piece_value += 15;
                        } else if (piece_x == 3 || piece_x == 4) {
                            piece_value += 20;
                        }
                    } else if (piece_y == 3) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 30;
                        } else if (piece_x == 1 || piece_x == 6) {
                            piece_value += 0;
                        } else if (piece_x == 2 || piece_x == 5) {
                            piece_value += 15;
                        } else if (piece_x == 3 || piece_x == 4) {
                            piece_value += 20;
                        }
                    } else if (piece_y == 2) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 30;
                        } else if (piece_x == 1 || piece_x == 6) {
                            piece_value += 5;
                        } else if (piece_x == 2 || piece_x == 5) {
                            piece_value += 10;
                        } else if (piece_x == 3 || piece_x == 4) {
                            piece_value += 15;
                        }
                    } else if (piece_y == 1) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 40;
                        } else if (piece_x == 1 || piece_x == 6) {
                            piece_value -= 20;
                        } else if (piece_x == 2 || piece_x == 5) {
                            piece_value += 0;
                        } else if (piece_x == 3 || piece_x == 4) {
                            piece_value += 5;
                        }
                    }
                    break;
                case "Bishop":
                    piece_value += 330;
                    if (piece_y == 0 || piece_y == 7) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 20;
                        } else {
                            piece_value -= 10;
                        }
                    } else if (p.get_location() == false /*down*/) {
                        if (piece_y == 1) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 10;
                            } else {
                                piece_value += 0;
                            }
                        } else if (piece_y == 2) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 10;
                            } else if (piece_x == 1 || piece_x == 6) {
                                piece_value += 0;
                            } else if (piece_x == 2 || piece_x == 5) {
                                piece_value += 5;
                            } else if (piece_x == 3 || piece_x == 4) {
                                piece_value += 10;
                            }
                        } else if (piece_y == 3) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 10;
                            } else if (piece_x == 1 || piece_x == 2 || piece_x == 5 || piece_x == 6) {
                                piece_value += 5;
                            } else if (piece_x == 3 || piece_x == 4) {
                                piece_value += 10;
                            }
                        } else if (piece_y == 4) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 10;
                            } else if (piece_x == 1 || piece_x == 6) {
                                piece_value += 0;
                            } else {
                                piece_value += 10;
                            }
                        } else if (piece_y == 5) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 10;
                            } else {
                                piece_value += 10;
                            }
                        } else if (piece_y == 6) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 10;
                            } else if (piece_x == 1 || piece_x == 6) {
                                piece_value += 5;
                            } else {
                                piece_value += 0;
                            }
                        }
                    } else if (piece_y == 6) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 10;
                        } else {
                            piece_value += 0;
                        }
                    } else if (piece_y == 5) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 10;
                        } else if (piece_x == 1 || piece_x == 6) {
                            piece_value += 0;
                        } else if (piece_x == 2 || piece_x == 5) {
                            piece_value += 5;
                        } else if (piece_x == 3 || piece_x == 4) {
                            piece_value += 10;
                        }
                    } else if (piece_y == 4) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 10;
                        } else if (piece_x == 1 || piece_x == 2 || piece_x == 5 || piece_x == 6) {
                            piece_value += 5;
                        } else if (piece_x == 3 || piece_x == 4) {
                            piece_value += 10;
                        }
                    } else if (piece_y == 3) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 10;
                        } else if (piece_x == 1 || piece_x == 6) {
                            piece_value += 0;
                        } else {
                            piece_value += 10;
                        }
                    } else if (piece_y == 2) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 10;
                        } else {
                            piece_value += 10;
                        }
                    } else if (piece_y == 1) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 10;
                        } else if (piece_x == 1 || piece_x == 6) {
                            piece_value += 5;
                        } else {
                            piece_value += 0;
                        }
                    }
                    break;
                case "Rook":
                    piece_value += 500;
                    if (p.get_location() == false /*down*/) {
                        if (piece_y == 0) {
                            piece_value += 0;
                        } else if (piece_y == 1) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value += 5;
                            } else {
                                piece_value += 10;
                            }
                        } else if (piece_y == 7) {
                            if (piece_x == 3 || piece_x == 4) {
                                piece_value += 5;
                            } else {
                                piece_value += 0;
                            }
                        } else if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 5;
                        } else {
                            piece_value += 0;
                        }
                    } else if (piece_y == 7) {
                        piece_value += 0;
                    } else if (piece_y == 6) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value += 5;
                        } else {
                            piece_value += 10;
                        }
                    } else if (piece_y == 0) {
                        if (piece_x == 3 || piece_x == 4) {
                            piece_value += 5;
                        } else {
                            piece_value += 0;
                        }
                    } else if (piece_x == 0 || piece_x == 7) {
                        piece_value -= 5;
                    } else {
                        piece_value += 0;
                    }
                    break;
                case "Queen":
                    piece_value += 900;
                    if (piece_y == 0 || piece_y == 7) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 20;
                        } else if (piece_x == 1 || piece_x == 2 || piece_x == 5 || piece_x == 6) {
                            piece_value -= 10;
                        } else {
                            piece_value -= 5;
                        }
                    }
                    if (p.get_location() == false /*down*/) {
                        if (piece_y == 1) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 10;
                            } else {
                                piece_value -= 0;
                            }
                        } else if (piece_y == 2) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 10;
                            } else if (piece_x == 1 || piece_x == 6) {
                                piece_value += 0;
                            } else {
                                piece_value += 5;
                            }
                        } else if (piece_y == 3) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 5;
                            } else if (piece_x == 1 || piece_x == 6) {
                                piece_value += 0;
                            } else {
                                piece_value += 5;
                            }
                        } else if (piece_y == 4) {
                            if (piece_x == 7) {
                                piece_value -= 5;
                            } else if (piece_x == 2 || piece_x == 3 || piece_x == 4 || piece_x == 5) {
                                piece_value += 5;
                            } else {
                                piece_value += 0;
                            }
                        } else if (piece_y == 5) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 10;
                            } else if (piece_x == 6) {
                                piece_value += 0;
                            } else {
                                piece_value += 5;
                            }
                        } else if (piece_y == 6) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 10;
                            } else if (piece_x == 2) {
                                piece_value += 5;
                            } else {
                                piece_value += 0;
                            }
                        }
                    } else if (piece_y == 6) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 10;
                        } else {
                            piece_value -= 0;
                        }
                    } else if (piece_y == 5) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 10;
                        } else if (piece_x == 1 || piece_x == 6) {
                            piece_value += 0;
                        } else {
                            piece_value += 5;
                        }
                    } else if (piece_y == 4) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 5;
                        } else if (piece_x == 1 || piece_x == 6) {
                            piece_value += 0;
                        } else {
                            piece_value += 5;
                        }
                    } else if (piece_y == 3) {
                        if (piece_x == 0) {
                            piece_value -= 5;
                        } else if (piece_x == 2 || piece_x == 3 || piece_x == 4 || piece_x == 5) {
                            piece_value += 5;
                        } else {
                            piece_value += 0;
                        }
                    } else if (piece_y == 2) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 10;
                        } else if (piece_x == 1) {
                            piece_value += 0;
                        } else {
                            piece_value += 5;
                        }
                    } else if (piece_y == 1) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 10;
                        } else if (piece_x == 5) {
                            piece_value += 5;
                        } else {
                            piece_value += 0;
                        }
                    }
                    break;
                case "King":
                    piece_value += 20000;
                    if (p.get_location() == false /*down*/) {
                        if (piece_y == 0 || piece_y == 1 || piece_y == 2 || piece_y == 3) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 30;
                            } else if (piece_x == 3 || piece_x == 4) {
                                piece_value -= 50;
                            } else {
                                piece_value -= 40;
                            }
                        } else if (piece_y == 4) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 20;
                            } else if (piece_x == 3 || piece_x == 4) {
                                piece_value -= 40;
                            } else {
                                piece_value -= 30;
                            }
                        } else if (piece_y == 5) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value -= 10;
                            } else {
                                piece_value -= 20;
                            }
                        } else if (piece_y == 6) {
                            if (piece_x == 0 || piece_x == 7 || piece_y == 1 || piece_y == 6) {
                                piece_value += 20;
                            } else {
                                piece_value += 0;
                            }
                        } else if (piece_y == 7) {
                            if (piece_x == 0 || piece_x == 7) {
                                piece_value += 20;
                            } else if (piece_x == 1 || piece_x == 6) {
                                piece_value += 30;
                            } else if (piece_x == 2 || piece_x == 5) {
                                piece_value += 10;
                            } else {
                                piece_value += 0;
                            }
                        }
                    } else if (piece_y == 7 || piece_y == 6 || piece_y == 5 || piece_y == 4) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 30;
                        } else if (piece_x == 3 || piece_x == 4) {
                            piece_value -= 50;
                        } else {
                            piece_value -= 40;
                        }
                    } else if (piece_y == 3) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 20;
                        } else if (piece_x == 3 || piece_x == 4) {
                            piece_value -= 40;
                        } else {
                            piece_value -= 30;
                        }
                    } else if (piece_y == 2) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value -= 10;
                        } else {
                            piece_value -= 20;
                        }
                    } else if (piece_y == 1) {
                        if (piece_x == 0 || piece_x == 7 || piece_y == 1 || piece_y == 6) {
                            piece_value += 20;
                        } else {
                            piece_value += 0;
                        }
                    } else if (piece_y == 0) {
                        if (piece_x == 0 || piece_x == 7) {
                            piece_value += 20;
                        } else if (piece_x == 1 || piece_x == 6) {
                            piece_value += 30;
                        } else if (piece_x == 2 || piece_x == 5) {
                            piece_value += 10;
                        } else {
                            piece_value += 0;
                        }
                    }
                    break;
            }
            if (p.get_location() == false) {
                /*white*/
                sum_down += piece_value;
            } else {
                /*black*/
                sum_up += piece_value;
            }
        }
        if (player_turn == 1) {
            return sum_down - sum_up;
        } else if (player_turn == 2) {
            return sum_up - sum_down;
        }
        return -1;
    }
}
