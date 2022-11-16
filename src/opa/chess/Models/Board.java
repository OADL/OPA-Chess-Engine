package opa.chess.Models;

import opa.chess.Config.CommonMethods;
import opa.chess.Config.Configurations;
import opa.chess.Enums.Color;
import opa.chess.Enums.MoveType;
import opa.chess.Enums.PieceType;
import opa.chess.Models.Pieces.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.io.PrintStream;

import static opa.chess.Config.CommonMethods.createEmptyBoard;

public class Board {

    private Square[][] squares = createEmptyBoard();
    private ArrayList<Piece> dead = new ArrayList<>();
    private ArrayList<Move> moves = new ArrayList<>();

    public Board() {
        init();
    }

    public Board copy() {
        Board newBoard = new Board();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                newBoard.squares[i][j] = this.squares[i][j].clone();
            }
        }
        if (!this.dead.isEmpty()) {
            newBoard.dead = new ArrayList<>();
            for (int i = 0; i < this.dead.size(); i++) {
                newBoard.dead.add(this.dead.get(i).clone());
            }
        }
        return newBoard;
    }

    private void init() {
        for (int i = 0; i < 8; i++) {
            squares[i][6].setPiece(new Pawn(Color.WHITE));
            squares[i][1].setPiece(new Pawn(Color.BLACK));
        }
        //up
        squares[2][0].setPiece(new Bishop(Color.BLACK));
        squares[5][0].setPiece(new Bishop(Color.BLACK));
        squares[1][0].setPiece(new Knight(Color.BLACK));
        squares[6][0].setPiece(new Knight(Color.BLACK));
        squares[0][0].setPiece(new Rook(Color.BLACK));
        squares[7][0].setPiece(new Rook(Color.BLACK));
        squares[3][0].setPiece(new Queen(Color.BLACK));
        squares[4][0].setPiece(new King(Color.BLACK));

        //down
        squares[2][7].setPiece(new Bishop(Color.WHITE));
        squares[5][7].setPiece(new Bishop(Color.WHITE));
        squares[1][7].setPiece(new Knight(Color.WHITE));
        squares[6][7].setPiece(new Knight(Color.WHITE));
        squares[0][7].setPiece(new Rook(Color.WHITE));
        squares[7][7].setPiece(new Rook(Color.WHITE));
        squares[3][7].setPiece(new Queen(Color.WHITE));
        squares[4][7].setPiece(new King(Color.WHITE));
    }

    public void print() {
        PrintStream printStream = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        System.out.println("===================================");

        for (int i = 0; i < 8; i++) {
            System.out.print("| ");
            for (int j = 0; j < 8; j++) {
                Piece piece = squares[j][i].getPiece();
                printStream.print((piece != null)? piece.toString(): " ");
                if (j < 7) {
                    System.out.print(" : ");
                }
            }
            System.out.println(" | " + (8 - i));
        }
        System.out.println("  a   b   c   d   e   f   g   h");
    }

    public ArrayList<Piece> getDead() {
        return dead;
    }

    public ArrayList<Move> getMoves() {
        return moves;
    }

    public Move getLastMove() {
        return (moves.size()>0)? moves.get(moves.size()-1) : null;
    }

    public Square[][] getSquares() {
        return squares;
    }

    public boolean handleMove(String move) {
        if (move == null || move.length() < 4) {
            return false;
        }
        int X1 = CommonMethods.convert(move.charAt(0)),
            Y1 = CommonMethods.convert(move.charAt(1)),
            X2 = CommonMethods.convert(move.charAt(2)),
            Y2 = CommonMethods.convert(move.charAt(3));

        if(X2 > 7 || X2 < 0 || Y2 > 7 || Y2 < 0) return false;
        Board tempBoard = copy();
        Square source = findSquare(X1, Y1, tempBoard.getSquares());
        Piece piece = source.getPiece();
        Square destination = tempBoard.getSquares()[X2][Y2];
        if (piece == null) {
            return false;
        }

        MoveType moveType = piece.checkMove(source, destination, tempBoard);

        if ((piece.getColor() == Color.WHITE && Configurations.player == 2) || (piece.getColor() == Color.BLACK && Configurations.player == 1)) {
            return false;
        }
        if (move.length() == 4 && moveType == MoveType.PROMOTION) {//reach end of board without promotion
            return false;
        }
        if (moveType == MoveType.INVALID) {
            return false;
        }

        if (piece.isFirstMove() && moveType == MoveType.CASTLING) {//Handle Castling
            int rookX = X2 > source.getX() ? 7 : 0;//get rook's location
            Square rook = findSquare(rookX, Y2, tempBoard.getSquares());
            Board castlingTempBoard = copy();
            Square castlingTempKing = findSquare(X1, Y1, castlingTempBoard.getSquares());
            if(castlingTempKing == null || rook == null) return false;
            if (rookX == 7) {
                for (int newX = X1 + 1 ; newX < rookX ; newX++) {
                    applyMove(castlingTempKing, findSquare(newX,Y1, castlingTempBoard.getSquares()));
                    castlingTempKing = findSquare(newX,Y1, castlingTempBoard.getSquares());
                    if (kingRisked(castlingTempKing, castlingTempBoard)) {
                        return false;
                    }
                }
            } else {
                for (int newX = X1 - 1 ; newX > rookX+1 ; newX--) {
                    applyMove(castlingTempKing, findSquare(newX,Y1, castlingTempBoard.getSquares()));
                    castlingTempKing = findSquare(newX,Y1, castlingTempBoard.getSquares());
                    if (kingRisked(castlingTempKing, castlingTempBoard)) {
                        return false;
                    }
                }
            }
            applyMove(rook, findSquare(rookX > 0 ? 5 : 3,Y2));
        }

        if (Math.abs(X1 - X2) == 1 && moveType == MoveType.EN_PASSANT) {
            applyEnPassantEat(source, destination, tempBoard.getDead(), tempBoard.getSquares());
        }else{
            applyEatIfAvailable(source, destination, tempBoard.getDead());
        }

        if (move.length() == 5 && moveType == MoveType.PROMOTION) {//Handle Promotion
            if (!applyPromotion(move.charAt(4), destination, piece.getColor())) return false;
        }

        Square king = findSquare(PieceType.KING, piece.getColor(), tempBoard.getSquares());
        Square oppositeKing = findSquare(PieceType.KING, (piece.getColor() == Color.WHITE)? Color.BLACK : Color.WHITE, tempBoard.getSquares());
        if (kingRisked(king, tempBoard)) {
            return false;
        } else if (kingRisked(oppositeKing, tempBoard)) {
            if(oppositeKing != null && oppositeKing.getPiece() != null) ((King)oppositeKing.getPiece()).setChecked(true);
        }
        if(king != null && king.getPiece() != null) ((King)king.getPiece()).setChecked(false);

        if (Configurations.player == 1) {
            Configurations.player = 2;
        } else {
            Configurations.player = 1;
        }
        this.moves.add(new Move(piece,source,destination,moveType));
        this.squares = tempBoard.getSquares();
        this.dead = tempBoard.getDead();
        piece.setFirstMove(false);
        return true;
    }

    private void applyMove(Square source, Square destination) {
        destination.setPiece(source.getPiece());
        source.setPiece(null);
    }

    private void applyEatIfAvailable(Square source, Square destination, ArrayList<Piece> dead) {
        Piece deadPiece = destination.getPiece();
        if(deadPiece != null && source.getPiece().canEat(deadPiece)) {
            dead.add(deadPiece);
        }
        applyMove(source,destination);
    }

    private void applyEnPassantEat(Square source, Square destination, ArrayList<Piece> dead, Square[][] squares) {
        Square deadSquare = findSquare(destination.getX(), source.getY(), squares);
        Piece deadPiece = deadSquare.getPiece();
        if(deadPiece != null && source.getPiece().canEat(deadPiece)) {
            dead.add(deadPiece);
            deadSquare.setPiece(null);
        }
        applyMove(source,destination);
    }

    private boolean applyPromotion(char promotion, Square square, Color color) {
        switch (promotion) {
            case 'p':
            case 'P':
            case 'k':
            case 'K':
                return false;
            case 'q':
            case 'Q':
                square.setPiece(new Queen(color));
                break;
            case 'r':
            case 'R':
                square.setPiece(new Rook(color));
                break;
            case 'n':
            case 'N':
                square.setPiece(new Knight(color));
                break;
            case 'b':
            case 'B':
                square.setPiece(new Bishop(color));
                break;
            default:
                return true;
        }
        return true;
    }

    private boolean kingRisked(Square kingSquare, Board board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece king = kingSquare.getPiece();
                Square pieceSquare = board.getSquares()[i][j];
                Piece piece = pieceSquare.getPiece();
                if (piece != null && piece.getColor() != king.getColor() && piece.checkMove(pieceSquare, kingSquare, board) != MoveType.INVALID) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isKingChecked(Color color) {
        Square king = findSquare(PieceType.KING, color, this.getSquares());
        if(king != null && king.getPiece() != null){
            return ((King)king.getPiece()).isChecked();
        }
        return false;
    }

    public ArrayList<String> nextMoves(int player) {
        ArrayList<String> moves = new ArrayList<>();
        Board temp_board = copy();
        int temp_player = Configurations.player;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square p = temp_board.squares[i][j];
                if(p.getPiece() == null) continue;
                int x = p.getX();
                int y = p.getY();
                int temp_x;
                int temp_y;
                if ((player == 1 && p.getPiece().getColor() == Color.WHITE) || (player == 2 && p.getPiece().getColor() == Color.BLACK)) {
                    switch (p.getPiece().getType()) {
                        case PAWN:
                            if (p.getPiece().getColor() == Color.WHITE) {
                                Configurations.player = temp_player;
                                temp_board = copy();
                                temp_y = y - 1;
                                if (temp_y == 0) {
                                    String s = CommonMethods.reverseConvert(x, y, x, temp_y) + 'q';
                                    if (temp_board.handleMove(s)) {
                                        moves.add(s);
                                        s = CommonMethods.reverseConvert(x, y, x, temp_y) + 'n';
                                        moves.add(s);
                                        s = CommonMethods.reverseConvert(x, y, x, temp_y) + 'b';
                                        moves.add(s);
                                        s = CommonMethods.reverseConvert(x, y, x, temp_y) + 'r';
                                        moves.add(s);
                                    }
                                }
                                Configurations.player = temp_player;
                                temp_board = copy();
                                temp_y = y - 1;
                                temp_x = x - 1;
                                if (temp_y == 0) {
                                    String s = CommonMethods.reverseConvert(x, y, temp_x, temp_y) + 'q';
                                    if (temp_board.handleMove(s)) {
                                        moves.add(s);
                                        s = CommonMethods.reverseConvert(x, y, temp_x, temp_y) + 'n';
                                        moves.add(s);
                                        s = CommonMethods.reverseConvert(x, y, temp_x, temp_y) + 'b';
                                        moves.add(s);
                                        s = CommonMethods.reverseConvert(x, y, temp_x, temp_y) + 'r';
                                        moves.add(s);
                                    }
                                }
                                Configurations.player = temp_player;
                                temp_board = copy();
                                temp_y = y - 1;
                                temp_x = x + 1;
                                if (temp_y == 0) {
                                    String s = CommonMethods.reverseConvert(x, y, temp_x, temp_y) + 'q';
                                    if (temp_board.handleMove(s)) {
                                        moves.add(s);
                                        s = CommonMethods.reverseConvert(x, y, temp_x, temp_y) + 'n';
                                        moves.add(s);
                                        s = CommonMethods.reverseConvert(x, y, temp_x, temp_y) + 'b';
                                        moves.add(s);
                                        s = CommonMethods.reverseConvert(x, y, temp_x, temp_y) + 'r';
                                        moves.add(s);
                                    }
                                }
                                Configurations.player = temp_player;
                                temp_board = copy();
                                if (temp_y != 0 && temp_board.handleMove(CommonMethods.reverseConvert(x, y, x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, x, temp_y));
                                }
                                Configurations.player = temp_player;
                                temp_board = copy();
                                temp_y = y - 2;
                                if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, x, temp_y));
                                }
                                Configurations.player = temp_player;
                                temp_board = copy();
                                temp_y = y - 1;
                                temp_x = x - 1;
                                if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                                }
                                Configurations.player = temp_player;
                                temp_board = copy();
                                temp_y = y - 1;
                                temp_x = x + 1;
                                if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                                }
                            } else {
                                Configurations.player = temp_player;
                                temp_board = copy();
                                temp_y = y + 1;
                                if (temp_y == 7) {
                                    String s = CommonMethods.reverseConvert(x, y, x, temp_y) + 'q';
                                    if (temp_board.handleMove(s)) {
                                        moves.add(s);
                                        s = CommonMethods.reverseConvert(x, y, x, temp_y) + 'n';
                                        moves.add(s);
                                        s = CommonMethods.reverseConvert(x, y, x, temp_y) + 'b';
                                        moves.add(s);
                                        s = CommonMethods.reverseConvert(x, y, x, temp_y) + 'r';
                                        moves.add(s);
                                    }
                                }
                                Configurations.player = temp_player;
                                temp_board = copy();
                                if (temp_y != 7 && temp_board.handleMove(CommonMethods.reverseConvert(x, y, x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, x, temp_y));
                                }
                                Configurations.player = temp_player;
                                temp_board = copy();
                                temp_y = y + 2;
                                if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, x, temp_y));
                                }
                                Configurations.player = temp_player;
                                temp_board = copy();
                                temp_y = y + 1;
                                temp_x = x - 1;
                                if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                                }
                                Configurations.player = temp_player;
                                temp_board = copy();
                                temp_y = y + 1;
                                temp_x = x + 1;
                                if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
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
                                    Configurations.player = temp_player;
                                    temp_board = copy();
                                    if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, y))) {
                                        moves.add(CommonMethods.reverseConvert(x, y, temp_x, y));
                                    }
                                    temp_x--;
                                } else if (temp_x > x) {
                                    Configurations.player = temp_player;
                                    temp_board = copy();
                                    if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, y))) {
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
                                    Configurations.player = temp_player;
                                    temp_board = copy();
                                    if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, x, temp_y))) {
                                        moves.add(CommonMethods.reverseConvert(x, y, x, temp_y));
                                    }
                                    temp_y--;
                                } else if (temp_y > y) {
                                    Configurations.player = temp_player;
                                    temp_board = copy();
                                    if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, x, temp_y))) {
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
                                    Configurations.player = temp_player;
                                    temp_board = copy();
                                    if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                        moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));

                                    }
                                    temp_x--;
                                    temp_y--;
                                } else if (temp_x > x) {
                                    Configurations.player = temp_player;
                                    temp_board = copy();
                                    if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
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
                                    Configurations.player = temp_player;
                                    temp_board = copy();
                                    if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                        moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                                    }
                                    temp_y--;
                                    temp_x++;
                                } else if (temp_y > y) {
                                    Configurations.player = temp_player;
                                    temp_board = copy();
                                    if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
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
                            Configurations.player = temp_player;
                            temp_board = copy();
                            if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                            temp_x = x - 1;
                            temp_y = y + 2;
                            Configurations.player = temp_player;
                            temp_board = copy();
                            if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                            temp_x = x - 2;
                            temp_y = y - 1;
                            Configurations.player = temp_player;
                            temp_board = copy();
                            if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                            temp_x = x - 2;
                            temp_y = y + 1;
                            Configurations.player = temp_player;
                            temp_board = copy();
                            if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                            temp_x = x + 1;
                            temp_y = y - 2;
                            Configurations.player = temp_player;
                            temp_board = copy();
                            if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                            temp_x = x + 1;
                            temp_y = y + 2;
                            Configurations.player = temp_player;
                            temp_board = copy();
                            if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                            temp_x = x + 2;
                            temp_y = y - 1;
                            Configurations.player = temp_player;
                            temp_board = copy();
                            if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                            temp_x = x + 2;
                            temp_y = y + 1;
                            Configurations.player = temp_player;
                            temp_board = copy();
                            if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
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
                                    Configurations.player = temp_player;
                                    temp_board = copy();
                                    if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, y))) {
                                        moves.add(CommonMethods.reverseConvert(x, y, temp_x, y));
                                    }
                                    temp_x--;
                                } else if (temp_x > x) {
                                    Configurations.player = temp_player;
                                    temp_board = copy();
                                    if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, y))) {
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
                                    Configurations.player = temp_player;
                                    temp_board = copy();
                                    if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, x, temp_y))) {
                                        moves.add(CommonMethods.reverseConvert(x, y, x, temp_y));
                                    }
                                    temp_y--;
                                } else if (temp_y > y) {
                                    Configurations.player = temp_player;
                                    temp_board = copy();
                                    if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, x, temp_y))) {
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
                                    Configurations.player = temp_player;
                                    temp_board = copy();
                                    if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                        moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                                    }
                                    temp_x--;
                                    temp_y--;
                                } else if (temp_x > x) {
                                    Configurations.player = temp_player;
                                    temp_board = copy();
                                    if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
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
                                    Configurations.player = temp_player;
                                    temp_board = copy();
                                    if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                        moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                                    }
                                    temp_y--;
                                    temp_x++;
                                } else if (temp_y > y) {
                                    Configurations.player = temp_player;
                                    temp_board = copy();
                                    if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
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
                            Configurations.player = temp_player;
                            temp_board = copy();
                            if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                            temp_x = x - 1;
                            temp_y = y + 1;
                            Configurations.player = temp_player;
                            temp_board = copy();
                            if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                            temp_x = x - 1;
                            temp_y = y;
                            Configurations.player = temp_player;
                            temp_board = copy();
                            if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                            temp_x = x;
                            temp_y = y - 1;
                            Configurations.player = temp_player;
                            temp_board = copy();
                            if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                            temp_x = x;
                            temp_y = y + 1;
                            Configurations.player = temp_player;
                            temp_board = copy();
                            if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                            temp_x = x + 1;
                            temp_y = y - 1;
                            Configurations.player = temp_player;
                            temp_board = copy();
                            if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                            temp_x = x + 1;
                            temp_y = y + 1;
                            Configurations.player = temp_player;
                            temp_board = copy();
                            if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                            temp_x = x + 1;
                            temp_y = y;
                            Configurations.player = temp_player;
                            temp_board = copy();
                            if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, temp_y))) {
                                moves.add(CommonMethods.reverseConvert(x, y, temp_x, temp_y));
                            }
                            Configurations.player = temp_player;
                            temp_board = copy();
                            if ((p.getPiece().getColor() == Color.WHITE && y == 7) || (p.getPiece().getColor() == Color.BLACK && y == 0)) {
                                temp_x = 2;
                                Configurations.player = temp_player;
                                temp_board = copy();
                                if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, temp_x, y));
                                }
                                temp_x = 6;
                                Configurations.player = temp_player;
                                temp_board = copy();
                                if (temp_board.handleMove(CommonMethods.reverseConvert(x, y, temp_x, y))) {
                                    moves.add(CommonMethods.reverseConvert(x, y, temp_x, y));
                                }
                            }
                            break;
                    }
                }
            }
        }
        Configurations.player = temp_player;
        return moves;
    }

    public Square findSquare(int sqX, int sqY) {
        return squares[sqX][sqY];
    }

    private Square findSquare(int sqX, int sqY, Square[][] squares) {
        return squares[sqX][sqY];
    }

    private Square findSquare(PieceType type, Color color, Square[][] squares) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = squares[i][j].getPiece();
                if (piece != null && piece.getType().equals(type) && piece.getColor() == color) {
                    return squares[i][j];
                }
            }
        }
        return null;
    }
}
