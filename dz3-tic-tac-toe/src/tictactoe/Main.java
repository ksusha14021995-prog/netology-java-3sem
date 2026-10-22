package tictactoe;

import java.util.Random;
import java.util.Scanner;

/**
 * Точка входа.
 *   java -cp out tictactoe.Main        — два игрока-человека
 *   java -cp out tictactoe.Main --ai   — человек (X) против компьютера (O)
 */
public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean vsComputer = args.length > 0 && args[0].equals("--ai");

        Player first = new HumanPlayer("Игрок 1", 'X', input);
        Player second = vsComputer
                ? new AIPlayer("Компьютер", 'O', new Random())
                : new HumanPlayer("Игрок 2", 'O', input);

        new Game(first, second).startGame();
    }
}
