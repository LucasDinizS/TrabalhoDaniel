package market;

public class OperationStack {
    private static class Node {
        private final boolean addition;
        private final int code;
        private final int quantity;
        private final Node below;

        private Node(boolean addition, int code, int quantity, Node below) {
            this.addition = addition;
            this.code = code;
            this.quantity = quantity;
            this.below = below;
        }
    }

    private Node top;

    public void push(boolean addition, int code, int quantity) {
        top = new Node(addition, code, quantity, top);
    }

    public void pop() {
        if (top != null) {
            top = top.below;
        }
    }

    public boolean topIsAddition() {
        return top.addition;
    }

    public int topCode() {
        return top.code;
    }

    public int topQuantity() {
        return top.quantity;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public void clear() {
        top = null;
    }
}
