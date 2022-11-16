package opa.chess;

import opa.chess.Config.CommonMethods;
import opa.chess.Models.Board;
import opa.chess.Services.AI;
import opa.chess.Services.UCI;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import javax.swing.Timer;

public class Game {

    private final AI ai;
    private final Timer timer;
    private Board board;
    private ArrayList<String> moves = new ArrayList<>();
    private boolean Quit = false;

    public Game() {
        ai = new AI();
        timer = new Timer(29000, arg0 -> CommonMethods.stop = true);
        timer.setRepeats(false);
    }

    public void start() {
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

    private void UCIoption() {
        UCI uci = new UCI();
    }

    private void CONSOLEoption() {
        CommonMethods.player = 1;
        CommonMethods.en_passant = false;
        CommonMethods.castling = false;
        CommonMethods.white_king_checked = false;
        CommonMethods.black_king_checked = false;
        board = new Board();
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

    private void HvsAI() {
        moves = new ArrayList<>();
        System.out.println("============================");
        System.out.println("| Load Game?               |");
        System.out.println("|        > y/yes           |");
        System.out.println("|        > n/no            |");
        System.out.println("============================");
        System.out.print("> ");
        Scanner input = new Scanner(System.in);
        String i = input.nextLine();
        if (i.equalsIgnoreCase("yes") || i.equalsIgnoreCase("y")) {
            loadGame();
        } else {
            System.out.println("============================");
            System.out.println("| Color:                   |");
            System.out.println("|        1. White          |");
            System.out.println("|        2. Black          |");
            System.out.println("============================");
            System.out.print("> ");
            String i2 = input.nextLine();
            System.out.println("********* Game Started **********\n");
            board.print();
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

    private void HvsH() {
        moves = new ArrayList<>();
        System.out.println("============================");
        System.out.println("| Load Game?               |");
        System.out.println("|        > y/yes           |");
        System.out.println("|        > n/no            |");
        System.out.println("============================");
        System.out.print("> ");
        Scanner input = new Scanner(System.in);
        String i = input.nextLine();
        if (i.equalsIgnoreCase("yes") || i.equalsIgnoreCase("y")) {
            loadGame();
        } else {
            System.out.println("============================");
            System.out.println("| Players:                 |");
            System.out.println("|     1 => White (UP)      |");
            System.out.println("|     2 => Black (DOWN)    |");
            System.out.println("============================");
            System.out.println("********* Game Started **********\n");
            board.print();
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

    private void AIvsAI() {
        System.out.println("============================");
        System.out.println("| AIs:                     |");
        System.out.println("|     1 => White (UP)      |");
        System.out.println("|     2 => Black (DOWN)    |");
        System.out.println("============================");
        System.out.println("********* Game Started **********\n");
        board.print();
        System.out.println("=================================");
        Thread thread = new Thread(() -> {
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
        });
        thread.start();
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
                thread.interrupt();
                break;
            }
        }
    }

    private void loadGame() {
        System.out.println("Please enter File name : ");
        System.out.print("> ");
        Scanner input = new Scanner(System.in);
        String loadedFileName = input.nextLine();
        String loadedFile;
        Scanner in;
        try {
            in = new Scanner(new FileReader(loadedFileName + ".txt"));
            loadedFile = in.nextLine();
            in.close();
        } catch (FileNotFoundException ex) {
            System.err.println("Error Reading File");
            return;
        }
        System.out.println("Game Loaded!");
        String[] loadedMoves = loadedFile.split(" ");
        CommonMethods.player = 1;
        CommonMethods.en_passant = false;
        CommonMethods.castling = false;
        CommonMethods.white_king_checked = false;
        CommonMethods.black_king_checked = false;
        board = new Board();
        System.out.println("========== Game Started =========");
        for (String w1 : loadedMoves) {
            System.out.println("============== " + w1 + " ============");
            if (!board.applyMove(w1)) {
                System.out.println("Invalid Move: " + w1);
                break;
            }
            moves.add(w1);
            board.print();
            System.out.println("=================================");
        }
    }

    private void AIturn(int x) {
        long startTime = System.nanoTime();

        if (x == 0) {
            System.out.print("\nAI Turn: \n> Thinking .. \n");
        } else {
            System.out.print("\nAI " + x + " Turn: \n> Thinking ..\n");
        }
        CommonMethods.stop = false;
        timer.start();
        String ai_move = ai.alphaBeta(CommonMethods.player, board, Integer.MIN_VALUE, Integer.MAX_VALUE, CommonMethods.depth);
        timer.stop();
        if (ai_move != null) {
            System.out.println("> AI Played: " + ai_move);
            if (!board.applyMove(ai_move)) {
                System.out.println("Invalid AI Move: " + ai_move);
                return;
            }
            long stopTime = System.nanoTime();
            float elapsedTime = (stopTime-startTime)/1000000000F;
            System.out.println("> Elapsed time: "+ elapsedTime);
            moves.add(ai_move);
            board.print();
            System.out.println("===================================");
        } else {
            System.out.println("AI ERROR!");
            return;
        }
    }

    private boolean HUturn(int x) {
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
                if (!board.applyMove(w)) {
                    System.out.println("Invalid Move: " + w);
                    continue;
                }
                moves.add(w);
                board.print();
                System.out.println("==================================");
                return true;
            }
        }
    }

    private boolean Draw() {
        ArrayList<String> AvailableMoves = board.nextMoves(CommonMethods.player);
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
