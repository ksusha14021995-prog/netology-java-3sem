package tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Простой компьютерный игрок: выбирает случайную свободную клетку. */
public class AIPlayer extends Player {
    private final Random random;

    public AIPlayer(String name, char mark, Random random) {
        super(name, mark);
        this.random = random;
    }

    @Override
    public int[] makeMove(Board board) {
        List<int[]> free = new ArrayList<>();
        for (int r = 0; r < board.getSize(); r++) {
            for (int c = 0; c < board.getSize(); c++) {
                if (board.isEmpty(r, c)) {
                    free.add(new int[]{r, c});
                }
            }
        }
        if (free.isEmpty()) {
            throw new IllegalStateException("Нет свободных клеток");
        }
        int[] move = free.get(random.nextInt(free.size()));
        System.out.printf("%s ходит: %d %d%n", this, move[0] + 1, move[1] + 1);
        return move;
    }
}
