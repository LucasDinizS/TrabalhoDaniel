package market;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private static Market market;
    private static InputReader input;
    private static Cashier cashier;

    public static void main(String[] args) {
        market = new Market();
        market.loadSampleData();
        input = new InputReader(new Scanner(System.in));
        cashier = new Cashier(market, input);

        System.out.println("=================================================");
        System.out.println("   SISTEMA DE ESTOQUE, COMPRAS E CAIXA");
        System.out.println("=================================================");

        try {
            runMenu();
        } catch (NoSuchElementException exception) {
            System.out.println("\nEntrada encerrada.");
        }
        System.out.println("Sistema finalizado.");
    }

    private static void runMenu() {
        while (true) {
            printMenu();
            int option = input.readInt("Opção: ", 0, 10);
            if (option == 0) {
                return;
            }
            try {
                switch (option) {
                    case 1 -> registerProduct();
                    case 2 -> restock();
                    case 3 -> printStock();
                    case 4 -> market.catalog().printPriceTable();
                    case 5 -> showCategories();
                    case 6 -> addCustomer();
                    case 7 -> showQueue();
                    case 8 -> cashier.attendCustomers();
                    case 9 -> showBoughtTogether();
                    case 10 -> cashier.runSimulation();
                    default -> System.out.println("Opção inválida.");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Erro: " + exception.getMessage());
            }
            input.readLine("\nPressione ENTER para continuar...");
        }
    }

    private static void printMenu() {
        System.out.println("\n----------------- MENU PRINCIPAL -----------------");
        System.out.println(" 1. Cadastrar produto              (lista encadeada)");
        System.out.println(" 2. Repor estoque");
        System.out.println(" 3. Listar estoque                 (lista encadeada)");
        System.out.println(" 4. Tabela de códigos e preços     (array)");
        System.out.println(" 5. Departamentos e categorias     (árvore)");
        System.out.println(" 6. Adicionar cliente à fila       (fila)");
        System.out.println(" 7. Ver fila de atendimento        (fila)");
        System.out.println(" 8. Atender próximo cliente        (caixa + pilha)");
        System.out.println(" 9. Produtos comprados juntos      (grafo)");
        System.out.println("10. Simulação automática de compra");
        System.out.println(" 0. Sair");
        System.out.println("--------------------------------------------------");
    }

    private static void registerProduct() {
        if (market.catalog().isFull()) {
            System.out.println("Catálogo cheio. Não é possível cadastrar mais produtos.");
            return;
        }
        int code = input.readInt("Código (1 a 99999): ", 1, 99999);
        if (market.catalog().findByCode(code) != null) {
            System.out.println("Já existe um produto com esse código.");
            return;
        }
        String name = input.readText("Nome: ", 30);
        int price = input.readMoneyInCents("Preço: R$ ", 1, 10000000);
        int quantity = input.readInt("Quantidade inicial (0 a " + Market.MAX_STOCK_PER_PRODUCT + "): ",
                0, Market.MAX_STOCK_PER_PRODUCT);
        System.out.println("Departamentos existentes:");
        market.categories().printDepartmentNames();
        String department = input.readText("Departamento: ", 20);
        String category = input.readText("Categoria: ", 20);
        Product product = new Product(code, name, price, quantity, department, category);
        market.registerProduct(product);
        System.out.println("Produto cadastrado: " + product);
    }

    private static void restock() {
        int code = input.readInt("Código do produto: ", 1, 99999);
        Product product = market.stock().findByCode(code);
        if (product == null) {
            System.out.println("Produto não encontrado.");
            return;
        }
        System.out.println(product);
        int limit = Market.MAX_STOCK_PER_PRODUCT - product.getQuantity();
        if (limit <= 0) {
            System.out.println("Estoque já está no limite máximo.");
            return;
        }
        int amount = input.readInt("Quantidade a adicionar (1 a " + limit + "): ", 1, limit);
        market.restock(code, amount);
        System.out.println("Estoque atualizado: " + product);
    }

    private static void printStock() {
        market.stock().print();
        System.out.println("\nProdutos com estoque baixo (até 5 unidades):");
        market.stock().printLowStock(5);
    }

    private static void showCategories() {
        System.out.println("1. Ver árvore completa");
        System.out.println("2. Ver um departamento");
        int option = input.readInt("Opção: ", 1, 2);
        if (option == 1) {
            market.categories().print();
            return;
        }
        market.categories().printDepartmentNames();
        String department = input.readText("Departamento: ", 20);
        if (!market.categories().printDepartment(department)) {
            System.out.println("Departamento não encontrado.");
        }
    }

    private static void addCustomer() {
        String name = input.readText("Nome do cliente: ", 30);
        Customer customer = market.queue().enqueue(name);
        System.out.printf("%s entrou na fila. Posição: %d%n", customer, market.queue().size());
    }

    private static void showQueue() {
        market.queue().print();
        Customer next = market.queue().peek();
        if (next != null) {
            System.out.println("Próximo a ser atendido: " + next);
        }
    }

    private static void showBoughtTogether() {
        System.out.println("Matriz de adjacência (quantas vezes cada par foi comprado junto):");
        market.boughtTogether().printMatrix();
        if (!input.readYesNo("\nVer recomendações para um produto?")) {
            return;
        }
        int code = input.readInt("Código do produto: ", 1, 99999);
        Product product = market.catalog().findByCode(code);
        if (product == null) {
            System.out.println("Produto não encontrado.");
            return;
        }
        int[] related = market.boughtTogether().recommendationsFor(code, 3);
        if (related.length == 0) {
            System.out.println("Nenhuma relação registrada para " + product.getName() + ".");
            return;
        }
        System.out.printf("Quem compra %s também leva (grau do vértice: %d):%n",
                product.getName(), market.boughtTogether().degreeOf(code));
        for (int relatedCode : related) {
            System.out.printf("  - %s (%d vez(es))%n",
                    market.catalog().findByCode(relatedCode).getName(),
                    market.boughtTogether().weightBetween(code, relatedCode));
        }
    }
}
