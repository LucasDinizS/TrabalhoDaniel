package market;

public class Cart {
    private final int[] codes;
    private final int[] quantities;
    private final OperationStack history = new OperationStack();
    private final ProductCatalog catalog;
    private int size;

    public Cart(ProductCatalog catalog, int capacity) {
        this.catalog = catalog;
        this.codes = new int[capacity];
        this.quantities = new int[capacity];
    }

    public void add(int code, int quantity) {
        validateAddition(code, quantity);
        insert(code, quantity);
        history.push(true, code, quantity);
    }

    public void remove(int code, int quantity) {
        validateRemoval(code, quantity);
        subtract(code, quantity);
        history.push(false, code, quantity);
    }

    public void undo() {
        if (history.isEmpty()) {
            throw new IllegalStateException("Não há operações para desfazer.");
        }
        boolean wasAddition = history.topIsAddition();
        int code = history.topCode();
        int quantity = history.topQuantity();
        history.pop();
        if (wasAddition) {
            subtract(code, quantity);
        } else {
            insert(code, quantity);
        }
        System.out.printf("Desfeita a %s de %d x %s.%n",
                wasAddition ? "adição" : "remoção", quantity, catalog.findByCode(code).getName());
    }

    public int quantityOf(int code) {
        int index = indexOf(code);
        return index >= 0 ? quantities[index] : 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int distinctItems() {
        return size;
    }

    public int codeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Posição inválida no carrinho: " + index);
        }
        return codes[index];
    }

    public int quantityAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Posição inválida no carrinho: " + index);
        }
        return quantities[index];
    }

    public int[] codes() {
        int[] copy = new int[size];
        for (int i = 0; i < size; i++) {
            copy[i] = codes[i];
        }
        return copy;
    }

    public double total() {
        double total = 0;
        for (int i = 0; i < size; i++) {
            total += catalog.priceOf(codes[i]) * quantities[i];
        }
        return Math.round(total * 100) / 100.0;
    }

    public void clear() {
        size = 0;
        history.clear();
    }

    public void print() {
        if (isEmpty()) {
            System.out.println("Carrinho vazio.");
            return;
        }
        System.out.println("Qtd | Produto                | Unitário   | Subtotal");
        for (int i = 0; i < size; i++) {
            Product product = catalog.findByCode(codes[i]);
            double subtotal = product.getPrice() * quantities[i];
            System.out.printf("%3d | %-22s | R$ %7.2f | R$ %.2f%n",
                    quantities[i], product.getName(), product.getPrice(), subtotal);
        }
        System.out.printf("TOTAL: R$ %.2f%n", total());
    }

    private void validateAddition(int code, int quantity) {
        Product product = catalog.findByCode(code);
        if (product == null) {
            throw new IllegalArgumentException("Produto " + code + " não cadastrado.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        int alreadyInCart = quantityOf(code);
        if (alreadyInCart + quantity > product.getQuantity()) {
            throw new IllegalArgumentException(String.format(
                    "Estoque insuficiente para %s: disponível %d, no carrinho %d.",
                    product.getName(), product.getQuantity(), alreadyInCart));
        }
        if (alreadyInCart == 0 && size == codes.length) {
            throw new IllegalArgumentException("Carrinho cheio: limite de " + codes.length + " itens diferentes.");
        }
    }

    private void validateRemoval(int code, int quantity) {
        int inCart = quantityOf(code);
        if (inCart == 0) {
            throw new IllegalArgumentException("Produto " + code + " não está no carrinho.");
        }
        if (quantity <= 0 || quantity > inCart) {
            throw new IllegalArgumentException("Quantidade inválida: o carrinho tem " + inCart + " unidade(s).");
        }
    }

    private void insert(int code, int quantity) {
        int index = indexOf(code);
        if (index >= 0) {
            quantities[index] += quantity;
            return;
        }
        codes[size] = code;
        quantities[size] = quantity;
        size++;
    }

    private void subtract(int code, int quantity) {
        int index = indexOf(code);
        quantities[index] -= quantity;
        if (quantities[index] == 0) {
            for (int i = index; i < size - 1; i++) {
                codes[i] = codes[i + 1];
                quantities[i] = quantities[i + 1];
            }
            size--;
        }
    }

    private int indexOf(int code) {
        for (int i = 0; i < size; i++) {
            if (codes[i] == code) {
                return i;
            }
        }
        return -1;
    }
}
