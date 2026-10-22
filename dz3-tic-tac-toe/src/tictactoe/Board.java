package tictactoe;

/** Игровое поле 3x3. Хранит сетку и умеет проверять победу и заполненность. */
public class Board {
    public static final char EMPTY = ' ';
    private static final int SIZE = 3;

    private final char[][] grid = new char[SIZE][SIZE];

    public Board() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                grid[r][c] = EMPTY;
            }
        }
    }

    public int getSize() {
        return SIZE;
    }

    public boolean isInside(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    public boolean isEmpty(int row, int col) {
        return grid[row][col] == EMPTY;
    }

    public void placeMark(int row, int col, char mark) {
        if (!isInside(row, col)) {
            throw new IllegalArgumentException("Клетка вне поля: " + row + "," + col);
        }
        if (!isEmpty(row, col)) {
            throw new IllegalArgumentException("Клетка занята: " + row + "," + col);
        }
        grid[row][col] = mark;
    }

    public boolean isFull() {
        for (char[] row : grid) {
            for (char cell : row) {
                if (cell == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkWin(char mark) {
        for (int i = 0; i < SIZE; i++) {
            if (lineIs(mark, i, 0, 0, 1) || lineIs(mark, 0, i, 1, 0)) {
                return true;
            }
        }
        return lineIs(mark, 0, 0, 1, 1) || lineIs(mark, 0, SIZE - 1, 1, -1);
    }

    private boolean lineIs(char mark, int row, int col, int dRow, int dCol) {
        for (int step = 0; step < SIZE; step++) {
            if (grid[row + step * dRow][col + step * dCol] != mark) {
                return false;
            }
        }
        return true;
    }

    public void displayBoard() {
        System.out.println("    1   2   3");
        for (int r = 0; r < SIZE; r++) {
            System.out.printf("%d   %c | %c | %c%n", r + 1, grid[r][0], grid[r][1], grid[r][2]);
            if (r < SIZE - 1) {
                System.out.println("   ---+---+---");
            }
        }
    }
}
