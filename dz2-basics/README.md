# ДЗ 2. Java для программистов. Часть 1

Студент: Волынкина Ксения, BHEMBD-25.

## Задание 1. Число-палиндром — [`PalindromeNumber.java`](src/PalindromeNumber.java)

Проверка **без превращения числа в строку**: разворачиваем арифметически только правую
половину числа (`% 10` и `/ 10`) и сравниваем с левой. Отрицательные числа и числа,
оканчивающиеся на 0 (кроме 0), сразу отсекаются. Переполнения `int` нет — разворачивается
только половина цифр. Сложность O(log₁₀ n), память O(1).

## Задание 2. Глубина бинарного дерева — [`BinaryTreeDepth.java`](src/BinaryTreeDepth.java)

- рекурсивно: `1 + max(depth(left), depth(right))`, для `null` — 0;
- итеративно обходом в ширину по уровням — без риска `StackOverflowError` на очень глубоком дереве.

## Запуск

```bash
cd dz2-basics
javac -encoding UTF-8 -d out src/*.java
java -cp out PalindromeNumber
java -cp out BinaryTreeDepth
```

Ожидаемый вывод:

```
121 -> true
-121 -> false
10 -> false
0 -> true
7 -> true
1221 -> true
12321 -> true
123 -> false
1000021 -> false
2147483647 -> false

Пустое дерево: 0 / 0
Дерево [3,9,20,null,null,15,7]: 3 / 3
Цепочка 1->2->3->4: 4 / 4
```
