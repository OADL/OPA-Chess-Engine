package opa.chess;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import javax.swing.Timer;

public class Game {

    private static final AI ai = new AI();
    private static Board b;
    private static Thread t;
    private static Thread t2;
    private static ArrayList<String> moves = new ArrayList<String>();
    private static boolean Quit = false;
    private static boolean AIrun = false;
    private static Timer timer;

    public Game() {
        timer = new Timer(29000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                CommonMethods.stop = true;
            }
        });
        timer.setRepeats(false);
        Start();
    }

    public static void Start() {
        Scanner input = new Scanner(System.in);
        while (true) {
            CommonMethods.player = 1;
            CommonMethods.en_passant = false;
            CommonMethods.castling = false;
            CommonMethods.white_king_checked = false;
            CommonMethods.black_king_checked = false;

            System.out.println("============================");
            System.out.println("|   Welcome to OPA Chess   |");
            System.out.println("============================");
            System.out.println("| Options:                 |");
            System.out.println("|        > UCI             |");
            System.out.println("|        > Console         |");
            System.out.println("|        > Exit            |");
            System.out.println("============================");
            System.out.print("> ");
            String inputline = input.nextLine();
            if (inputline.equalsIgnoreCase("uci")) {
                UCIoption();
            } else if (inputline.equalsIgnoreCase("console")) {
                CONSOLEoption();
            } else if (inputline.equalsIgnoreCase("exit")) {
                System.out.println("============================");
                System.out.println("|Thanks for using OPA Chess|");
                System.out.println("============================");
                System.exit(0);
                break;
            }
        }

    }

    private static void UCIoption() {
        UCI uci = new UCI();
    }

    private static void CONSOLEoption() {
        CommonMethods.player = 1;
        CommonMethods.en_passant = false;
        CommonMethods.castling = false;
        CommonMethods.white_king_checked = false;
        CommonMethods.black_king_checked = false;
        b = new Board();
        System.out.println("============================");
        System.out.println("|       Console Mode       |");
        System.out.println("============================");
        System.out.println("| Options:                 |");
        System.out.println("|        1. Human vs AI    |");
        System.out.println("|        2. Human vs Human |");
        System.out.println("|        3. AI vs AI       |");
        System.out.println("============================");
        System.out.print("> ");
        Scanner input = new Scanner(System.in);
        String options = input.nextLine();
        if (options.contains("1")) {
            HvsAI();
        } else if (options.contains("2")) {
            HvsH();
        } else if (options.contains("3")) {
            AIvsAI();
        } else {
            System.err.println("Sorry wrong option.");
            System.err.println("============================");
        }
    }

    private static void HvsAI() {
        moves = new ArrayList<String>();
        System.out.println("============================");
        System.out.println("| Load Game?               |");
        System.out.println("|        > y/yes           |");
        System.out.println("|        > n/no            |");
        System.out.println("============================");
        System.out.print("> ");
        Scanner input = new Scanner(System.in);
        String i = input.nextLine();
        if (i.equalsIgnoreCase("yes") || i.equalsIgnoreCase("y")) {
            LoadGame();
        } else {
            System.out.println("============================");
            System.out.println("| Color:                   |");
            System.out.println("|        1. White          |");
            System.out.println("|        2. Black          |");
            System.out.println("============================");
            System.out.print("> ");
            String i2 = input.nextLine();
            System.out.println("********* Game Started **********\n");
            try {
                b.print_board();
            } catch (UnsupportedEncodingException ex) {
            }
            System.out.println("=================================");
            if (i2.equalsIgnoreCase("2")) {
                AIturn(0);
                CommonMethods.stop = false;
            }
        }
        while (true) {
            if (Draw()) {
                System.out.println("========== Game Ended ==========");
                break;
            }
            if (HUturn(0)) {
                AIturn(0);
                CommonMethods.stop = false;
            } else {
                break;
            }
        }
    }

    private static void HvsH() {
        moves = new ArrayList<String>();
        System.out.println("============================");
        System.out.println("| Load Game?               |");
        System.out.println("|        > y/yes           |");
        System.out.println("|        > n/no            |");
        System.out.println("============================");
        System.out.print("> ");
        Scanner input = new Scanner(System.in);
        String i = input.nextLine();
        if (i.equalsIgnoreCase("yes") || i.equalsIgnoreCase("y")) {
            LoadGame();
        } else {
            System.out.println("============================");
            System.out.println("| Players:                  |");
            System.out.println("|     1 => White (UP)      |");
            System.out.println("|     2 => Black (DOWN)    |");
            System.out.println("============================");
            System.out.println("********* Game Started **********\n");
            try {
                b.print_board();
            } catch (UnsupportedEncodingException ex) {
            }
            System.out.println("=================================");
        }
        while (true) {
            if (Draw()) {
                System.out.println("========== Game Ended ==========");
                break;
            }
            if (!HUturn(CommonMethods.player)) {
                break;
            }
        }
    }

    private static void AIvsAI() {
        System.out.println("============================");
        System.out.println("| AIs:                     |");
        System.out.println("|     1 => White (UP)      |");
        System.out.println("|     2 => Black (DOWN)    |");
        System.out.println("============================");
        System.out.println("********* Game Started **********\n");
        try {
            b.print_board();
        } catch (UnsupportedEncodingException ex) {
        }
        System.out.println("=================================");
        t = new Thread() {
            @Override
            public void run() {
                Scanner sc = new Scanner(System.in);
                while (true) {
                    if (sc.hasNext()) {
                        String Input = sc.nextLine();
                        if (Input.equalsIgnoreCase("stop")) {
                            CommonMethods.stop = true;
                        } else if (Input.equalsIgnoreCase("end")) {
                            CommonMethods.stop = true;
                            Quit = true;
                            break;
                        }
                    }
                }
            }
        };
        t.start();
        while (true) {
            if (Draw()) {
                System.out.println("========== Game Ended ==========");
                break;
            }
            AIturn(CommonMethods.player);
            CommonMethods.stop = false;
            if (Quit) {
                Quit = false;
                System.out.println("========== Game Ended ==========");
                t.suspend();
                break;
            }
        }
    }

    private static void LoadGame() {
        System.out.println("Please enter File name : ");
        System.out.print("> ");
        Scanner input = new Scanner(System.in);
        String LoadedFilename = input.nextLine();
        String Loadedfile;
        Scanner in;
        try {
            in = new Scanner(new FileReader(LoadedFilename + ".txt"));
            Loadedfile = in.nextLine();
            in.close();
        } catch (FileNotFoundException ex) {
            Loadedfile = null;
            System.err.println("Error Reading File");
        }
        System.out.println("Game Loaded!");
        String[] Loadedgamemoves = Loadedfile.split(" ");
        CommonMethods.player = 1;
        CommonMethods.en_passant = false;
        CommonMethods.castling = false;
        CommonMethods.white_king_checked = false;
        CommonMethods.black_king_checked = false;
        b = new Board();
        System.out.println("========== Game Started =========");
        for (String w1 : Loadedgamemoves) {
            System.out.println("============== " + w1 + " ============");
            if (!b.apply_move(w1)) {
                System.out.println("Invalid Move: " + w1);
                break;
            }
            moves.add(w1);
            try {
                b.print_board();
            } catch (UnsupportedEncodingException ex) {
            }
            System.out.println("=================================");
        }
    }

    private static void AIturn(int x) {
        if (x == 0) {
            System.out.print("\nAI Turn: \n> Thinking .. \n");
        } else {
            System.out.print("\nAI " + x + " Turn: \n> Thinking ..\n");
        }
        CommonMethods.stop = false;
        timer.start();
        String ai_move = ai.alphabeta(CommonMethods.player, b, Integer.MIN_VALUE, Integer.MAX_VALUE, CommonMethods.depth);
        timer.stop();
        if (ai_move != null) {
            System.out.println("> AI Played: " + ai_move);
            if (!b.apply_move(ai_move)) {
                System.out.println("Invalid AI Move: " + ai_move);
                return;
            }
            moves.add(ai_move);
            try {
                b.print_board();
            } catch (UnsupportedEncodingException ex) {
            }
            System.out.println("===================================");
        } else {
            System.out.println("AI ERROR!");
            return;
        }
    }

    private static boolean HUturn(int x) {
        Scanner input = new Scanner(System.in);
        while (true) {
            if (x == 0) {
                System.out.print("\nYour Turn: (<move>\\save\\end)\n> ");
            } else {
                System.out.print("\nPlayer " + x + " Turn: (<move>\\save\\end)\n> ");
            }
            String w = input.nextLine();
            if (w.equalsIgnoreCase("end")) {
                System.out.println("========== Game Ended ==========");
                return false;
            } else if (w.equalsIgnoreCase("save")) {
                try {
                    System.out.println("Please enter File name : ");
                    System.out.print("> ");
                    String SaveFilename = input.nextLine();
                    PrintWriter writer = new PrintWriter(SaveFilename + ".txt", "UTF-8");
                    for (int j = 0; j < moves.size(); j++) {
                        writer.print(moves.get(j) + " ");
                    }
                    writer.close();
                    System.out.println("Game Saved!");
                } catch (IOException e) {
                    System.out.println("Error Saving File!!");
                }
            } else {
                if (!b.apply_move(w)) {
                    System.out.println("Invalid Move: " + w);
                    continue;
                }
                moves.add(w);
                try {
                    b.print_board();
                } catch (UnsupportedEncodingException ex) {
                }
                System.out.println("==================================");
                return true;
            }
        }
    }

    private static boolean Draw() {
        ArrayList<String> AvailableMoves = b.next_moves(CommonMethods.player);
        if (AvailableMoves.isEmpty()) {
            if (CommonMethods.player == 1 && CommonMethods.white_king_checked) {
                System.out.println("   ==||Checkmate Black Won||==");
            } else if (CommonMethods.player == 1 && !CommonMethods.white_king_checked) {
                System.out.println("    ====== Stalemate =====");
            } else if (CommonMethods.player == 2 && CommonMethods.black_king_checked) {
                System.out.println("   ==||Checkmate White Won||==");
            } else if (CommonMethods.player == 2 && !CommonMethods.black_king_checked) {
                System.out.println("    ====== Stalemate =====");
            } else {
                System.err.println("      ====== ERROR =====");
            }
            return true;
        }
        return false;
    }
}
