package market;

public record Customer(int ticket, String name) {
    @Override
    public String toString() {
        return String.format("Senha %03d - %s", ticket, name);
    }
}
