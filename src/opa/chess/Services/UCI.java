package opa.chess.Services;

import opa.chess.Models.Board;
import opa.chess.Config.CommonMethods;

import java.util.Scanner;
import javax.swing.Timer;

public class UCI {

    private static final String NAME_VERSION = "OPA-Chess AlphaBeta v0.01";
    private static final String AUTHOR = "OPA";
    private static AI ai;
    private static Board board;
    private static Thread thread;
    private static Timer timer;

    public UCI() {
        timer = new Timer(29000, arg0 -> CommonMethods.stop = true);
        timer.setRepeats(false);
        CommonMethods.player = 1;
        CommonMethods.en_passant = false;
        CommonMethods.castling = false;
        CommonMethods.white_king_checked = false;
        CommonMethods.black_king_checked = false;
        ai = new AI();
        board = new Board();
        init();
        API();
    }

    public static void API() {
        Scanner input = new Scanner(System.in);
        while (true) {
            String inputline = input.nextLine();
            if (inputline.equals("isready")) {
                isReady(); //done
            } else if (inputline.equals("ucinewgame")) {
                uciNewGame(); //TODO
            } else if (inputline.startsWith("position")) {
                position(inputline);
            } else if (inputline.startsWith("go")) {
                go(); //done
            } else if (inputline.equals("print")) {
                print();
            } else if (inputline.equals("stop")) {
                stop();
            } else if (inputline.equals("quit")) {
                System.exit(0);
                break;
            }
        }

    }

    private static void init() {
        System.out.println("id name " + NAME_VERSION);
        System.out.println("id Author " + AUTHOR);
        //options available
        System.out.println("uciok");
    }

    private static void isReady() {
        System.out.println("readyok"); //To change body of generated methods, choose Tools | Templates.
    }

    private static void uciNewGame() {
        //clear hash + other stuff if needed
        System.out.println("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void go() {//this will generate the next nove and print it
        thread = new Thread(() -> {
            CommonMethods.stop = false;
            timer.start();
            String move = ai.alphaBeta(CommonMethods.player, board, Integer.MIN_VALUE, Integer.MAX_VALUE, CommonMethods.depth);
            timer.stop();
            CommonMethods.stop = false;
            System.out.println("bestmove " + move);
        });
        thread.start();
    }

    private static void stop() {
        thread.interrupt();
    }

    private static void print() {
        board.print();
    }

    private static void position(String Input) {
        Input = Input.replace("position ", "");
        if (Input.startsWith("startpos")) {
            if (Input.contains("startpos ")) {
                Input = Input.replace("startpos ", "");
                CommonMethods.player = 1;
                CommonMethods.en_passant = false;
                CommonMethods.castling = false;
                CommonMethods.white_king_checked = false;
                CommonMethods.black_king_checked = false;
                board = new Board();
            } else {
                Input = Input.replace("startpos", "");
                CommonMethods.player = 1;
                CommonMethods.en_passant = false;
                CommonMethods.castling = false;
                CommonMethods.white_king_checked = false;
                CommonMethods.black_king_checked = false;
                board = new Board();
            }
        } else if (Input.startsWith("fen")) {
            Input = Input.replace("fen ", "");
        }

        if (Input.startsWith("moves")) {
            Input = Input.replace("moves ", "");
            String[] movess = Input.split(" ");
            for (String w : movess) {
                if (!board.applyMove(w)) {
                    break;
                }
            }
        }
    }

}