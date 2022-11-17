package opa.chess;

import opa.chess.Config.Configurations;
import opa.chess.Enums.Color;
import opa.chess.Models.Board;
import opa.chess.Models.Move;
import opa.chess.Services.AI;
import opa.chess.Services.UCI;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class MainMenu {

    private final AI ai;
    private final Timer timer;
    private Board board;
    private ArrayList<String> moves = new ArrayList<>();
    private boolean Quit = false;

    public MainMenu() {
        ai = new AI();
        timer = new Timer(29000, arg0 -> Configurations.stop = true);
        timer.setRepeats(false);
    }

    public void start() {
        Scanner input = new Scanner(System.in);
        while (true) {
            Configurations.player = 1;
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
        new UCI();
    }

    private void CONSOLEoption() {
        Configurations.player = 1;
        board = new Board();
        System.out.println("============================");
        System.out.println("|       Console Mode       |");
        System.out.println("============================");
        System.out.println("| Options:                 |");
        System.out.println("|        1. Human vs AI    |");
        System.out.println("|        2. Human vs Human |");
        System.out.println("|        3. AI vs AI       |");
        System.out.println("|        4. Back           |");
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
        } else if (options.contains("4")) {
            return;
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
                Configurations.stop = false;
            }
        }
        while (true) {
            if (isGameFinished()) {
                System.out.println("========== Game Ended ==========");
                break;
            }
            if (Hturn(0)) {
                AIturn(0);
                Configurations.stop = false;
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
            if (isGameFinished()) {
                System.out.println("========== Game Ended ==========");
                break;
            }
            if (!Hturn(Configurations.player)) {
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
                        Configurations.stop = true;
                    } else if (Input.equalsIgnoreCase("end")) {
                        Configurations.stop = true;
                        Quit = true;
                        break;
                    }
                }
            }
        });
        thread.start();
        while (true) {
            if (isGameFinished()) {
                System.out.println("========== Game Ended ==========");
                break;
            }
            AIturn(Configurations.player);
            Configurations.stop = false;
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
        Configurations.player = 1;
        board = new Board();
        System.out.println("========== Game Started =========");
        for (String w1 : loadedMoves) {
            System.out.println("============== " + w1 + " ============");
            if (!board.handleMove(w1)) {
                System.out.println("Invalid Move: " + w1);
                break;
            }
            moves.add(w1);
            board.print();
            System.out.println("=================================");
        }
    }

    private void saveGame() {
        try {
            Scanner input = new Scanner(System.in);
            System.out.println("Please enter File name : ");
            System.out.print("> ");
            String SaveFilename = input.nextLine();
            PrintWriter writer = new PrintWriter(SaveFilename + ".txt", StandardCharsets.UTF_8);
            for (String move : moves) {
                writer.print(move + " ");
            }
            writer.close();
            System.out.println("Game Saved!");
        } catch (IOException e) {
            System.out.println("Error Saving File!!");
        }
    }

    private void AIturn(int x) {
        if (x == 0) {
            System.out.print("\nAI Turn: \n> Thinking .. \n");
        } else {
            System.out.print("\nAI " + x + " Turn: \n> Thinking ..\n");
        }
        Configurations.stop = false;
        long startTime = System.nanoTime();
        timer.start();
        Move ai_move = ai.alphaBeta(Configurations.player, board, Integer.MIN_VALUE, Integer.MAX_VALUE, Configurations.depth);
        timer.stop();
        if (ai_move != null) {
            System.out.println("> AI Played: " + ai_move);
            if (!board.handleMove(ai_move)) {
                System.out.println("Invalid AI Move: " + ai_move);
                return;
            }
            long stopTime = System.nanoTime();
            float elapsedTime = (stopTime-startTime)/1000000000F;
            System.out.println("> Elapsed time: "+ elapsedTime);
            moves.add(ai_move.toString());
            board.print();
            System.out.println("===================================");
        } else {
            System.out.println("AI ERROR!");
        }
    }

    private boolean Hturn(int x) {
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
                saveGame();
            } else {
                if (!board.handleMove(w)) {
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

    private boolean isGameFinished() {
        if (board.nextMoves(Configurations.player).isEmpty()) {
            if (Configurations.player == 1 && board.isKingChecked(Color.WHITE)) {
                System.out.println("   ==||Checkmate Black Won||==");
            } else if (Configurations.player == 1 && !board.isKingChecked(Color.WHITE)) {
                System.out.println("    ====== Stalemate =====");
            } else if (Configurations.player == 2 && board.isKingChecked(Color.BLACK)) {
                System.out.println("   ==||Checkmate White Won||==");
            } else if (Configurations.player == 2 && !board.isKingChecked(Color.BLACK)) {
                System.out.println("    ====== Stalemate =====");
            } else {
                System.err.println("      ====== ERROR =====");
            }
            return true;
        }
        return false;
    }
}
