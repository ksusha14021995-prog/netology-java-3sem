package tictactoe;

/** Партия: содержит поле и двух игроков (композиция), передаёт ход и определяет исход. */
public class Game {
    private final Board board;
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;

    public Game(Player player1, Player player2) {
        this.board = new Board();
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
    }

    public void startGame() {
        System.out.printf("Крестики-нолики: %s против %s%n%n", player1, player2);
        board.displayBoard();
        while (true) {
            int[] move = currentPlayer.makeMove(board);
            board.placeMark(move[0], move[1], currentPlayer.getMark());
            System.out.println();
            board.displayBoard();
            if (checkForWinOrDraw()) {
                return;
            }
            switchPlayer();
        }
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    /** Печатает результат и возвращает true, если партия закончена. */
    public boolean checkForWinOrDraw() {
        if (board.checkWin(currentPlayer.getMark())) {
            System.out.printf("%nПобедил %s!%n", currentPlayer);
            return true;
        }
        if (board.isFull()) {
            System.out.println("\nНичья!");
            return true;
        }
        return false;
    }
}
