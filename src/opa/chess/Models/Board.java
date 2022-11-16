package opa.chess.Models;

import opa.chess.Config.CommonMethods;
import opa.chess.Enums.Color;
import opa.chess.Enums.PieceType;
import opa.chess.Models.Pieces.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.io.PrintStream;

import static opa.chess.Config.CommonMethods.createEmptyBoard;

public class Board {

    private String[][] board = createEmptyBoard();

    private ArrayList<Piece> pieces = new ArrayList<>();
    private ArrayList<Piece> dead = new ArrayList<>();

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
            Piece pawn = new Pawn(Color.WHITE, i, 6);//down
            pieces.add(pawn);
            pawn = new Pawn(Color.BLACK, i, 1);//up
            pieces.add(pawn);
        }
        Piece piece;
        //up
        piece = new Bishop(Color.BLACK, 2, 0);
        pieces.add(piece);
        piece = new Bishop(Color.BLACK, 5, 0);
        pieces.add(piece);
        piece = new Knight(Color.BLACK, 1, 0);
        pieces.add(piece);
        piece = new Knight(Color.BLACK, 6, 0);
        pieces.add(piece);
        piece = new Rook(Color.BLACK, 0, 0);
        pieces.add(piece);
        piece = new Rook(Color.BLACK, 7, 0);
        pieces.add(piece);
        piece = new Queen(Color.BLACK, 3, 0);
        pieces.add(piece);
        piece = new King(Color.BLACK, 4, 0);
        pieces.add(piece);

        //down
        piece = new Bishop(Color.WHITE, 2, 7);
        pieces.add(piece);
        piece = new Bishop(Color.WHITE, 5, 7);
        pieces.add(piece);
        piece = new Knight(Color.WHITE, 1, 7);
        pieces.add(piece);
        piece = new Knight(Color.WHITE, 6, 7);
        pieces.add(piece);
        piece =new Rook(Color.WHITE, 0, 7);
        pieces.add(piece);
        piece =new Rook(Color.WHITE, 7, 7);
        pieces.add(piece);
        piece = new Queen(Color.WHITE, 3, 7);
        pieces.add(piece);
        piece = new King(Color.WHITE, 4, 7);
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

    private void update() {
        board = createEmptyBoard();
        for (Piece piece : pieces) {
            board[piece.getX()][piece.getY()] = piece.toString();
        }
    }

    public ArrayList<Piece> getDead() {
        return dead;
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }

    public boolean applyMove(String move) {
        if (move == null || move.length() < 4) {
            return false;
        }
        int X1 = CommonMethods.convert(move.charAt(0)),
            Y1 = CommonMethods.convert(move.charAt(1)),
            X2 = CommonMethods.convert(move.charAt(2)),
            Y2 = CommonMethods.convert(move.charAt(3));
        @SuppressWarnings("unchecked")
        ArrayList<Piece> temp_pieces = (ArrayList<Piece>) pieces.clone(); // temporary changes array list
        @SuppressWarnings("unchecked")
        ArrayList<Piece> temp_dead = (ArrayList<Piece>) dead.clone();// temporary changes array list
        Piece piece = findPiece(X1, Y1, temp_pieces);
        if (piece == null) {
            return false;
        }
        if (move.length() == 4) {
            if (piece.getType() == PieceType.PAWN && (((piece.getColor()==Color.WHITE) && Y2 == 0) || ((piece.getColor()==Color.BLACK) && Y2 == 7))) {//reach end of board without promotion
                return false;
            }
            if ((piece.getColor() == Color.WHITE && CommonMethods.player == 1) || (piece.getColor() == Color.BLACK && CommonMethods.player == 2)) {
                if (piece.checkMove(X2, Y2, this)) {
                    if (piece.isFirstMove()) {
                        if (piece.getType() == PieceType.KING) {
                            if (CommonMethods.castling) {//check castling variable
                                int rookX = X2 > piece.getX() ? 7 : 0;//get rook's location
                                Piece rook = findPiece(rookX, Y2, temp_pieces);
                                Board temp_board = new Board();
                                temp_board = copy(this,temp_board);
                                Piece temp_king = findPiece(X1, Y1, temp_board.pieces);
                                boolean temp_castl = CommonMethods.castling;
                                boolean temp_enpassant = CommonMethods.en_passant;
                                if(temp_king != null) {
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
                        }
                        piece.setFirstMove(false);
                    }
                    if (Math.abs(X1 - X2) == 1 && CommonMethods.en_passant) {
                        piece.setX(X2);
                        piece.setY(Y2);
                        applyEatIfAvailable(piece, temp_pieces, temp_dead);

                        CommonMethods.en_passant = false;
                        if (CommonMethods.player == 1) {
                            CommonMethods.player = 2;
                        } else {
                            CommonMethods.player = 1;
                        }
                        if (kingRisked(findPiece(PieceType.KING, piece.getColor(), temp_pieces), temp_pieces)) {
                            return false;
                        } else if (kingRisked(findPiece(PieceType.KING, (piece.getColor() == Color.WHITE)? Color.BLACK : Color.WHITE, temp_pieces), temp_pieces)) {
                            if (piece.getColor() == Color.BLACK) {
                                CommonMethods.white_king_checked = true;
                            } else {
                                CommonMethods.black_king_checked = true;
                            }
                        }
                        if (piece.getColor() == Color.WHITE) {
                            CommonMethods.white_king_checked = false;
                        } else {
                            CommonMethods.black_king_checked = false;
                        }
                        pieces = temp_pieces;
                        dead = temp_dead;
                        update();

                        return true;

                    } else {
                        piece.setX(X2);
                        piece.setY(Y2);
                        applyEatIfAvailable(piece, temp_pieces, temp_dead);
                        if (kingRisked(findPiece(PieceType.KING, piece.getColor(), temp_pieces), temp_pieces)) {
                            return false;
                        } else if (kingRisked(findPiece(PieceType.KING, (piece.getColor() == Color.WHITE)? Color.BLACK : Color.WHITE, temp_pieces), temp_pieces)) {
                            if (piece.getColor() == Color.BLACK) {
                                CommonMethods.white_king_checked = true;
                            } else {
                                CommonMethods.black_king_checked = true;
                            }
                        }
                        if (piece.getColor() == Color.WHITE) {
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
            if ((piece.getColor() == Color.WHITE && CommonMethods.player == 1) || (piece.getColor() == Color.BLACK && CommonMethods.player == 2)) {
                if (piece.getType() == PieceType.PAWN) {
                    if (piece.checkMove(X2, Y2, this)) {
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
                            piece.setX(X2);
                            piece.setY(Y2);
                            applyEatIfAvailable(piece, temp_pieces, temp_dead);
                            if (kingRisked(findPiece(PieceType.KING, piece.getColor(), temp_pieces), temp_pieces)) {
                                return false;
                            } else if (kingRisked(findPiece(PieceType.KING, (piece.getColor() == Color.WHITE)? Color.BLACK : Color.WHITE, temp_pieces), temp_pieces)) {
                                if (piece.getColor() == Color.BLACK) {
                                    CommonMethods.white_king_checked = true;
                                } else {
                                    CommonMethods.black_king_checked = true;
                                }
                            }
                            if (piece.getColor() == Color.WHITE) {
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

    private void applyEatIfAvailable(Piece piece, ArrayList<Piece> pieces, ArrayList<Piece> dead) {
        Piece deadPiece = findPiece(piece.getX(), piece.getY(), pieces);
        if(deadPiece != null && piece.canEat(deadPiece)) {
            pieces.remove(deadPiece);
            deadPiece.setY(-1);
            deadPiece.setX(-1);
            dead.add(deadPiece);
        }
    }

    private boolean kingRisked(Piece king, ArrayList<Piece> pieces) {
        for (Piece piece : pieces) {
            if (piece.getColor() != king.getColor() && piece.checkMove(king.getX(), king.getY(), this)) {
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
            if ((player == 1 && p.getColor() == Color.WHITE) || (player == 2 && p.getColor() == Color.BLACK)) {
                switch (p.getType()) {
                    case PAWN:
                        if (p.getColor() == Color.WHITE) {
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
                        if ((p.getColor() == Color.WHITE && y == 7) || (p.getColor() == Color.BLACK && y == 0)) {
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

    public Piece findPiece(int sqX, int sqY) {
        for (Piece piece : pieces) {
            if (piece.getX() == sqX && piece.getY() == sqY) {
                return piece;
            }
        }
        return null;
    }

    private Piece findPiece(int sqX, int sqY, ArrayList<Piece> pieces) {
        for (Piece piece : pieces) {
            if (piece.getX() == sqX && piece.getY() == sqY) {
                return piece;
            }
        }
        return null;
    }

    private Piece findPiece(PieceType type, Color color, ArrayList<Piece> pieces) {
        for (Piece piece : pieces) {
            if (piece.getType().equals(type) && piece.getColor() == color) {
                return piece;
            }
        }
        return null;
    }
}
