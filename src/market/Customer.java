package market;

public class Customer {
    private final int ticket;
    private final String name;

    public Customer(int ticket, String name) {
        this.ticket = ticket;
        this.name = name;
    }

    public int getTicket() {
        return ticket;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("Senha %03d - %s", ticket, name);
    }
}
