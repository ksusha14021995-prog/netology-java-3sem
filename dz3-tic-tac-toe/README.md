# ДЗ 3. Консольные «Крестики-нолики» (наследование и композиция)

Студент: Волынкина Ксения, BHEMBD-25.

## Сборка и запуск

Нужна Java 17+.

```bash
cd dz3-tic-tac-toe
javac -encoding UTF-8 -d out $(find src -name "*.java")   # Windows PowerShell: javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src\*.java)
java -cp out tictactoe.Main        # два игрока за одним компьютером
java -cp out tictactoe.Main --ai   # человек (X) против компьютера (O)
```

Ход вводится как «строка столбец» от 1 до 3, например `2 3`.

## Классы

| Класс | Роль |
|---|---|
| `Player` (abstract) | общие атрибуты `name`, `mark`; абстрактный `makeMove(Board)` |
| `HumanPlayer extends Player` | читает ход из консоли, проверяет ввод (формат, диапазон, занятость клетки) |
| `AIPlayer extends Player` | случайный допустимый ход (доп. задание) |
| `Board` | двумерный массив `grid`; `displayBoard()`, `isFull()`, `checkWin(mark)`, `placeMark(row, col, mark)` |
| `Game` | содержит `Board` и двух `Player`, `currentPlayer`; `startGame()`, `switchPlayer()`, `checkForWinOrDraw()` |
| `Main` | собирает игру из игроков по аргументам командной строки |

## Проектные решения

- **Наследование.** `Player` — абстрактный: «просто игрок» не умеет ходить, ход определяет
  конкретный тип. `Game` работает только с `Player`, поэтому замена человека на компьютер
  не требует правок в логике партии.
- **Композиция.** `Game` владеет `Board` и игроками — партия состоит из поля и участников,
  а не является ими.
- `makeMove` получает `Board`, чтобы игрок мог выбрать свободную клетку; ставит метку
  только `Game` через `placeMark`, а `placeMark` дополнительно защищён от занятых/чужих клеток.
- Проверка победы — один метод `lineIs` для строк, столбцов и обеих диагоналей (через шаг `dRow/dCol`).
- `Scanner` создаётся один раз в `Main` и передаётся в `HumanPlayer` — на одном `System.in`
  не должно быть двух сканеров.
