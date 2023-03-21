package interviewquestions.graphs;

import java.util.LinkedList;

public class Main {
    public static void main(String args[]) {

        /**
         *     0
         *   1   2
         *  3 4
         */

        TreeNode root = new TreeNode(0);
        root.left = new TreeNode(1);
        root.right = new TreeNode(2);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(4);

        System.out.println("Inorder traversal");
        inorder(root);

        System.out.println("\nPreorder traversal ");
        preorder(root);

        System.out.println("\nPostorder traversal");
        postorder(root);

        System.out.println("\nLevelorder traversal");
        printLevelOrder(root);

        System.out.println("\nReversed Levelorder traversal");
        printReversedLevelOrder(root);

        System.out.println("\nPrint specified Level");
        printLevel(root, 3);

        System.out.println("\nNon recursive DFS (preorder) traversal");
        nonRecursivePreorderTraversal(root);

    }

    private static void nonRecursivePreorderTraversal(TreeNode root) {
        LinkedList<TreeNode> stack = new LinkedList<>();
        stack.add(root);
        while(!stack.isEmpty()) {
            TreeNode currentNode = stack.poll();
            if (currentNode == null) { continue; }
            System.out.print(currentNode.data+" ");
            stack.addFirst(currentNode.right);
            stack.addFirst(currentNode.left);
        }
    }

    private static void printLevel(TreeNode root, int targetLevel) {
        LinkedList<TreeNode> stack = new LinkedList<>();
        root.level = 1;
        stack.add(root);
        while (!stack.isEmpty()) {
            TreeNode currentNode = stack.poll();
            if (currentNode == null) { continue; }
            if (currentNode.level == targetLevel) {
                System.out.print(currentNode.data+" ");
            } else {
                if (currentNode.left != null) {
                    currentNode.left.level = currentNode.level+1;
                    stack.add(currentNode.left);
                }
                if (currentNode.right != null) {
                    currentNode.right.level = currentNode.level+1;
                    stack.add(currentNode.right);
                }
            }

        }
    }

    private static void printReversedLevelOrder(TreeNode root) {
        LinkedList<TreeNode> stack = new LinkedList<>();
        stack.add(root);
        while (!stack.isEmpty()) {
            TreeNode currentNode = stack.poll();
            if (currentNode==null) {continue;}
            System.out.print(currentNode.data+" ");
            stack.add(currentNode.right);
            stack.add(currentNode.left);
        }
    }

    private static void printLevelOrder(TreeNode root) {
        LinkedList<TreeNode> stack = new LinkedList<>();
        stack.add(root);
        while (!stack.isEmpty()) {
            TreeNode currentNode = stack.poll();
            if (currentNode == null) { continue; }
            System.out.print(currentNode.data+" ");
            stack.add(currentNode.left);
            stack.add(currentNode.right);
        }

    }

    private static void postorder(TreeNode root) {
        if (root == null) { return; }
        postorder(root.left);
        postorder(root.right);
        System.out.print(root.data+" ");
    }

    private static void preorder(TreeNode root) {
        if (root == null) { return; }
        System.out.print(root.data+" ");
        preorder(root.left);
        preorder(root.right);
    }

    private static void inorder(TreeNode root) {
        if (root == null) { return; }
        inorder(root.left);
        System.out.print(root.data+" ");
        inorder(root.right);
    }

    static class TreeNode {
        int data;
        public int level;
        TreeNode left, right;

        public TreeNode(int key) {
            data = key;
            left = right = null;
        }
    }
}
