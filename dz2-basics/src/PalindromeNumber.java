/**
 * Задание 1. Проверка числа на палиндром без превращения в строку.
 *
 * Идея: разворачиваем только правую половину числа. Когда развёрнутая половина
 * становится >= оставшейся левой части, мы дошли до середины. Так нет риска
 * переполнения int, и работаем за O(log10 n) без доп. памяти.
 */
public class PalindromeNumber {

    public static boolean isPalindrome(int x) {
        // Отрицательные числа ("-121" -> "121-") и числа, оканчивающиеся на 0 (кроме самого 0), — не палиндромы.
        if (x < 0 || (x % 10 == 0 && x != 0)) {
            return false;
        }
        int reversedHalf = 0;
        while (x > reversedHalf) {
            reversedHalf = reversedHalf * 10 + x % 10;
            x /= 10;
        }
        // Для нечётного количества цифр средняя цифра оказывается в reversedHalf — отбрасываем её делением на 10.
        return x == reversedHalf || x == reversedHalf / 10;
    }

    public static void main(String[] args) {
        int[] samples = {121, -121, 10, 0, 7, 1221, 12321, 123, 1000021, Integer.MAX_VALUE};
        for (int n : samples) {
            System.out.printf("%d -> %s%n", n, isPalindrome(n));
        }
    }
}
