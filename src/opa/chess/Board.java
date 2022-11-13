package opa.chess;

import java.util.ArrayList;
import java.io.UnsupportedEncodingException;
import java.io.PrintStream;

public class Board {

    String[][] board = new String[][]{
        {" ", " ", " ", " ", " ", " ", " ", " "},
        {" ", " ", " ", " ", " ", " ", " ", " "},
        {" ", " ", " ", " ", " ", " ", " ", " "},
        {" ", " ", " ", " ", " ", " ", " ", " "},
        {" ", " ", " ", " ", " ", " ", " ", " "},
        {" ", " ", " ", " ", " ", " ", " ", " "},
        {" ", " ", " ", " ", " ", " ", " ", " "},
        {" ", " ", " ", " ", " ", " ", " ", " "}};

    public ArrayList<Piece> pieces = new ArrayList<Piece>();
    public ArrayList<Piece> dead = new ArrayList<Piece>();

    public Board() {
        board_init();
    }

    public Board boardcopy(Board b, Board temp_board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                temp_board.board[i][j] = b.board[i][j];
            }
        }
        if (!b.pieces.isEmpty()) {
            temp_board.pieces = new ArrayList<Piece>();
            for (int i = 0; i < b.pieces.size(); i++) {
                temp_board.pieces.add(new Piece("", false, false, 0, 0));
                temp_board.pieces.get(i).set_color(b.pieces.get(i).get_color());
                temp_board.pieces.get(i).set_firstmove(b.pieces.get(i).get_firstmove());
                temp_board.pieces.get(i).set_location(b.pieces.get(i).get_location());
                temp_board.pieces.get(i).set_x(b.pieces.get(i).get_x());
                temp_board.pieces.get(i).set_y(b.pieces.get(i).get_y());
                temp_board.pieces.get(i).set_type(b.pieces.get(i).get_type());
            }
        }
        if (!b.dead.isEmpty()) {
            temp_board.dead = new ArrayList<Piece>();
            for (int i = 0; i < b.dead.size(); i++) {
                temp_board.dead.add(new Piece("", false, false, 0, 0));
                temp_board.dead.get(i).set_color(b.dead.get(i).get_color());
                temp_board.dead.get(i).set_firstmove(b.dead.get(i).get_firstmove());
                temp_board.dead.get(i).set_location(b.dead.get(i).get_location());
                temp_board.dead.get(i).set_x(b.dead.get(i).get_x());
                temp_board.dead.get(i).set_y(b.dead.get(i).get_y());
                temp_board.dead.get(i).set_type(b.dead.get(i).get_type());
            }
        }
        temp_board.update_board();
        return temp_board;
    }

    private void board_init() {
        for (int i = 0; i < 8; i++) {
            Piece pawn = new Piece("Pawn", false, false, i, 6);//down
            pieces.add(pawn);
            pawn = new Piece("Pawn", true, true, i, 1);//up
            pieces.add(pawn);
        }
        for (int i = 0; i < 2; i++) {
            Piece piece;
            if (i == 0) {
                piece = new Piece("Bishop", true, true, 2, 0);//up
                pieces.add(piece);
                piece = new Piece("Bishop", true, true, 5, 0);
                pieces.add(piece);
                piece = new Piece("Knight", true, true, 1, 0);
                pieces.add(piece);
                piece = new Piece("Knight", true, true, 6, 0);
                pieces.add(piece);
                piece = new Piece("Rook", true, true, 0, 0);
                pieces.add(piece);
                piece = new Piece("Rook", true, true, 7, 0);
                pieces.add(piece);
                piece = new Piece("Queen", true, true, 3, 0);
                pieces.add(piece);
                piece = new Piece("King", true, true, 4, 0);
                pieces.add(piece);
            }
            if (i == 1) {
                piece = new Piece("Bishop", false, false, 2, 7);//down
                pieces.add(piece);
                piece = new Piece("Bishop", false, false, 5, 7);
                pieces.add(piece);
                piece = new Piece("Knight", false, false, 1, 7);
                pieces.add(piece);
                piece = new Piece("Knight", false, false, 6, 7);
                pieces.add(piece);
                piece = new Piece("Rook", false, false, 0, 7);
                pieces.add(piece);
                piece = new Piece("Rook", false, false, 7, 7);
                pieces.add(piece);
                piece = new Piece("Queen", false, false, 3, 7);
                pieces.add(piece);
                piece = new Piece("King", false, false, 4, 7);
                pieces.add(piece);
            }
        }
        update_board();
    }

    void print_board() throws UnsupportedEncodingException {
        PrintStream oo = new PrintStream(System.out, true, "UTF-8");
        System.out.println("===================================");

        for (int i = 0; i < 8; i++) {
            System.out.print("| ");
            for (int j = 0; j < 8; j++) {
                String s = board[j][i];
                oo.print(s);
                if (j < 7) {
                    System.out.print(" : ");
                }
            }
            System.out.println(" | " + (8 - i));
        }
        System.out.println("  a   b   c   d   e   f   g   h");
    }

    public boolean apply_move(String move) {
        if (move == null || move.length() < 4) {
            return false;
        }
        char x1 = move.charAt(0);
        char y1 = move.charAt(1);
        char x2 = move.charAt(2);
        char y2 = move.charAt(3);
        int X1, X2, Y1, Y2;
        X1 = CommonMethods.convert(x1);
        Y1 = CommonMethods.convert(y1);
        X2 = CommonMethods.convert(x2);
        Y2 = CommonMethods.convert(y2);
        @SuppressWarnings("unchecked")
        ArrayList<Piece> temp_pieces = (ArrayList<Piece>) pieces.clone(); // temporary changes array list
        @SuppressWarnings("unchecked")
        ArrayList<Piece> temp_dead = (ArrayList<Piece>) dead.clone();// temporary changes array list
        Piece piece = CommonMethods.get_piece_on_square(X1, Y1, temp_pieces);
        if (piece == null) {
            return false;
        }
        if (move.length() == 4) {
            if (piece.get_type() == "Pawn" && (((piece.get_location() == false) && Y2 == 0) || ((piece.get_location() == true) && Y2 == 7))) {//reach end of board without promotion
                return false;
            }
            if ((piece.get_color() == false && CommonMethods.player == 1) || (piece.get_color() == true && CommonMethods.player == 2)) {
                if (piece.check_move(X2, Y2, temp_pieces)) {
                    if (piece.get_firstmove()) {
                        if (piece.get_type() == "King") {
                            if (CommonMethods.castling) {//check castling variable
                                int rookX = X2 > piece.get_x() ? 7 : 0;//get rook's location
                                Piece rook = CommonMethods.get_piece_on_square(rookX, Y2, temp_pieces);
                                Board temp_board = new Board();
                                temp_board = boardcopy(this, temp_board);
                                Piece temp_king = CommonMethods.get_piece_on_square(X1, Y1, temp_board.pieces);
                                boolean temp_castl = CommonMethods.castling;
                                boolean temp_enpassant = CommonMethods.en_passant;
                                if (rookX == 7) {
                                    for (int i = X1; i < rookX; i++) {
                                        CommonMethods.castling = temp_castl;
                                        CommonMethods.en_passant = temp_enpassant;
                                        temp_king.set_x(i);
                                        if (king_risked(temp_king, temp_pieces)) {
                                            return false;
                                        }
                                    }
                                } else if (rookX == 0) {
                                    for (int i = X1; i > rookX; i--) {
                                        CommonMethods.castling = temp_castl;
                                        CommonMethods.en_passant = temp_enpassant;
                                        temp_king.set_x(i);
                                        if (king_risked(temp_king, temp_pieces)) {
                                            return false;
                                        }
                                    }
                                    CommonMethods.castling = temp_castl;
                                    CommonMethods.en_passant = temp_enpassant;
                                }
                                if (rook != null) {
                                    int rooknewX = rookX > 0 ? 5 : 3;
                                    rook.set_x(rooknewX);
                                    rook.set_y(Y2);
                                    CommonMethods.castling = false;
                                }
                            }
                        }
                        piece.set_firstmove(false);
                    }
                    Piece deadpiece;
                    if (Math.abs(X1 - X2) == 1 && CommonMethods.en_passant == true) {
                        piece.set_x(X2);
                        piece.set_y(Y2);
                        deadpiece = CommonMethods.get_piece_on_square(X2, Y1, temp_pieces);
                        CommonMethods.en_passant = false;
                        if (deadpiece != null) {
                            apply_eat(deadpiece, temp_pieces, temp_dead);
                        }
                        if (CommonMethods.player == 1) {
                            CommonMethods.player = 2;
                        } else {
                            CommonMethods.player = 1;
                        }
                        if (king_risked(CommonMethods.get_piece_on_square("King", piece.get_color(), temp_pieces), temp_pieces)) {
                            return false;
                        } else if (king_risked(CommonMethods.get_piece_on_square("King", !(piece.get_color()), temp_pieces), temp_pieces)) {
                            if (piece.get_color() == true) {
                                CommonMethods.white_king_checked = true;
                            } else {
                                CommonMethods.black_king_checked = true;
                            }
                        }
                        if (piece.get_color() == false) {
                            CommonMethods.white_king_checked = false;
                        } else {
                            CommonMethods.black_king_checked = false;
                        }
                        pieces = temp_pieces;
                        dead = temp_dead;
                        update_board();

                        return true;

                    } else {
                        deadpiece = piece.can_eat(X2, Y2, temp_pieces);
                        piece.set_x(X2);
                        piece.set_y(Y2);
                        if (deadpiece != null) {
                            apply_eat(deadpiece, temp_pieces, temp_dead);
                        }
                        if (king_risked(CommonMethods.get_piece_on_square("King", piece.get_color(), temp_pieces), temp_pieces)) {
                            return false;
                        } else if (king_risked(CommonMethods.get_piece_on_square("King", !(piece.get_color()), temp_pieces), temp_pieces)) {
                            if (piece.get_color() == true) {
                                CommonMethods.white_king_checked = true;
                            } else {
                                CommonMethods.black_king_checked = true;
                            }
                        }
                        if (piece.get_color() == false) {
                            CommonMethods.white_king_checked = false;
                        } else {
                            CommonMethods.black_king_checked = false;
                        }
                        if (CommonMethods.player == 1) {
                            CommonMethods.player = 2;
                        } else {
                            CommonMethods.player = 1;
                        }
                        pieces = temp_pieces;
                        dead = temp_dead;
                        update_board();
                        return true;
                    }
                }
            }
        } else if (move.length() == 5) {
            char p = move.charAt(4);
            if ((piece.get_color() == false && CommonMethods.player == 1) || (piece.get_color() == true && CommonMethods.player == 2)) {
                if (piece.get_type() == "Pawn") {
                    if (piece.check_move(X2, Y2, temp_pieces)) {
                        if (Y2 == 0 || Y2 == 7) {
                            switch (p) {
                                case 'p':
                                case 'P':
                                case 'k':
                                case 'K':
                                    return false;
                                case 'q':
                                case 'Q':
                                    piece.set_type("Queen");
                                    break;
                                case 'r':
                                case 'R':
                                    piece.set_type("Rook");
                                    break;
                                case 'n':
                                case 'N':
                                    piece.set_type("Knight");
                                    break;
                                case 'b':
                                case 'B':
                                    piece.set_type("Bishop");
                                    break;
                            }
                            Piece deadpiece = piece.can_eat(X2, Y2, temp_pieces);
                            if (deadpiece != null) {
                                apply_eat(deadpiece, temp_pieces, temp_dead);
                            }
                            piece.set_x(X2);
                            piece.set_y(Y2);
                            if (king_risked(CommonMethods.get_piece_on_square("King", piece.get_color(), temp_pieces), temp_pieces)) {
                                return false;
                            } else if (king_risked(CommonMethods.get_piece_on_square("King", !(piece.get_color()), temp_pieces), temp_pieces)) {
                                if (piece.get_color() == true) {
                                    CommonMethods.white_king_checked = true;
                                } else {
                                    CommonMethods.black_king_checked = true;
                                }
                            }
                            if (piece.get_color() == false) {
                                CommonMethods.white_king_checked = false;
                            } else {
                                CommonMethods.black_king_checked = false;
                            }
                            if (CommonMethods.player == 1) {
                                CommonMethods.player = 2;
                            } else {
                                CommonMethods.player = 1;
                            }
                            pieces = temp_pieces;
                            dead = temp_dead;
                            update_board();
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }

    public void apply_eat(Piece deadpiece, ArrayList<Piece> temp_pieces, ArrayList<Piece> temp_dead) {
        temp_pieces.remove(deadpiece);
        deadpiece.set_y(-1);
        deadpiece.set_x(-1);
        temp_dead.add(deadpiece);
    }

    private void update_board() {
        board = new String[][]{
            {" ", " ", " ", " ", " ", " ", " ", " "},
            {" ", " ", " ", " ", " ", " ", " ", " "},
            {" ", " ", " ", " ", " ", " ", " ", " "},
            {" ", " ", " ", " ", " ", " ", " ", " "},
            {" ", " ", " ", " ", " ", " ", " ", " "},
            {" ", " ", " ", " ", " ", " ", " ", " "},
            {" ", " ", " ", " ", " ", " ", " ", " "},
            {" ", " ", " ", " ", " ", " ", " ", " "}};
        for (int i = 0; i < pieces.size(); i++) {
            String name = pieces.get(i).get_type();
            int X = pieces.get(i).get_x();
            int Y = pieces.get(i).get_y();
            if (pieces.get(i).get_color() == false) {
                switch (name) {
                    case "Pawn":
                        board[X][Y] = "\u265F";
                        break;
                    case "Bishop":
                        board[X][Y] = "\u265D";
                        break;
                    case "Rook":
                        board[X][Y] = "\u265C";
                        break;
                    case "Knight":
                        board[X][Y] = "\u265E";
                        break;
                    case "Queen":
                        board[X][Y] = "\u265B";
                        break;
                    case "King":
                        board[X][Y] = "\u265A";
                        break;
                }
            } else {
                switch (name) {
                    case "Pawn":
                        board[X][Y] = "\u2659";
                        break;
                    case "Bishop":
                        board[X][Y] = "\u2657";
                        break;
                    case "Rook":
                        board[X][Y] = "\u2656";
                        break;
                    case "Knight":
                        board[X][Y] = "\u2658";
                        break;
                    case "Queen":
                        board[X][Y] = "\u2655";
                        break;
                    case "King":
                        board[X][Y] = "\u2654";
                        break;
                }
            }

        }
    }

    private boolean king_risked(Piece king, ArrayList<Piece> temp_pieces) {

        for (int i = 0; i < temp_pieces.size(); i++) {
            if (temp_pieces.get(i).get_color() == king.get_color()) {
                continue;
            } else if (temp_pieces.get(i).check_move(king.get_x(), king.get_y(), temp_pieces)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> next_moves(int player) {
        ArrayList<String> moves = new ArrayList<String>();
        @SuppressWarnings("unchecked")
        Board temp_board = new Board();
        temp_board = boardcopy(this, temp_board);
        int temp_player = CommonMethods.player;
        boolean temp_castl = CommonMethods.castling;
        boolean temp_en_pt = CommonMethods.en_passant;
        boolean temp_w_k_c = CommonMethods.white_king_checked;
        boolean temp_w_b_c = CommonMethods.black_king_checked;
        for (int i = 0; i < temp_board.pieces.size(); i++) {
            Piece p = temp_board.pieces.get(i);
            int x = p.get_x();
            int y = p.get_y();
            int temp_x;
            int temp_y;
            if ((player == 1 && p.get_color() == false) || (player == 2 && p.get_color() == true)) {
                switch (p.get_type()) {
                    case "Pawn":
                        if (p.get_color() == false) {
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = boardcopy(this, temp_board);
                            temp_y = y - 1;
                            if (temp_y == 0) {
                                String s = CommonMethods.rev_convert(x, y, x, temp_y) + 'q';
                                if (temp_board.apply_move(s)) {
                                    moves.add(s);
                                    s = CommonMethods.rev_convert(x, y, x, temp_y) + 'n';
                                    moves.add(s);
                                    s = CommonMethods.rev_convert(x, y, x, temp_y) + 'b';
                                    moves.add(s);
                                    s = CommonMethods.rev_convert(x, y, x, temp_y) + 'r';
                                    moves.add(s);
                                }
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = boardcopy(this, temp_board);
                            temp_y = y - 1;
                            temp_x = x - 1;
                            if (temp_y == 0) {
                                String s = CommonMethods.rev_convert(x, y, temp_x, temp_y) + 'q';
                                if (temp_board.apply_move(s)) {
                                    moves.add(s);
                                    s = CommonMethods.rev_convert(x, y, temp_x, temp_y) + 'n';
                                    moves.add(s);
                                    s = CommonMethods.rev_convert(x, y, temp_x, temp_y) + 'b';
                                    moves.add(s);
                                    s = CommonMethods.rev_convert(x, y, temp_x, temp_y) + 'r';
                                    moves.add(s);
                                }
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = boardcopy(this, temp_board);
                            temp_y = y - 1;
                            temp_x = x + 1;
                            if (temp_y == 0) {
                                String s = CommonMethods.rev_convert(x, y, temp_x, temp_y) + 'q';
                                if (temp_board.apply_move(s)) {
                                    moves.add(s);
                                    s = CommonMethods.rev_convert(x, y, temp_x, temp_y) + 'n';
                                    moves.add(s);
                                    s = CommonMethods.rev_convert(x, y, temp_x, temp_y) + 'b';
                                    moves.add(s);
                                    s = CommonMethods.rev_convert(x, y, temp_x, temp_y) + 'r';
                                    moves.add(s);
                                }
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = boardcopy(this, temp_board);
                            if (temp_y != 0 && temp_board.apply_move(CommonMethods.rev_convert(x, y, x, temp_y))) {
                                moves.add(CommonMethods.rev_convert(x, y, x, temp_y));
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = boardcopy(this, temp_board);
                            temp_y = y - 2;
                            if (temp_board.apply_move(CommonMethods.rev_convert(x, y, x, temp_y))) {
                                moves.add(CommonMethods.rev_convert(x, y, x, temp_y));
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = boardcopy(this, temp_board);
                            temp_y = y - 1;
                            temp_x = x - 1;
                            if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = boardcopy(this, temp_board);
                            temp_y = y - 1;
                            temp_x = x + 1;
                            if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                            }
                        } else {
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = boardcopy(this, temp_board);
                            temp_y = y + 1;
                            if (temp_y == 7) {
                                String s = CommonMethods.rev_convert(x, y, x, temp_y) + 'q';
                                if (temp_board.apply_move(s)) {
                                    moves.add(s);
                                    s = CommonMethods.rev_convert(x, y, x, temp_y) + 'n';
                                    moves.add(s);
                                    s = CommonMethods.rev_convert(x, y, x, temp_y) + 'b';
                                    moves.add(s);
                                    s = CommonMethods.rev_convert(x, y, x, temp_y) + 'r';
                                    moves.add(s);
                                }
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = boardcopy(this, temp_board);
                            if (temp_y != 7 && temp_board.apply_move(CommonMethods.rev_convert(x, y, x, temp_y))) {
                                moves.add(CommonMethods.rev_convert(x, y, x, temp_y));
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = boardcopy(this, temp_board);
                            temp_y = y + 2;
                            if (temp_board.apply_move(CommonMethods.rev_convert(x, y, x, temp_y))) {
                                moves.add(CommonMethods.rev_convert(x, y, x, temp_y));
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = boardcopy(this, temp_board);
                            temp_y = y + 1;
                            temp_x = x - 1;
                            if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = boardcopy(this, temp_board);
                            temp_y = y + 1;
                            temp_x = x + 1;
                            if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                            }
                        }
                        break;
                    case "Rook":
                        temp_x = x - 1;
                        while (true) {
                            if (temp_x <= -1) {
                                temp_x = x + 1;
                            }
                            if (temp_x >= 8) {
                                break;
                            } else if (temp_x < x) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = boardcopy(this, temp_board);
                                if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, y))) {
                                    moves.add(CommonMethods.rev_convert(x, y, temp_x, y));
                                }
                                temp_x--;
                            } else if (temp_x > x) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = boardcopy(this, temp_board);
                                if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, y))) {
                                    moves.add(CommonMethods.rev_convert(x, y, temp_x, y));
                                }
                                temp_x++;
                            }
                        }
                        temp_y = y - 1;
                        while (true) {
                            if (temp_y <= -1) {
                                temp_y = y + 1;
                            }
                            if (temp_y >= 8) {
                                break;
                            } else if (temp_y < y) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = boardcopy(this, temp_board);
                                if (temp_board.apply_move(CommonMethods.rev_convert(x, y, x, temp_y))) {
                                    moves.add(CommonMethods.rev_convert(x, y, x, temp_y));
                                }
                                temp_y--;
                            } else if (temp_y > y) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = boardcopy(this, temp_board);
                                if (temp_board.apply_move(CommonMethods.rev_convert(x, y, x, temp_y))) {
                                    moves.add(CommonMethods.rev_convert(x, y, x, temp_y));
                                }
                                temp_y++;
                            }
                        }
                        break;
                    case "Bishop":
                        temp_x = x - 1;
                        temp_y = y - 1;
                        while (true) {
                            if (temp_x <= -1) {
                                temp_x = x + 1;
                                temp_y = y + 1;
                            }
                            if (temp_x >= 8) {
                                break;
                            } else if (temp_x < x) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = boardcopy(this, temp_board);
                                if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                                    
                                }
                                temp_x--;
                                temp_y--;
                            } else if (temp_x > x) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = boardcopy(this, temp_board);
                                if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                                }
                                temp_x++;
                                temp_y++;
                            }
                        }
                        temp_x = x + 1;
                        temp_y = y - 1;
                        while (true) {
                            if (temp_y <= -1) {
                                temp_y = y + 1;
                                temp_x = x - 1;
                            }
                            if (temp_y >= 8) {
                                break;
                            } else if (temp_y < y) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = boardcopy(this, temp_board);
                                if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                                }
                                temp_y--;
                                temp_x++;
                            } else if (temp_y > y) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = boardcopy(this, temp_board);
                                if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                                }
                                temp_y++;
                                temp_x--;
                            }
                        }
                        break;
                    case "Knight":
                        temp_x = x - 1;
                        temp_y = y - 2;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = boardcopy(this, temp_board);
                        if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                        }
                        temp_x = x - 1;
                        temp_y = y + 2;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = boardcopy(this, temp_board);
                        if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                        }
                        temp_x = x - 2;
                        temp_y = y - 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = boardcopy(this, temp_board);
                        if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                        }
                        temp_x = x - 2;
                        temp_y = y + 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = boardcopy(this, temp_board);
                        if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                        }
                        temp_x = x + 1;
                        temp_y = y - 2;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = boardcopy(this, temp_board);
                        if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                        }
                        temp_x = x + 1;
                        temp_y = y + 2;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = boardcopy(this, temp_board);
                        if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                        }
                        temp_x = x + 2;
                        temp_y = y - 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = boardcopy(this, temp_board);
                        if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                        }
                        temp_x = x + 2;
                        temp_y = y + 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = boardcopy(this, temp_board);
                        if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                        }
                        break;
                    case "Queen":
                        temp_x = x - 1;
                        while (true) {
                            if (temp_x <= -1) {
                                temp_x = x + 1;
                            }
                            if (temp_x >= 8) {
                                break;
                            } else if (temp_x < x) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = boardcopy(this, temp_board);
                                if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, y))) {
                                    moves.add(CommonMethods.rev_convert(x, y, temp_x, y));
                                }
                                temp_x--;
                            } else if (temp_x > x) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = boardcopy(this, temp_board);
                                if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, y))) {
                                    moves.add(CommonMethods.rev_convert(x, y, temp_x, y));
                                }
                                temp_x++;
                            }
                        }
                        temp_y = y - 1;
                        while (true) {
                            if (temp_y <= -1) {
                                temp_y = y + 1;
                            }
                            if (temp_y >= 8) {
                                break;
                            } else if (temp_y < y) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = boardcopy(this, temp_board);
                                if (temp_board.apply_move(CommonMethods.rev_convert(x, y, x, temp_y))) {
                                    moves.add(CommonMethods.rev_convert(x, y, x, temp_y));
                                }
                                temp_y--;
                            } else if (temp_y > y) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = boardcopy(this, temp_board);
                                if (temp_board.apply_move(CommonMethods.rev_convert(x, y, x, temp_y))) {
                                    moves.add(CommonMethods.rev_convert(x, y, x, temp_y));
                                }
                                temp_y++;
                            }
                        }
                        temp_x = x - 1;
                        temp_y = y - 1;
                        while (true) {
                            if (temp_x <= -1) {
                                temp_x = x + 1;
                                temp_y = y + 1;
                            }
                            if (temp_x >= 8) {
                                break;
                            } else if (temp_x < x) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = boardcopy(this, temp_board);
                                if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                                }
                                temp_x--;
                                temp_y--;
                            } else if (temp_x > x) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = boardcopy(this, temp_board);
                                if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                                }
                                temp_x++;
                                temp_y++;
                            }
                        }
                        temp_x = x + 1;
                        temp_y = y - 1;
                        while (true) {
                            if (temp_y <= -1) {
                                temp_y = y + 1;
                                temp_x = x - 1;
                            }
                            if (temp_y >= 8) {
                                break;
                            } else if (temp_y < y) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = boardcopy(this, temp_board);
                                if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                                }
                                temp_y--;
                                temp_x++;
                            } else if (temp_y > y) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = boardcopy(this, temp_board);
                                if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                                }
                                temp_y++;
                                temp_x--;
                            }
                        }
                        break;
                    case "King":
                        temp_x = x - 1;
                        temp_y = y - 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = boardcopy(this, temp_board);
                        if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                        }
                        temp_x = x - 1;
                        temp_y = y + 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = boardcopy(this, temp_board);
                        if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                        }
                        temp_x = x - 1;
                        temp_y = y;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = boardcopy(this, temp_board);
                        if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                        }
                        temp_x = x;
                        temp_y = y - 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = boardcopy(this, temp_board);
                        if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                        }
                        temp_x = x;
                        temp_y = y + 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = boardcopy(this, temp_board);
                        if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                        }
                        temp_x = x + 1;
                        temp_y = y - 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = boardcopy(this, temp_board);
                        if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                        }
                        temp_x = x + 1;
                        temp_y = y + 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = boardcopy(this, temp_board);
                        if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                        }
                        temp_x = x + 1;
                        temp_y = y;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = boardcopy(this, temp_board);
                        if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.rev_convert(x, y, temp_x, temp_y));
                        }
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = boardcopy(this, temp_board);
                        if ((p.get_color() == false && y == 7) || (p.get_color() == true && y == 0)) {
                            temp_x = 2;
                            CommonMethods.player = temp_player;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = boardcopy(this, temp_board);
                            if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, y))) {
                                moves.add(CommonMethods.rev_convert(x, y, temp_x, y));
                            }
                            temp_x = 6;
                            CommonMethods.player = temp_player;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = boardcopy(this, temp_board);
                            if (temp_board.apply_move(CommonMethods.rev_convert(x, y, temp_x, y))) {
                                moves.add(CommonMethods.rev_convert(x, y, temp_x, y));
                            }
                        }
                        break;
                }
            }
        }
        CommonMethods.player = temp_player;
        CommonMethods.castling = temp_castl;
        CommonMethods.white_king_checked = temp_w_k_c;
        CommonMethods.black_king_checked = temp_w_b_c;
        return moves;
    }

}
