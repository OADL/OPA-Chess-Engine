package opa.chess.Models;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.Location;
import opa.chess.Enums.PieceType;
import opa.chess.Models.Pieces.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    public ArrayList<Piece> pieces = new ArrayList<>();
    public ArrayList<Piece> dead = new ArrayList<>();

    public Board() {
        init();
    }

    public Board copy(Board b, Board temp_board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                temp_board.board[i][j] = b.board[i][j];
            }
        }
        if (!b.pieces.isEmpty()) {
            temp_board.pieces = new ArrayList<>();
            for (int i = 0; i < b.pieces.size(); i++) {
                temp_board.pieces.add(b.pieces.get(i).clone());
            }
        }
        if (!b.dead.isEmpty()) {
            temp_board.dead = new ArrayList<>();
            for (int i = 0; i < b.dead.size(); i++) {
                temp_board.dead.add(b.dead.get(i).clone());
            }
        }
        temp_board.update();
        return temp_board;
    }

    private void init() {
        for (int i = 0; i < 8; i++) {
            Piece pawn = new Pawn(Location.DOWN, false, i, 6);//down
            pieces.add(pawn);
            pawn = new Pawn(Location.UP, true, i, 1);//up
            pieces.add(pawn);
        }
        Piece piece;
        //up
        piece = new Bishop(Location.UP, true, 2, 0);
        pieces.add(piece);
        piece = new Bishop(Location.UP, true, 5, 0);
        pieces.add(piece);
        piece = new Knight(Location.UP, true, 1, 0);
        pieces.add(piece);
        piece = new Knight(Location.UP, true, 6, 0);
        pieces.add(piece);
        piece = new Rook(Location.UP, true, 0, 0);
        pieces.add(piece);
        piece = new Rook(Location.UP, true, 7, 0);
        pieces.add(piece);
        piece = new Queen(Location.UP, true, 3, 0);
        pieces.add(piece);
        piece = new King(Location.UP, true, 4, 0);
        pieces.add(piece);

        //down
        piece = new Bishop(Location.DOWN, false, 2, 7);
        pieces.add(piece);
        piece = new Bishop(Location.DOWN, false, 5, 7);
        pieces.add(piece);
        piece = new Knight(Location.DOWN, false, 1, 7);
        pieces.add(piece);
        piece = new Knight(Location.DOWN, false, 6, 7);
        pieces.add(piece);
        piece =new Rook(Location.DOWN, false, 0, 7);
        pieces.add(piece);
        piece =new Rook(Location.DOWN, false, 7, 7);
        pieces.add(piece);
        piece = new Queen(Location.DOWN, false, 3, 7);
        pieces.add(piece);
        piece = new King(Location.DOWN, false, 4, 7);
        pieces.add(piece);
        update();
    }

    public void print() {
        PrintStream printStream = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        System.out.println("===================================");

        for (int i = 0; i < 8; i++) {
            System.out.print("| ");
            for (int j = 0; j < 8; j++) {
                String s = board[j][i];
                printStream.print(s);
                if (j < 7) {
                    System.out.print(" : ");
                }
            }
            System.out.println(" | " + (8 - i));
        }
        System.out.println("  a   b   c   d   e   f   g   h");
    }

    public boolean applyMove(String move) {
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
        Piece piece = CommonMethods.getPieceOnSquare(X1, Y1, temp_pieces);
        if (piece == null) {
            return false;
        }
        if (move.length() == 4) {
            if (piece.getType() == PieceType.PAWN && (((piece.getLocation()==Location.DOWN) && Y2 == 0) || ((piece.getLocation()==Location.UP) && Y2 == 7))) {//reach end of board without promotion
                return false;
            }
            if ((!piece.getColor() && CommonMethods.player == 1) || (piece.getColor() && CommonMethods.player == 2)) {
                if (piece.checkMove(X2, Y2, temp_pieces)) {
                    if (piece.isFirstMove()) {
                        if (piece.getType() == PieceType.KING) {
                            if (CommonMethods.castling) {//check castling variable
                                int rookX = X2 > piece.getX() ? 7 : 0;//get rook's location
                                Piece rook = CommonMethods.getPieceOnSquare(rookX, Y2, temp_pieces);
                                Board temp_board = new Board();
                                temp_board = copy(this,temp_board);
                                Piece temp_king = CommonMethods.getPieceOnSquare(X1, Y1, temp_board.pieces);
                                boolean temp_castl = CommonMethods.castling;
                                boolean temp_enpassant = CommonMethods.en_passant;
                                if (rookX == 7) {
                                    for (int i = X1; i < rookX; i++) {
                                        CommonMethods.castling = temp_castl;
                                        CommonMethods.en_passant = temp_enpassant;
                                        temp_king.setX(i);
                                        if (kingRisked(temp_king, temp_pieces)) {
                                            return false;
                                        }
                                    }
                                } else {
                                    for (int i = X1; i > rookX; i--) {
                                        CommonMethods.castling = temp_castl;
                                        CommonMethods.en_passant = temp_enpassant;
                                        temp_king.setX(i);
                                        if (kingRisked(temp_king, temp_pieces)) {
                                            return false;
                                        }
                                    }
                                    CommonMethods.castling = temp_castl;
                                    CommonMethods.en_passant = temp_enpassant;
                                }
                                if (rook != null) {
                                    int rooknewX = rookX > 0 ? 5 : 3;
                                    rook.setX(rooknewX);
                                    rook.setY(Y2);
                                    CommonMethods.castling = false;
                                }
                            }
                        }
                        piece.setFirstMove(false);
                    }
                    Piece deadpiece;
                    if (Math.abs(X1 - X2) == 1 && CommonMethods.en_passant) {
                        piece.setX(X2);
                        piece.setY(Y2);
                        deadpiece = CommonMethods.getPieceOnSquare(X2, Y1, temp_pieces);
                        CommonMethods.en_passant = false;
                        if (deadpiece != null) {
                            applyEat(deadpiece, temp_pieces, temp_dead);
                        }
                        if (CommonMethods.player == 1) {
                            CommonMethods.player = 2;
                        } else {
                            CommonMethods.player = 1;
                        }
                        if (kingRisked(CommonMethods.getPieceOnSquare(PieceType.KING, piece.getColor(), temp_pieces), temp_pieces)) {
                            return false;
                        } else if (kingRisked(CommonMethods.getPieceOnSquare(PieceType.KING, !(piece.getColor()), temp_pieces), temp_pieces)) {
                            if (piece.getColor()) {
                                CommonMethods.white_king_checked = true;
                            } else {
                                CommonMethods.black_king_checked = true;
                            }
                        }
                        if (!piece.getColor()) {
                            CommonMethods.white_king_checked = false;
                        } else {
                            CommonMethods.black_king_checked = false;
                        }
                        pieces = temp_pieces;
                        dead = temp_dead;
                        update();

                        return true;

                    } else {
                        deadpiece = piece.canEat(X2, Y2, temp_pieces);
                        piece.setX(X2);
                        piece.setY(Y2);
                        if (deadpiece != null) {
                            applyEat(deadpiece, temp_pieces, temp_dead);
                        }
                        if (kingRisked(CommonMethods.getPieceOnSquare(PieceType.KING, piece.getColor(), temp_pieces), temp_pieces)) {
                            return false;
                        } else if (kingRisked(CommonMethods.getPieceOnSquare(PieceType.KING, !(piece.getColor()), temp_pieces), temp_pieces)) {
                            if (piece.getColor()) {
                                CommonMethods.white_king_checked = true;
                            } else {
                                CommonMethods.black_king_checked = true;
                            }
                        }
                        if (!piece.getColor()) {
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
                        update();
                        return true;
                    }
                }
            }
        } else if (move.length() == 5) {
            char p = move.charAt(4);
            if ((!piece.getColor() && CommonMethods.player == 1) || (piece.getColor() && CommonMethods.player == 2)) {
                if (piece.getType() == PieceType.PAWN) {
                    if (piece.checkMove(X2, Y2, temp_pieces)) {
                        if (Y2 == 0 || Y2 == 7) {
                            switch (p) {
                                case 'p':
                                case 'P':
                                case 'k':
                                case 'K':
                                    return false;
                                case 'q':
                                case 'Q':
                                    piece.setType(PieceType.QUEEN);
                                    break;
                                case 'r':
                                case 'R':
                                    piece.setType(PieceType.ROOK);
                                    break;
                                case 'n':
                                case 'N':
                                    piece.setType(PieceType.KNIGHT);
                                    break;
                                case 'b':
                                case 'B':
                                    piece.setType(PieceType.BISHOP);
                                    break;
                            }
                            Piece deadpiece = piece.canEat(X2, Y2, temp_pieces);
                            if (deadpiece != null) {
                                applyEat(deadpiece, temp_pieces, temp_dead);
                            }
                            piece.setX(X2);
                            piece.setY(Y2);
                            if (kingRisked(CommonMethods.getPieceOnSquare(PieceType.KING, piece.getColor(), temp_pieces), temp_pieces)) {
                                return false;
                            } else if (kingRisked(CommonMethods.getPieceOnSquare(PieceType.KING, !(piece.getColor()), temp_pieces), temp_pieces)) {
                                if (piece.getColor()) {
                                    CommonMethods.white_king_checked = true;
                                } else {
                                    CommonMethods.black_king_checked = true;
                                }
                            }
                            if (!piece.getColor()) {
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
                            update();
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }

    public void applyEat(Piece deadpiece, ArrayList<Piece> temp_pieces, ArrayList<Piece> temp_dead) {
        temp_pieces.remove(deadpiece);
        deadpiece.setY(-1);
        deadpiece.setX(-1);
        temp_dead.add(deadpiece);
    }

    private void update() {
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
            PieceType type = pieces.get(i).getType();
            int X = pieces.get(i).getX();
            int Y = pieces.get(i).getY();
            if (!pieces.get(i).getColor()) {
                switch (type) {
                    case PAWN:
                        board[X][Y] = "\u265F";
                        break;
                    case BISHOP:
                        board[X][Y] = "\u265D";
                        break;
                    case ROOK:
                        board[X][Y] = "\u265C";
                        break;
                    case KNIGHT:
                        board[X][Y] = "\u265E";
                        break;
                    case QUEEN:
                        board[X][Y] = "\u265B";
                        break;
                    case KING:
                        board[X][Y] = "\u265A";
                        break;
                }
            } else {
                switch (type) {
                    case PAWN:
                        board[X][Y] = "\u2659";
                        break;
                    case BISHOP:
                        board[X][Y] = "\u2657";
                        break;
                    case ROOK:
                        board[X][Y] = "\u2656";
                        break;
                    case KNIGHT:
                        board[X][Y] = "\u2658";
                        break;
                    case QUEEN:
                        board[X][Y] = "\u2655";
                        break;
                    case KING:
                        board[X][Y] = "\u2654";
                        break;
                }
            }

        }
    }

    private boolean kingRisked(Piece king, ArrayList<Piece> temp_pieces) {
        for (int i = 0; i < temp_pieces.size(); i++) {
            if (temp_pieces.get(i).getColor() != king.getColor() && temp_pieces.get(i).checkMove(king.getX(), king.getY(), temp_pieces)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> nextMoves(int player) {
        ArrayList<String> moves = new ArrayList<>();
        @SuppressWarnings("unchecked")
        Board temp_board = new Board();
        temp_board = copy(this,temp_board);
        int temp_player = CommonMethods.player;
        boolean temp_castl = CommonMethods.castling;
        boolean temp_en_pt = CommonMethods.en_passant;
        boolean temp_w_k_c = CommonMethods.white_king_checked;
        boolean temp_w_b_c = CommonMethods.black_king_checked;
        for (int i = 0; i < temp_board.pieces.size(); i++) {
            Piece p = temp_board.pieces.get(i);
            int x = p.getX();
            int y = p.getY();
            int temp_x;
            int temp_y;
            if ((player == 1 && !p.getColor()) || (player == 2 && p.getColor())) {
                switch (p.getType()) {
                    case PAWN:
                        if (!p.getColor()) {
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = copy(this,temp_board);
                            temp_y = y - 1;
                            if (temp_y == 0) {
                                String s = CommonMethods.reverseConvert(x, y, x, temp_y) + 'q';
                                if (temp_board.applyMove(s)) {
                                    moves.add(s);
                                    s = CommonMethods.reverseConvert(x, y, x, temp_y) + 'n';
                                    moves.add(s);
                                    s = CommonMethods.reverseConvert(x, y, x, temp_y) + 'b';
                                    moves.add(s);
                                    s = CommonMethods.reverseConvert(x, y, x, temp_y) + 'r';
                                    moves.add(s);
                                }
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = copy(this,temp_board);
                            temp_y = y - 1;
                            temp_x = x - 1;
                            if (temp_y == 0) {
                                String s = CommonMethods.reverseConvert(x, y, temp_x, temp_y) + 'q';
                                if (temp_board.applyMove(s)) {
                                    moves.add(s);
                                    s = CommonMethods.reverseConvert(x, y, temp_x, temp_y) + 'n';
                                    moves.add(s);
                                    s = CommonMethods.reverseConvert(x, y, temp_x, temp_y) + 'b';
                                    moves.add(s);
                                    s = CommonMethods.reverseConvert(x, y, temp_x, temp_y) + 'r';
                                    moves.add(s);
                                }
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = copy(this,temp_board);
                            temp_y = y - 1;
                            temp_x = x + 1;
                            if (temp_y == 0) {
                                String s = CommonMethods.reverseConvert(x, y, temp_x, temp_y) + 'q';
                                if (temp_board.applyMove(s)) {
                                    moves.add(s);
                                    s = CommonMethods.reverseConvert(x, y, temp_x, temp_y) + 'n';
                                    moves.add(s);
                                    s = CommonMethods.reverseConvert(x, y, temp_x, temp_y) + 'b';
                                    moves.add(s);
                                    s = CommonMethods.reverseConvert(x, y, temp_x, temp_y) + 'r';
                                    moves.add(s);
                                }
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = copy(this,temp_board);
                            if (temp_y != 0 && temp_board.applyMove(CommonMethods.reverseConvert(x, y, x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, x, temp_y));
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = copy(this,temp_board);
                            temp_y = y - 2;
                            if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, x, temp_y));
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = copy(this,temp_board);
                            temp_y = y - 1;
                            temp_x = x - 1;
                            if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = copy(this,temp_board);
                            temp_y = y - 1;
                            temp_x = x + 1;
                            if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                        } else {
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = copy(this,temp_board);
                            temp_y = y + 1;
                            if (temp_y == 7) {
                                String s = CommonMethods.reverseConvert(x, y, x, temp_y) + 'q';
                                if (temp_board.applyMove(s)) {
                                    moves.add(s);
                                    s = CommonMethods.reverseConvert(x, y, x, temp_y) + 'n';
                                    moves.add(s);
                                    s = CommonMethods.reverseConvert(x, y, x, temp_y) + 'b';
                                    moves.add(s);
                                    s = CommonMethods.reverseConvert(x, y, x, temp_y) + 'r';
                                    moves.add(s);
                                }
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = copy(this,temp_board);
                            if (temp_y != 7 && temp_board.applyMove(CommonMethods.reverseConvert(x, y, x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, x, temp_y));
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = copy(this,temp_board);
                            temp_y = y + 2;
                            if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, x, temp_y));
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = copy(this,temp_board);
                            temp_y = y + 1;
                            temp_x = x - 1;
                            if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                            CommonMethods.player = temp_player;
                            CommonMethods.en_passant = temp_en_pt;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = copy(this,temp_board);
                            temp_y = y + 1;
                            temp_x = x + 1;
                            if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                        }
                        break;
                    case ROOK:
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
                                temp_board = copy(this,temp_board);
                                if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, temp_x, y));
                                }
                                temp_x--;
                            } else if (temp_x > x) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = copy(this,temp_board);
                                if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, temp_x, y));
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
                                temp_board = copy(this,temp_board);
                                if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, x, temp_y));
                                }
                                temp_y--;
                            } else if (temp_y > y) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = copy(this,temp_board);
                                if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, x, temp_y));
                                }
                                temp_y++;
                            }
                        }
                        break;
                    case BISHOP:
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
                                temp_board = copy(this,temp_board);
                                if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                                    
                                }
                                temp_x--;
                                temp_y--;
                            } else if (temp_x > x) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = copy(this,temp_board);
                                if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
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
                                temp_board = copy(this,temp_board);
                                if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                                }
                                temp_y--;
                                temp_x++;
                            } else if (temp_y > y) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = copy(this,temp_board);
                                if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                                }
                                temp_y++;
                                temp_x--;
                            }
                        }
                        break;
                    case KNIGHT:
                        temp_x = x - 1;
                        temp_y = y - 2;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = copy(this,temp_board);
                        if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                        }
                        temp_x = x - 1;
                        temp_y = y + 2;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = copy(this,temp_board);
                        if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                        }
                        temp_x = x - 2;
                        temp_y = y - 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = copy(this,temp_board);
                        if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                        }
                        temp_x = x - 2;
                        temp_y = y + 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = copy(this,temp_board);
                        if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                        }
                        temp_x = x + 1;
                        temp_y = y - 2;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = copy(this,temp_board);
                        if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                        }
                        temp_x = x + 1;
                        temp_y = y + 2;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = copy(this,temp_board);
                        if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                        }
                        temp_x = x + 2;
                        temp_y = y - 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = copy(this,temp_board);
                        if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                        }
                        temp_x = x + 2;
                        temp_y = y + 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = copy(this,temp_board);
                        if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                        }
                        break;
                    case QUEEN:
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
                                temp_board = copy(this,temp_board);
                                if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, temp_x, y));
                                }
                                temp_x--;
                            } else if (temp_x > x) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = copy(this,temp_board);
                                if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, temp_x, y));
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
                                temp_board = copy(this,temp_board);
                                if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, x, temp_y));
                                }
                                temp_y--;
                            } else if (temp_y > y) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = copy(this,temp_board);
                                if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, x, temp_y));
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
                                temp_board = copy(this,temp_board);
                                if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                                }
                                temp_x--;
                                temp_y--;
                            } else if (temp_x > x) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = copy(this,temp_board);
                                if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
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
                                temp_board = copy(this,temp_board);
                                if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                                }
                                temp_y--;
                                temp_x++;
                            } else if (temp_y > y) {
                                CommonMethods.player = temp_player;
                                CommonMethods.castling = temp_castl;
                                CommonMethods.white_king_checked = temp_w_k_c;
                                CommonMethods.black_king_checked = temp_w_b_c;
                                temp_board = copy(this,temp_board);
                                if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                                }
                                temp_y++;
                                temp_x--;
                            }
                        }
                        break;
                    case KING:
                        temp_x = x - 1;
                        temp_y = y - 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = copy(this,temp_board);
                        if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                        }
                        temp_x = x - 1;
                        temp_y = y + 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = copy(this,temp_board);
                        if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                        }
                        temp_x = x - 1;
                        temp_y = y;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = copy(this,temp_board);
                        if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                        }
                        temp_x = x;
                        temp_y = y - 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = copy(this,temp_board);
                        if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                        }
                        temp_x = x;
                        temp_y = y + 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = copy(this,temp_board);
                        if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                        }
                        temp_x = x + 1;
                        temp_y = y - 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = copy(this,temp_board);
                        if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                        }
                        temp_x = x + 1;
                        temp_y = y + 1;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = copy(this,temp_board);
                        if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                        }
                        temp_x = x + 1;
                        temp_y = y;
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = copy(this,temp_board);
                        if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                            moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                        }
                        CommonMethods.player = temp_player;
                        CommonMethods.castling = temp_castl;
                        CommonMethods.white_king_checked = temp_w_k_c;
                        CommonMethods.black_king_checked = temp_w_b_c;
                        temp_board = copy(this,temp_board);
                        if ((!p.getColor() && y == 7) || (p.getColor() && y == 0)) {
                            temp_x = 2;
                            CommonMethods.player = temp_player;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = copy(this,temp_board);
                            if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, y));
                            }
                            temp_x = 6;
                            CommonMethods.player = temp_player;
                            CommonMethods.castling = temp_castl;
                            CommonMethods.white_king_checked = temp_w_k_c;
                            CommonMethods.black_king_checked = temp_w_b_c;
                            temp_board = copy(this,temp_board);
                            if (temp_board.applyMove(CommonMethods.reverseConvert(x, y, temp_x, y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, y));
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
