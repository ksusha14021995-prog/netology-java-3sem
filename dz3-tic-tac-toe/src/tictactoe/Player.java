package tictactoe;

/** Базовый класс игрока: общее для человека и компьютера — имя, метка и обязанность сделать ход. */
public abstract class Player {
    private final String name;
    private final char mark;

    protected Player(String name, char mark) {
        this.name = name;
        this.mark = mark;
    }

    public String getName() {
        return name;
    }

    public char getMark() {
        return mark;
    }

    /** Возвращает ход в виде {row, col} (0-based). Доска передаётся, чтобы игрок мог выбрать свободную клетку. */
    public abstract int[] makeMove(Board board);

    @Override
    public String toString() {
        return name + " (" + mark + ")";
    }
}
