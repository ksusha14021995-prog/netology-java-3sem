import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Задание 2. Максимальная глубина бинарного дерева.
 *
 * Два решения:
 *  - рекурсивное (DFS): глубина = 1 + max(глубина левого, глубина правого);
 *  - итеративное (BFS по уровням): считаем, сколько уровней обошли.
 *    Не упирается в размер стека вызовов на вырожденных (очень глубоких) деревьях.
 */
public class BinaryTreeDepth {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {}

        TreeNode(int val) { this.val = val; }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    public static int maxDepthRecursive(TreeNode root) {
        if (root == null) {
            return 0;
        }
        return 1 + Math.max(maxDepthRecursive(root.left), maxDepthRecursive(root.right));
    }

    public static int maxDepthIterative(TreeNode root) {
        if (root == null) {
            return 0;
        }
        Deque<TreeNode> level = new ArrayDeque<>();
        level.add(root);
        int depth = 0;
        while (!level.isEmpty()) {
            depth++;
            for (int i = level.size(); i > 0; i--) {
                TreeNode node = level.poll();
                if (node.left != null) level.add(node.left);
                if (node.right != null) level.add(node.right);
            }
        }
        return depth;
    }

    public static void main(String[] args) {
        //        3
        //       / \
        //      9  20
        //         / \
        //        15  7
        TreeNode tree = new TreeNode(3,
                new TreeNode(9),
                new TreeNode(20, new TreeNode(15), new TreeNode(7)));

        TreeNode chain = new TreeNode(1, null, new TreeNode(2, null, new TreeNode(3, new TreeNode(4), null)));

        System.out.println("Пустое дерево: " + maxDepthRecursive(null) + " / " + maxDepthIterative(null));
        System.out.println("Дерево [3,9,20,null,null,15,7]: " + maxDepthRecursive(tree) + " / " + maxDepthIterative(tree));
        System.out.println("Цепочка 1->2->3->4: " + maxDepthRecursive(chain) + " / " + maxDepthIterative(chain));
    }
}
