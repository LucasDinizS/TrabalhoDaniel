package market;

public class Cashier {
    private final Market market;

    public Cashier(Market market) {
        this.market = market;
    }

    public void attendCustomers() {
        boolean keepAttending = true;
        while (keepAttending) {
            Customer customer = market.queue().dequeue();
            if (customer == null) {
                System.out.println("Não há clientes na fila.");
                return;
            }
            System.out.println("\n>>> Chamando: " + customer);
            attend(customer);
            Customer next = market.queue().peek();
            if (next == null) {
                System.out.println("Fila vazia. Caixa livre.");
                return;
            }
            System.out.println("Próximo da fila: " + next);
            keepAttending = Main.readYes("Chamar o próximo cliente?");
        }
    }

    private void attend(Customer customer) {
        Cart cart = market.newCart();
        while (true) {
            System.out.printf("%n--- Caixa | %s | Itens: %d | Total: R$ %.2f ---%n",
                    customer, cart.distinctItems(), cart.total());
            System.out.println("1. Adicionar produto");
            System.out.println("2. Remover produto");
            System.out.println("3. Desfazer última operação");
            System.out.println("4. Ver carrinho");
            System.out.println("5. Sugestões de produtos comprados juntos");
            System.out.println("6. Finalizar compra");
            System.out.println("0. Cancelar atendimento");
            int option = Main.readInt("Opção: ");
            try {
                switch (option) {
                    case 1 -> addToCart(cart);
                    case 2 -> removeFromCart(cart);
                    case 3 -> cart.undo();
                    case 4 -> cart.print();
                    case 5 -> printSuggestions(cart);
                    case 6 -> {
                        if (checkout(customer, cart)) {
                            return;
                        }
                    }
                    case 0 -> {
                        if (Main.readYes("Cancelar a compra de " + customer.getName() + "?")) {
                            System.out.println("Atendimento cancelado. O estoque não foi alterado.");
                            return;
                        }
                    }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Erro: " + exception.getMessage());
            }
        }
    }

    private void addToCart(Cart cart) {
        int code = Main.readInt("Código do produto: ");
        Product product = market.catalog().findByCode(code);
        if (product == null) {
            System.out.println("Produto não encontrado.");
            return;
        }
        int available = product.getQuantity() - cart.quantityOf(code);
        if (available <= 0) {
            System.out.println("Sem estoque disponível para " + product.getName() + ".");
            return;
        }
        int quantity = Main.readInt("Quantidade (disponível: " + available + "): ");
        cart.add(code, quantity);
        System.out.printf("Adicionado: %d x %s%n", quantity, product.getName());
        int[] suggestion = market.boughtTogether().recommendationsFor(code, 1);
        if (suggestion.length > 0 && cart.quantityOf(suggestion[0]) == 0) {
            System.out.println("Dica: quem leva " + product.getName() + " costuma levar "
                    + market.catalog().findByCode(suggestion[0]).getName() + ".");
        }
    }

    private void removeFromCart(Cart cart) {
        if (cart.isEmpty()) {
            System.out.println("Carrinho vazio.");
            return;
        }
        cart.print();
        int code = Main.readInt("Código do produto a remover: ");
        int inCart = cart.quantityOf(code);
        if (inCart == 0) {
            System.out.println("Esse produto não está no carrinho.");
            return;
        }
        int quantity = Main.readInt("Quantidade a remover (no carrinho: " + inCart + "): ");
        cart.remove(code, quantity);
        System.out.println("Removido do carrinho.");
    }

    private void printSuggestions(Cart cart) {
        if (cart.isEmpty()) {
            System.out.println("Adicione produtos para receber sugestões.");
            return;
        }
        boolean found = false;
        for (int i = 0; i < cart.distinctItems(); i++) {
            int code = cart.codeAt(i);
            int[] related = market.boughtTogether().recommendationsFor(code, 3);
            for (int relatedCode : related) {
                if (cart.quantityOf(relatedCode) == 0) {
                    System.out.printf("  %s -> %s (comprados juntos %d vez(es))%n",
                            market.catalog().findByCode(code).getName(),
                            market.catalog().findByCode(relatedCode).getName(),
                            market.boughtTogether().weightBetween(code, relatedCode));
                    found = true;
                }
            }
        }
        if (!found) {
            System.out.println("Nenhuma sugestão nova para este carrinho.");
        }
    }

    private boolean checkout(Customer customer, Cart cart) {
        if (cart.isEmpty()) {
            System.out.println("Carrinho vazio. Adicione produtos antes de finalizar.");
            return false;
        }
        cart.print();
        double total = cart.total();
        double paid = Main.readDouble("Valor recebido em dinheiro: R$ ");
        int relations = market.completeSale(cart, paid);
        printReceipt(customer, total, paid, relations);
        return true;
    }

    public void runSimulation() {
        System.out.println("\n===== SIMULAÇÃO AUTOMÁTICA DE COMPRA =====");
        if (market.queue().isEmpty()) {
            market.queue().enqueue("Cliente Simulação");
        }
        System.out.println("Fila antes do atendimento:");
        market.queue().print();
        Customer customer = market.queue().dequeue();
        System.out.println("\n>>> Chamando: " + customer);
        Cart cart = market.newCart();
        try {
            simulateAdd(cart, 101, 1);
            simulateAdd(cart, 102, 2);
            simulateAdd(cart, 301, 3);
            simulateAdd(cart, 401, 2);
            System.out.println("\nCliente desistiu do último item. Desfazendo (pilha)...");
            cart.undo();
            System.out.println();
            cart.print();
            double total = cart.total();
            double paid = ((int) (total / 50) + 1) * 50;
            System.out.printf("%nCliente paga com R$ %.2f%n", paid);
            int relations = market.completeSale(cart, paid);
            printReceipt(customer, total, paid, relations);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            System.out.println("Simulação interrompida: " + exception.getMessage());
            return;
        }
        System.out.println("\nEstoque atualizado dos itens vendidos:");
        System.out.println("  " + market.catalog().findByCode(101));
        System.out.println("  " + market.catalog().findByCode(102));
        System.out.println("  " + market.catalog().findByCode(301));
        Customer next = market.queue().peek();
        System.out.println(next == null ? "\nFila vazia. Caixa livre." : "\nPróximo cliente chamado: " + next);
    }

    private void simulateAdd(Cart cart, int code, int quantity) {
        cart.add(code, quantity);
        System.out.printf("+ %d x %s%n", quantity, market.catalog().findByCode(code).getName());
    }

    private void printReceipt(Customer customer, double total, double paid, int relations) {
        System.out.println("\n========== CUPOM ==========");
        System.out.println("Cliente: " + customer);
        System.out.printf("Total:   R$ %.2f%n", total);
        System.out.printf("Pago:    R$ %.2f%n", paid);
        System.out.printf("Troco:   R$ %.2f%n", paid - total);
        System.out.println("Relações registradas no grafo: " + relations);
        System.out.println("===========================");
    }
}
