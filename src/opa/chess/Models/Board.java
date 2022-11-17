package opa.chess.Models;

import opa.chess.Config.CommonMethods;
import opa.chess.Config.Configurations;
import opa.chess.Enums.Color;
import opa.chess.Enums.MoveType;
import opa.chess.Enums.PieceType;
import opa.chess.Models.Pieces.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
        try {
            if(!processMove(new Move(move))) return false;
            CommonMethods.switchPlayer();
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean handleMove(Move move) {
        if(move == null) return false;
        if(!processMove(move)) return false;
        CommonMethods.switchPlayer();
        return true;
    }

    public boolean processMove(Move move) {
        Board tempBoard = copy();
        Square source = tempBoard.findSquare(move.getSource().getX(), move.getSource().getY());
        Piece piece = source.getPiece();
        if (piece == null) return false;
        Square destination = tempBoard.findSquare(move.getDestination().getX(), move.getDestination().getY());
        MoveType moveType = piece.checkMove(source, destination, tempBoard);
        move.setPiece(piece);
        move.setMoveType(moveType);

        if ((piece.getColor() == Color.WHITE && Configurations.player == 2) || (piece.getColor() == Color.BLACK && Configurations.player == 1)) {
            return false;
        }
        if (move.getPromotion() == null && moveType == MoveType.PROMOTION) {//reach end of board without promotion
            return false;
        }
        if (moveType == MoveType.INVALID) {
            return false;
        }

        if (piece.isFirstMove() && moveType == MoveType.CASTLING) {//Handle Castling
            int rookX = destination.getX() > source.getX() ? 7 : 0;//get rook's location
            Square rook = tempBoard.findSquare(rookX, destination.getY());
            Board castlingTempBoard = copy();
            Square castlingTempKing = castlingTempBoard.findSquare(source.getX(), source.getY());
            if(castlingTempKing == null || rook == null) return false;
            if (rookX == 7) {
                for (int newX = source.getX() + 1 ; newX < rookX ; newX++) {
                    applyMove(castlingTempKing, castlingTempBoard.findSquare(newX,source.getY()));
                    castlingTempKing = castlingTempBoard.findSquare(newX,source.getY());
                    if (kingRisked(castlingTempKing, castlingTempBoard)) {
                        return false;
                    }
                }
            } else {
                for (int newX = source.getX() - 1 ; newX > rookX+1 ; newX--) {
                    applyMove(castlingTempKing, castlingTempBoard.findSquare(newX,source.getY()));
                    castlingTempKing = castlingTempBoard.findSquare(newX,source.getY());
                    if (kingRisked(castlingTempKing, castlingTempBoard)) {
                        return false;
                    }
                }
            }
            applyMove(rook, findSquare(rookX > 0 ? 5 : 3,destination.getY()));
        }

        if (Math.abs(source.getX() - destination.getX()) == 1 && moveType == MoveType.EN_PASSANT) {
            applyEnPassantEat(source, destination, tempBoard);
        }else{
            applyEatIfAvailable(source, destination, tempBoard);
        }

        if (move.getPromotion() != null && moveType == MoveType.PROMOTION) {//Handle Promotion
            if (!applyPromotion(move.getPromotion(), destination, piece.getColor())) return false;
        }

        Square king = tempBoard.findSquare(PieceType.KING, piece.getColor());
        Square oppositeKing = tempBoard.findSquare(PieceType.KING, (piece.getColor() == Color.WHITE)? Color.BLACK : Color.WHITE);
        if (kingRisked(king, tempBoard)) {
            return false;
        } else if (kingRisked(oppositeKing, tempBoard)) {
            if(oppositeKing != null && oppositeKing.getPiece() != null) ((King)oppositeKing.getPiece()).setChecked(true);
        }
        if(king != null && king.getPiece() != null) ((King)king.getPiece()).setChecked(false);

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

    private void applyEatIfAvailable(Square source, Square destination, Board board) {
        Piece deadPiece = destination.getPiece();
        if(deadPiece != null && source.getPiece().canEat(deadPiece)) {
            board.getDead().add(deadPiece);
        }
        applyMove(source,destination);
    }

    private void applyEnPassantEat(Square source, Square destination, Board board) {
        Square deadSquare = board.findSquare(destination.getX(), source.getY());
        Piece deadPiece = deadSquare.getPiece();
        if(deadPiece != null && source.getPiece().canEat(deadPiece)) {
            board.getDead().add(deadPiece);
            deadSquare.setPiece(null);
        }
        applyMove(source,destination);
    }

    private boolean applyPromotion(PieceType promotion, Square square, Color color) {
        switch (promotion) {
            case QUEEN -> square.setPiece(new Queen(color));
            case ROOK -> square.setPiece(new Rook(color));
            case KNIGHT -> square.setPiece(new Knight(color));
            case BISHOP -> square.setPiece(new Bishop(color));
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
        Square king = findSquare(PieceType.KING, color);
        if(king != null && king.getPiece() != null){
            return ((King)king.getPiece()).isChecked();
        }
        return false;
    }

    public List<Move> nextMoves(int player) {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square square = findSquare(i,j);
                if(square.getPiece() == null) continue;
                if ((player == 1 && square.getPiece().getColor() == Color.WHITE) || (player == 2 && square.getPiece().getColor() == Color.BLACK)) {
                    moves.addAll(square.getPiece().nextMoves(square,this));
                }
            }
        }
        return moves;
    }

    public Square findSquare(int sqX, int sqY) {
        return squares[sqX][sqY];
    }

    public Square findSquare(PieceType type, Color color) {
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
