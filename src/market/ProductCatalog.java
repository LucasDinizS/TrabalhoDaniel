package market;

public class ProductCatalog {
    private final int[] codes;
    private final int[] prices;
    private final Product[] products;
    private int size;

    public ProductCatalog(int capacity) {
        codes = new int[capacity];
        prices = new int[capacity];
        products = new Product[capacity];
        size = 0;
    }

    public boolean isFull() {
        return size == codes.length;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return codes.length;
    }

    public boolean add(Product product) {
        if (isFull() || indexOf(product.getCode()) >= 0) {
            return false;
        }
        int position = size;
        while (position > 0 && codes[position - 1] > product.getCode()) {
            codes[position] = codes[position - 1];
            prices[position] = prices[position - 1];
            products[position] = products[position - 1];
            position--;
        }
        codes[position] = product.getCode();
        prices[position] = product.getPriceInCents();
        products[position] = product;
        size++;
        return true;
    }

    public int indexOf(int code) {
        int low = 0;
        int high = size - 1;
        while (low <= high) {
            int middle = (low + high) / 2;
            if (codes[middle] == code) {
                return middle;
            }
            if (codes[middle] < code) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }
        return -1;
    }

    public Product findByCode(int code) {
        int index = indexOf(code);
        return index >= 0 ? products[index] : null;
    }

    public int priceOf(int code) {
        int index = indexOf(code);
        return index >= 0 ? prices[index] : -1;
    }

    public void printPriceTable() {
        if (size == 0) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        System.out.println("Posição | Código | Preço");
        for (int i = 0; i < size; i++) {
            System.out.printf("%7d | %6d | %s%n", i, codes[i], Money.format(prices[i]));
        }
        System.out.printf("Ocupação do array: %d/%d%n", size, codes.length);
    }
}
