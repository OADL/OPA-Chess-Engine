package opa.chess;

import java.util.ArrayList;
import java.util.Random;

public class CommonMethods {

    public static boolean stop = false;

    public static boolean en_passant = false;
    public static boolean castling = false;

    public static boolean white_king_checked = false;
    public static boolean black_king_checked = false;

    public static int depth = 4;
    public static int player = 1;

    public static int convert(char C) {
        int I = -1;
        switch (C) {
            case 'a':
                I = Character.getNumericValue('0');
                break;
            case 'b':
                I = Character.getNumericValue('1');
                break;
            case 'c':
                I = Character.getNumericValue('2');
                break;
            case 'd':
                I = Character.getNumericValue('3');
                break;
            case 'e':
                I = Character.getNumericValue('4');
                break;
            case 'f':
                I = Character.getNumericValue('5');
                break;
            case 'g':
                I = Character.getNumericValue('6');
                break;
            case 'h':
                I = Character.getNumericValue('7');
                break;
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
                I = 8 - Character.getNumericValue(C);
                break;
        }
        return I;
    }

    public static String rev_convert(int x1, int y1, int x2, int y2) {
        String move = "";
        move += (char) (x1 + 97);
        move += 8 - y1;
        move += (char) (x2 + 97);
        move += 8 - y2;
        return move;
    }

    public static Piece get_piece_on_square(int sqX, int sqY, ArrayList<Piece> pieces) {
        for (int i = 0; i < pieces.size(); i++) {
            if (pieces.get(i).get_x() == sqX && pieces.get(i).get_y() == sqY) {
                return pieces.get(i);
            }
        }
        return null;
    }

    public static Piece get_piece_on_square(String Type, boolean Color, ArrayList<Piece> pieces) {
        for (int i = 0; i < pieces.size(); i++) {
            if (pieces.get(i).get_type() == Type && pieces.get(i).get_color() == Color) {
                return pieces.get(i);
            }
        }
        return null;
    }

    public static String[] rand_moves(int x) {
        String[] result = new String[x];
        for (int j = 0; j < x; j++) {

            char[] nums = "12345678".toCharArray();
            char[] chars = "abcdefgh".toCharArray();
            StringBuilder sb = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 4; i++) {
                char c = chars[random.nextInt(chars.length)];
                char n = nums[random.nextInt(nums.length)];
                if (i % 2 == 0) {
                    sb.append(c);
                } else {
                    sb.append(n);
                }
            }

            String output = sb.toString();
            result[j] = output;

        }
        return result;
    }
}
