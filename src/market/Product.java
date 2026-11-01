package market;

public class Product {
    private final int code;
    private final String name;
    private final int priceInCents;
    private final String department;
    private final String category;
    private int quantity;

    public Product(int code, String name, int priceInCents, int quantity, String department, String category) {
        this.code = code;
        this.name = name;
        this.priceInCents = priceInCents;
        this.quantity = quantity;
        this.department = department;
        this.category = category;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getPriceInCents() {
        return priceInCents;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDepartment() {
        return department;
    }

    public String getCategory() {
        return category;
    }

    public void increaseQuantity(int amount) {
        quantity += amount;
    }

    public boolean decreaseQuantity(int amount) {
        if (amount <= 0 || amount > quantity) {
            return false;
        }
        quantity -= amount;
        return true;
    }

    @Override
    public String toString() {
        return String.format("[%d] %-22s %10s  estoque: %d", code, name, Money.format(priceInCents), quantity);
    }
}
