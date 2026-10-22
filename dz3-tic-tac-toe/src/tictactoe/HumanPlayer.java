package tictactoe;

import java.util.Scanner;

/** Игрок-человек: читает ход из консоли и переспрашивает, пока ввод не станет допустимым. */
public class HumanPlayer extends Player {
    private final Scanner input;

    public HumanPlayer(String name, char mark, Scanner input) {
        super(name, mark);
        this.input = input;
    }

    @Override
    public int[] makeMove(Board board) {
        int size = board.getSize();
        while (true) {
            System.out.printf("%s, ваш ход — строка и столбец от 1 до %d через пробел: ", this, size);
            if (!input.hasNextLine()) {
                throw new IllegalStateException("Ввод закончился до конца игры");
            }
            String[] parts = input.nextLine().trim().split("\\s+");
            if (parts.length != 2) {
                System.out.println("Нужно два числа, например: 2 3");
                continue;
            }
            int row;
            int col;
            try {
                row = Integer.parseInt(parts[0]) - 1;
                col = Integer.parseInt(parts[1]) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Это не числа, попробуйте ещё раз.");
                continue;
            }
            if (!board.isInside(row, col)) {
                System.out.printf("Координаты должны быть от 1 до %d.%n", size);
                continue;
            }
            if (!board.isEmpty(row, col)) {
                System.out.println("Клетка уже занята.");
                continue;
            }
            return new int[]{row, col};
        }
    }
}
