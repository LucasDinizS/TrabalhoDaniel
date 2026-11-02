package market;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static Market market;
    private static Cashier cashier;

    public static void main(String[] args) {
        market = new Market();
        market.loadSampleData();
        cashier = new Cashier(market);

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

    static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException exception) {
                System.out.println("Entrada inválida: digite um número inteiro.");
            }
        }
    }

    static double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readLine(prompt).replace(',', '.'));
            } catch (NumberFormatException exception) {
                System.out.println("Valor inválido: use o formato 12,50.");
            }
        }
    }

    static boolean readYes(String prompt) {
        return readLine(prompt + " (s/n): ").equalsIgnoreCase("s");
    }

    private static void runMenu() {
        while (true) {
            printMenu();
            int option = readInt("Opção: ");
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
            readLine("\nPressione ENTER para continuar...");
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
        int code = readInt("Código: ");
        if (market.catalog().findByCode(code) != null) {
            System.out.println("Já existe um produto com esse código.");
            return;
        }
        String name = readLine("Nome: ");
        double price = readDouble("Preço: R$ ");
        int quantity = readInt("Quantidade inicial: ");
        System.out.println("Departamentos existentes:");
        market.categories().printDepartmentNames();
        String department = readLine("Departamento: ");
        String category = readLine("Categoria: ");
        Product product = new Product(code, name, price, quantity, category);
        market.registerProduct(product, department);
        System.out.println("Produto cadastrado: " + product);
    }

    private static void restock() {
        int code = readInt("Código do produto: ");
        Product product = market.stock().findByCode(code);
        if (product == null) {
            System.out.println("Produto não encontrado.");
            return;
        }
        System.out.println(product);
        int amount = readInt("Quantidade a adicionar: ");
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
        int option = readInt("Opção: ");
        if (option == 1) {
            market.categories().print();
            return;
        }
        market.categories().printDepartmentNames();
        String department = readLine("Departamento: ");
        if (!market.categories().printDepartment(department)) {
            System.out.println("Departamento não encontrado.");
        }
    }

    private static void addCustomer() {
        String name = readLine("Nome do cliente: ");
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
        if (!readYes("\nVer recomendações para um produto?")) {
            return;
        }
        int code = readInt("Código do produto: ");
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
