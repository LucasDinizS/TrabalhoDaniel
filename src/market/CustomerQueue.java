package market;

public class CustomerQueue {
    private static class Node {
        private final Customer customer;
        private Node next;

        private Node(Customer customer) {
            this.customer = customer;
        }
    }

    private Node front;
    private Node rear;
    private int size;
    private int nextTicket = 1;

    public Customer enqueue(String name) {
        Customer customer = new Customer(nextTicket++, name);
        Node node = new Node(customer);
        if (rear == null) {
            front = node;
        } else {
            rear.next = node;
        }
        rear = node;
        size++;
        return customer;
    }

    public Customer dequeue() {
        if (isEmpty()) {
            return null;
        }
        Customer customer = front.customer;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return customer;
    }

    public Customer peek() {
        return isEmpty() ? null : front.customer;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }

    public void print() {
        if (isEmpty()) {
            System.out.println("Nenhum cliente aguardando.");
            return;
        }
        Node current = front;
        int position = 1;
        while (current != null) {
            System.out.printf("%2dº %s%n", position, current.customer);
            current = current.next;
            position++;
        }
    }
}
