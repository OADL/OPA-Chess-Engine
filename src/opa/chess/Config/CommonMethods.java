package opa.chess.Config;

import opa.chess.Enums.Color;
import opa.chess.Enums.PieceType;
import opa.chess.Models.Pieces.Piece;
import opa.chess.Models.Square;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class CommonMethods {

    public static int convert(char C) {
        int I = switch (C) {
            case 'a' -> Character.getNumericValue('0');
            case 'b' -> Character.getNumericValue('1');
            case 'c' -> Character.getNumericValue('2');
            case 'd' -> Character.getNumericValue('3');
            case 'e' -> Character.getNumericValue('4');
            case 'f' -> Character.getNumericValue('5');
            case 'g' -> Character.getNumericValue('6');
            case 'h' -> Character.getNumericValue('7');
            case '1', '2', '3', '4', '5', '6', '7', '8' -> 8 - Character.getNumericValue(C);
            default -> -1;
        };
        return I;
    }

    public static String reverseConvert(int x1, int y1, int x2, int y2) {
        String move = "";
        move += (char) (x1 + 97);
        move += 8 - y1;
        move += (char) (x2 + 97);
        move += 8 - y2;
        return move;
    }

    public static String[] randomMoves(int x) {
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

    public static Square[][] createEmptyBoard() {
        Square[][] squares = new Square[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j] = new Square(null, i,j);
            }
        }
        return squares;
    }
}
