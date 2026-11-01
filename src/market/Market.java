package market;

public class Market {
    public static final int CATALOG_CAPACITY = 50;
    public static final int CART_CAPACITY = 20;
    public static final int MAX_STOCK_PER_PRODUCT = 100000;

    private final ProductCatalog catalog = new ProductCatalog(CATALOG_CAPACITY);
    private final StockLinkedList stock = new StockLinkedList();
    private final CategoryTree categories = new CategoryTree("Mercado");
    private final ProductGraph boughtTogether = new ProductGraph(CATALOG_CAPACITY);
    private final CustomerQueue queue = new CustomerQueue();

    public ProductCatalog catalog() {
        return catalog;
    }

    public StockLinkedList stock() {
        return stock;
    }

    public CategoryTree categories() {
        return categories;
    }

    public ProductGraph boughtTogether() {
        return boughtTogether;
    }

    public CustomerQueue queue() {
        return queue;
    }

    public Cart newCart() {
        return new Cart(catalog, CART_CAPACITY);
    }

    public void registerProduct(Product product) {
        if (catalog.isFull()) {
            throw new IllegalStateException("Catálogo cheio: limite de " + catalog.capacity() + " produtos.");
        }
        if (catalog.indexOf(product.getCode()) >= 0) {
            throw new IllegalArgumentException("Já existe um produto com o código " + product.getCode() + ".");
        }
        catalog.add(product);
        stock.append(product);
        categories.insert(product);
        boughtTogether.addVertex(product.getCode());
    }

    public void restock(int code, int amount) {
        Product product = stock.findByCode(code);
        if (product == null) {
            throw new IllegalArgumentException("Produto " + code + " não encontrado no estoque.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("A quantidade de reposição deve ser maior que zero.");
        }
        if ((long) product.getQuantity() + amount > MAX_STOCK_PER_PRODUCT) {
            throw new IllegalArgumentException("A reposição ultrapassa o limite de " + MAX_STOCK_PER_PRODUCT + " unidades.");
        }
        stock.restock(code, amount);
    }

    public SaleResult completeSale(Cart cart, int paidInCents) {
        if (cart.isEmpty()) {
            throw new IllegalStateException("Não é possível finalizar uma compra com o carrinho vazio.");
        }
        long total = cart.totalInCents();
        if (paidInCents < total) {
            throw new IllegalArgumentException("Valor pago insuficiente. Faltam " + Money.format(total - paidInCents) + ".");
        }
        for (int i = 0; i < cart.distinctItems(); i++) {
            Product product = catalog.findByCode(cart.codeAt(i));
            if (product.getQuantity() < cart.quantityAt(i)) {
                throw new IllegalStateException("Estoque de " + product.getName() + " mudou e ficou insuficiente.");
            }
        }
        for (int i = 0; i < cart.distinctItems(); i++) {
            catalog.findByCode(cart.codeAt(i)).decreaseQuantity(cart.quantityAt(i));
        }
        int relations = boughtTogether.registerPurchase(cart.codes());
        int change = (int) (paidInCents - total);
        cart.clear();
        return new SaleResult(total, paidInCents, change, relations);
    }

    public void loadSampleData() {
        registerProduct(new Product(101, "Arroz 5kg", 2899, 40, "Alimentos", "Grãos"));
        registerProduct(new Product(102, "Feijão 1kg", 849, 50, "Alimentos", "Grãos"));
        registerProduct(new Product(103, "Macarrão 500g", 499, 60, "Alimentos", "Massas"));
        registerProduct(new Product(104, "Molho de Tomate", 349, 45, "Alimentos", "Massas"));
        registerProduct(new Product(201, "Refrigerante 2L", 999, 30, "Bebidas", "Refrigerantes"));
        registerProduct(new Product(202, "Suco de Laranja 1L", 799, 25, "Bebidas", "Sucos"));
        registerProduct(new Product(203, "Café 500g", 1899, 22, "Bebidas", "Café"));
        registerProduct(new Product(301, "Leite Integral 1L", 549, 80, "Laticínios", "Leites"));
        registerProduct(new Product(302, "Queijo Mussarela", 1290, 20, "Laticínios", "Queijos"));
        registerProduct(new Product(401, "Detergente 500ml", 279, 35, "Limpeza", "Cozinha"));
        registerProduct(new Product(402, "Sabão em Pó 1kg", 1599, 4, "Limpeza", "Lavanderia"));
        registerProduct(new Product(501, "Pão de Forma", 899, 18, "Padaria", "Pães"));

        boughtTogether.addRelation(101, 102, 10);
        boughtTogether.addRelation(103, 104, 8);
        boughtTogether.addRelation(203, 301, 6);
        boughtTogether.addRelation(501, 302, 5);
        boughtTogether.addRelation(501, 301, 4);
        boughtTogether.addRelation(201, 103, 2);
        boughtTogether.addRelation(401, 402, 3);

        queue.enqueue("Ana");
        queue.enqueue("Bruno");
        queue.enqueue("Carla");
    }
}
