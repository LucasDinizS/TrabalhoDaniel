package market;

public class StockLinkedList {
    private static class Node {
        private final Product product;
        private Node next;

        private Node(Product product) {
            this.product = product;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public void append(Product product) {
        Node node = new Node(product);
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
        size++;
    }

    public Product findByCode(int code) {
        Node current = head;
        while (current != null) {
            if (current.product.getCode() == code) {
                return current.product;
            }
            current = current.next;
        }
        return null;
    }

    public boolean restock(int code, int amount) {
        Product product = findByCode(code);
        if (product == null || amount <= 0) {
            return false;
        }
        product.increaseQuantity(amount);
        return true;
    }

    public int size() {
        return size;
    }

    public long totalUnits() {
        long total = 0;
        Node current = head;
        while (current != null) {
            total += current.product.getQuantity();
            current = current.next;
        }
        return total;
    }

    public double totalValue() {
        double total = 0;
        Node current = head;
        while (current != null) {
            total += current.product.getQuantity() * current.product.getPrice();
            current = current.next;
        }
        return total;
    }

    public void printLowStock(int threshold) {
        boolean found = false;
        Node current = head;
        while (current != null) {
            if (current.product.getQuantity() <= threshold) {
                System.out.println("  " + current.product);
                found = true;
            }
            current = current.next;
        }
        if (!found) {
            System.out.println("  Nenhum produto com estoque baixo.");
        }
    }

    public void print() {
        if (head == null) {
            System.out.println("Estoque vazio.");
            return;
        }
        Node current = head;
        int position = 1;
        while (current != null) {
            System.out.printf("%2d. %s%n", position, current.product);
            current = current.next;
            position++;
        }
        System.out.printf("Produtos: %d | Unidades: %d | Valor total: R$ %.2f%n",
                size, totalUnits(), totalValue());
    }
}
