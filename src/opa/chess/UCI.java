package opa.chess;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import java.io.UnsupportedEncodingException;
import javax.swing.Timer;

public class UCI {

    private static String NAMEVERSION = "OPA-Chess AlphaBeta v0.01";
    private static String AUTHOR = "OPA";
    private static AI ai;
    private static Board b;
    private static Thread t;
    private static Timer timer;

    public UCI() {
        timer = new Timer(29000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                CommonMethods.stop = true;
            }
        });
        timer.setRepeats(false);
        CommonMethods.player = 1;
        CommonMethods.en_passant = false;
        CommonMethods.castling = false;
        CommonMethods.white_king_checked = false;
        CommonMethods.black_king_checked = false;
        ai = new AI();
        b = new Board();
        UCIoption();
        API();
    }

    public static void API() {
        Scanner input = new Scanner(System.in);
        while (true) {
            String inputline = input.nextLine();
            if (inputline.equals("isready")) {
                ISREADYoption(); //done
            } else if (inputline.equals("ucinewgame")) {
                UCINEWGAMEoption(); //done
            } else if (inputline.startsWith("position")) {
                POSITIONoption(inputline);
            } else if (inputline.startsWith("go")) {
                GOoption(); //done
            } else if (inputline.equals("print")) {
                PRINToption();
            } else if (inputline.equals("stop")) {
                STOPoption();
            } else if (inputline.equals("quit")) {
                System.exit(0);
                break;
            }
        }

    }

    private static void UCIoption() {
        System.out.println("id name " + NAMEVERSION);
        System.out.println("id Author " + AUTHOR);
        //options available
        System.out.println("uciok");
    }

    private static void ISREADYoption() {
        System.out.println("readyok"); //To change body of generated methods, choose Tools | Templates.
    }

    private static void UCINEWGAMEoption() {
        //clear hash + other stuff if needed
        System.out.println("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void GOoption() {//this will generate the next nove and print it
        t = new Thread() {
            public void run() {
                CommonMethods.stop = false;
                timer.start();
                String move = ai.alphabeta(CommonMethods.player, b, Integer.MIN_VALUE, Integer.MAX_VALUE, CommonMethods.depth);
                timer.stop();
                CommonMethods.stop = false;
                System.out.println("bestmove " + move);
            }
        };
        t.start();
    }

    private static void STOPoption() {
        t.interrupt();
    }

    private static void PRINToption() {
        try {
            b.print_board();
        } catch (UnsupportedEncodingException ex) {
        }
    }

    private static void POSITIONoption(String Input) {

        Input = Input.replace("position ", "");
        if (Input.startsWith("startpos")) {
            if (Input.contains("startpos ")) {

                Input = Input.replace("startpos ", "");
                CommonMethods.player = 1;
                CommonMethods.en_passant = false;
                CommonMethods.castling = false;
                CommonMethods.white_king_checked = false;
                CommonMethods.black_king_checked = false;
                b = new Board();

            } else {
                Input = Input.replace("startpos", "");
                CommonMethods.player = 1;
                CommonMethods.en_passant = false;
                CommonMethods.castling = false;
                CommonMethods.white_king_checked = false;
                CommonMethods.black_king_checked = false;
                b = new Board();

            }
        } else if (Input.startsWith("fen")) {
            Input = Input.replace("fen ", "");
        }

        if (Input.startsWith("moves")) {
            Input = Input.replace("moves ", "");
            String[] movess = Input.split(" ");
            for (String w : movess) {
                if (!b.apply_move(w)) {
                    break;
                }
            }
        }
    }

}
