package opa.chess.Services;

import opa.chess.Config.Configurations;
import opa.chess.Config.Constants;
import opa.chess.Models.Board;

import java.util.Scanner;
import javax.swing.Timer;

public class UCI {

    private static AI ai;
    private static Board board;
    private static Thread thread;
    private static Timer timer;

    public UCI() {
        timer = new Timer(29000, arg0 -> Configurations.stop = true);
        timer.setRepeats(false);
        Configurations.player = 1;
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
        System.out.println("id name " + Constants.NAME_VERSION);
        System.out.println("id Author " + Constants.AUTHOR);
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
            Configurations.stop = false;
            timer.start();
            String move = ai.alphaBeta(Configurations.player, board, Integer.MIN_VALUE, Integer.MAX_VALUE, Configurations.depth);
            timer.stop();
            Configurations.stop = false;
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
                Configurations.player = 1;
                board = new Board();
            } else {
                Input = Input.replace("startpos", "");
                Configurations.player = 1;
                board = new Board();
            }
        } else if (Input.startsWith("fen")) {
            Input = Input.replace("fen ", "");
        }

        if (Input.startsWith("moves")) {
            Input = Input.replace("moves ", "");
            String[] movess = Input.split(" ");
            for (String w : movess) {
                if (!board.handleMove(w)) {
                    break;
                }
            }
        }
    }

}
