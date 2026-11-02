package market;

public class Market {
    public static final int CATALOG_CAPACITY = 50;
    public static final int CART_CAPACITY = 20;

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

    public void registerProduct(Product product, String department) {
        if (catalog.isFull()) {
            throw new IllegalStateException("Catálogo cheio: limite de " + catalog.capacity() + " produtos.");
        }
        if (catalog.indexOf(product.getCode()) >= 0) {
            throw new IllegalArgumentException("Já existe um produto com o código " + product.getCode() + ".");
        }
        catalog.add(product);
        stock.append(product);
        categories.insert(department, product);
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
        stock.restock(code, amount);
    }

    public int completeSale(Cart cart, double paid) {
        if (cart.isEmpty()) {
            throw new IllegalStateException("Não é possível finalizar uma compra com o carrinho vazio.");
        }
        double total = cart.total();
        if (paid < total) {
            throw new IllegalArgumentException(String.format("Valor pago insuficiente. Faltam R$ %.2f.", total - paid));
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
        cart.clear();
        return relations;
    }

    public void loadSampleData() {
        registerProduct(new Product(101, "Arroz 5kg", 28.99, 40, "Grãos"), "Alimentos");
        registerProduct(new Product(102, "Feijão 1kg", 8.49, 50, "Grãos"), "Alimentos");
        registerProduct(new Product(103, "Macarrão 500g", 4.99, 60, "Massas"), "Alimentos");
        registerProduct(new Product(104, "Molho de Tomate", 3.49, 45, "Massas"), "Alimentos");
        registerProduct(new Product(201, "Refrigerante 2L", 9.99, 30, "Refrigerantes"), "Bebidas");
        registerProduct(new Product(202, "Suco de Laranja 1L", 7.99, 25, "Sucos"), "Bebidas");
        registerProduct(new Product(203, "Café 500g", 18.99, 22, "Café"), "Bebidas");
        registerProduct(new Product(301, "Leite Integral 1L", 5.49, 80, "Leites"), "Laticínios");
        registerProduct(new Product(302, "Queijo Mussarela", 12.90, 20, "Queijos"), "Laticínios");
        registerProduct(new Product(401, "Detergente 500ml", 2.79, 35, "Cozinha"), "Limpeza");
        registerProduct(new Product(402, "Sabão em Pó 1kg", 15.99, 4, "Lavanderia"), "Limpeza");
        registerProduct(new Product(501, "Pão de Forma", 8.99, 18, "Pães"), "Padaria");

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
