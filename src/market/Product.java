package market;

public class Product {
    private final int code;
    private final String name;
    private final double price;
    private final String category;
    private int quantity;

    public Product(int code, String name, double price, int quantity, String category) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
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
        return String.format("[%d] %-22s R$ %6.2f  estoque: %d", code, name, price, quantity);
    }
}
