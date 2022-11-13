package opa.chess;

import java.util.ArrayList;

public class Piece {

    private String Type;
    private boolean Location; //False Down  True UP
    private boolean Color;    //False White True black
    private boolean first_move;
    private int X;
    private int Y;

    public Piece(String Type, boolean Location, int X, int Y) {
        this.Type = Type;
        this.Location = Location;
        this.X = X;
        this.Y = Y;
        this.first_move = true;
    }

    public Piece(String Type, boolean Location, boolean Color, int X, int Y) {
        this.Type = Type;
        this.Location = Location;
        this.Color = Color;
        this.X = X;
        this.Y = Y;
        this.first_move = true;
    }

    public String get_type() {
        return Type;
    }

    public int get_x() {
        return X;
    }

    public int get_y() {
        return Y;
    }

    public boolean get_color() {
        return Color;
    }

    public boolean get_location() {
        return Location;
    }

    public boolean get_firstmove() {
        return this.first_move;
    }

    public void set_type(String Type) {
        this.Type = Type;
    }

    public void set_x(int X) {
        this.X = X;
    }

    public void set_y(int Y) {
        this.Y = Y;
    }

    public void set_color(boolean Color) {
        this.Color = Color;
    }

    public void set_location(boolean Location) {
        this.Location = Location;
    }

    public void set_firstmove(boolean first_move) {
        this.first_move = first_move;
    }

    public Piece can_eat(int destX, int destY, ArrayList<Piece> pieces) {

        Piece returned = CommonMethods.get_piece_on_square(destX, destY, pieces);

        if (returned != null) {
            if (returned.get_color() != this.Color) {
                return returned;
            }
        }
        return null;

    }

    public boolean check_move(int x2, int y2, ArrayList<Piece> pieces) {

        if (x2 == this.X && y2 == this.Y) { //checks if not moved
            return false;
        }
        if (x2 < 0 || x2 > 7 || y2 > 7 || y2 < 0) { //checks if out of boundary
            return false;
        }
        switch (this.Type) { //checks if valid

            case "Pawn":

                if (this.Location) {// UP going down  
                    if ((y2 == this.Y + 1) && x2 == this.X) {// check if this is a 1 step forward 
                        if (!blocked(x2, y2, pieces)) {
                            CommonMethods.en_passant = false;
                            return true;
                        } else {
                            return false;
                        }
                    } else if ((y2 == this.Y + 2) && x2 == this.X) { //check if this is a 2 step forward

                        if (this.first_move == true) { // check if this is the first move
                            if (!blocked(x2, y2, pieces)) {
                                CommonMethods.en_passant = true;
                                return (true);
                            }
                        }
                    } else if ((y2 == this.Y + 1) && (x2 == this.X + 1 || x2 == this.X - 1)) {//diagonal move
                        Piece adjpiece = CommonMethods.get_piece_on_square(x2, this.Y, pieces);
                        if (adjpiece != null) {
                            if (adjpiece.get_type() == "Pawn" && CommonMethods.en_passant == true && adjpiece.get_firstmove() == true && adjpiece.get_color() != this.Color) {
                                return true;
                            }
                        }
                        if (can_eat(x2, y2, pieces) != null) {//check if an opponents piece can be eaten by this move
                            CommonMethods.en_passant = false;
                            return true;
                        }
                    }
                } else // DOWN going up
                if ((y2 == this.Y - 1) && x2 == this.X) {// check if this is a 1 step forward 
                    if (!blocked(x2, y2, pieces)) {
                        CommonMethods.en_passant = false;
                        return true;
                    } else {
                        return false;
                    }

                } else if ((y2 == this.Y - 2) && x2 == this.X) { //check if this is a 2 step forward

                    if (this.first_move == true) { // check if this is the first move
                        if (!blocked(x2, y2, pieces)) {
                            CommonMethods.en_passant = true;
                            return (true);
                        }
                    }
                } else if ((y2 == this.Y - 1) && (x2 == this.X + 1 || x2 == this.X - 1)) {//diagonal move
                    Piece adjpiece = CommonMethods.get_piece_on_square(x2, this.Y, pieces);
                    if (adjpiece != null) {
                        if (adjpiece.get_type() == "Pawn" && CommonMethods.en_passant == true && adjpiece.get_firstmove() == true && adjpiece.get_color() != this.Color) {
                            return true;
                        }
                    }
                    if (can_eat(x2, y2, pieces) != null) { //check if an opponents piece can be eaten by this move
                        CommonMethods.en_passant = false;
                        return true;
                    }
                }

                break;

            case "Knight":
                if ((y2 == this.Y + 2 || y2 == this.Y - 2) && (x2 == this.X + 1 || x2 == this.X - 1)) {
                    CommonMethods.en_passant = false;
                    return (!blocked(x2, y2, pieces));
                } else if ((y2 == this.Y + 1 || y2 == this.Y - 1) && (x2 == this.X + 2 || x2 == this.X - 2)) {
                    CommonMethods.en_passant = false;
                    return (!blocked(x2, y2, pieces));
                }
                break;

            case "Bishop":

                if (Math.abs(y2 - this.Y) == Math.abs(x2 - this.X)) {
                    CommonMethods.en_passant = false;
                    return (!blocked(x2, y2, pieces));
                }
                break;

            case "Rook":
                if (y2 != this.Y && x2 == this.X) {
                    CommonMethods.en_passant = false;
                    return (!blocked(x2, y2, pieces));
                } else if (y2 == this.Y && x2 != this.X) {
                    CommonMethods.en_passant = false;
                    return (!blocked(x2, y2, pieces));
                }
                break;

            case "Queen":

                if (Math.abs(y2 - this.Y) == Math.abs(x2 - this.X)) {
                    CommonMethods.en_passant = false;
                    return (!blocked(x2, y2, pieces));
                } else if (y2 != this.Y && x2 == this.X) {
                    CommonMethods.en_passant = false;
                    return (!blocked(x2, y2, pieces));
                } else if (y2 == this.Y && x2 != this.X) {
                    CommonMethods.en_passant = false;
                    return (!blocked(x2, y2, pieces));
                }
                break;

            case "King":
                if ((x2 == 2 || x2 == 6) && (y2 == 0 || y2 == 7)) {//all possible castling possitions

                    if (this.first_move) {//checks if this is the first move for the King
                        if ((this.Color == false && CommonMethods.white_king_checked) || (this.Color == true && CommonMethods.black_king_checked))//if checked cant castle
                        {
                            return false;
                        }
                        int rookX = x2 > this.X ? 7 : 0;//get the rooks position
                        Piece rook = CommonMethods.get_piece_on_square(rookX, y2, pieces);
                        if (rook != null) {
                            if (rook.get_firstmove()) {//checks if the rooks hasn't moved yet
                                for (int i = 0; i < Math.abs(rookX - this.X) - 1; i++) {//checks if there is nothing between them
                                    if (rookX == 7) {
                                        if (blocked(rookX - 1 - i, y2, pieces)) {
                                            return false;
                                        }
                                    } else if (rookX == 0) {
                                        if (blocked(rookX + 1 + i, y2, pieces)) {
                                            return false;
                                        }
                                    }
                                }
                                CommonMethods.castling = true;
                                return true;
                            }
                        }
                    }
                }

                if ((Math.abs(y2 - this.Y) == 1 || Math.abs(y2 - this.Y) == 0) && (Math.abs(x2 - this.X) == 1 || Math.abs(x2 - this.X) == 0)) {
                    CommonMethods.en_passant = false;
                    return (!blocked(x2, y2, pieces));
                }
                break;
        }
        return false;
    }

    private boolean pieceOnSquare(int x, int y, ArrayList<Piece> pieces) {
        for (int i = 0; i < pieces.size(); i++) {
            if (pieces.get(i).get_x() == x && pieces.get(i).get_y() == y) {
                return true;
            }
        }
        return false;
    }

    protected boolean blocked(int x2, int y2, ArrayList<Piece> pieces) {
        int dX = x2 > this.X ? 1 : -1;
        int dY = y2 > this.Y ? 1 : -1;
        switch (this.Type) {
            case "Pawn":
                for (int i = 1; i <= Math.abs(y2 - this.Y); i++) {
                    if (pieceOnSquare(this.X, this.Y + i * dY, pieces)) {
                        return true;
                    }
                }
                break;
            case "Rook":
                if (x2 == this.X) {
                    for (int i = 1; i < Math.abs(y2 - this.Y); i++) {
                        if (pieceOnSquare(this.X, this.Y + i * dY, pieces)) {
                            return true;
                        }
                    }
                } else if (y2 == this.Y) {
                    for (int i = 1; i < Math.abs(x2 - this.X); i++) {
                        if (pieceOnSquare(this.X + i * dX, this.Y, pieces)) {
                            return true;
                        }
                    }
                }
                for (int i = 0; i < pieces.size(); i++) {
                    if (pieces.get(i).get_x() == x2 && pieces.get(i).get_y() == y2) {
                        if (pieces.get(i).get_color() == this.Color) {
                            return true;
                        }
                    }
                }
                break;
            case "Bishop":
                for (int i = 1; i < Math.abs(x2 - this.X); ++i) {
                    if (pieceOnSquare(this.X + i * dX, this.Y + i * dY, pieces)) {
                        return true;
                    }
                }
                for (int i = 0; i < pieces.size(); i++) {
                    if (pieces.get(i).get_x() == x2 && pieces.get(i).get_y() == y2) {
                        if (pieces.get(i).get_color() == this.Color) {
                            return true;
                        }
                    }
                }
                break;
            case "Knight":
                for (int i = 0; i < pieces.size(); i++) {
                    if (pieces.get(i).get_x() == x2 && pieces.get(i).get_y() == y2) {
                        if (pieces.get(i).get_color() == this.Color) {
                            return true;
                        }
                    }
                }
                break;
            case "Queen":
                if (x2 == this.X) {
                    for (int i = 1; i < Math.abs(y2 - this.Y); i++) {
                        if (pieceOnSquare(this.X, this.Y + i * dY, pieces)) {

                            return true;
                        }
                    }
                } else if (y2 == this.Y) {
                    for (int i = 1; i < Math.abs(x2 - this.X); i++) {
                        if (pieceOnSquare(this.X + i * dX, this.Y, pieces)) {
                            return true;
                        }
                    }
                } else {
                    for (int i = 1; i < Math.abs(x2 - this.X); ++i) {
                        if (pieceOnSquare(this.X + i * dX, this.Y + i * dY, pieces)) {
                            return true;
                        }
                    }
                }
                for (int i = 0; i < pieces.size(); i++) {
                    if (pieces.get(i).get_x() == x2 && pieces.get(i).get_y() == y2) {
                        if (pieces.get(i).get_color() == this.Color) {
                            return true;
                        }
                    }
                }
                break;
            case "King":
                for (int i = 0; i < pieces.size(); i++) {
                    if (pieces.get(i).get_x() == x2 && pieces.get(i).get_y() == y2) {
                        if (pieces.get(i).get_color() == this.Color) {
                            return true;
                        }
                    }
                }
                break;
        }
        return false;
    }
}
