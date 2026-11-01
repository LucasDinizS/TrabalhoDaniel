package market;

public class OperationStack {
    private static class Node {
        private final CartOperation operation;
        private final Node below;

        private Node(CartOperation operation, Node below) {
            this.operation = operation;
            this.below = below;
        }
    }

    private Node top;
    private int size;

    public void push(CartOperation operation) {
        top = new Node(operation, top);
        size++;
    }

    public CartOperation pop() {
        if (isEmpty()) {
            return null;
        }
        CartOperation operation = top.operation;
        top = top.below;
        size--;
        return operation;
    }

    public CartOperation peek() {
        return isEmpty() ? null : top.operation;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        top = null;
        size = 0;
    }
}
