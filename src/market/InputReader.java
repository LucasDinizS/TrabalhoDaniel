package market;

import java.util.Scanner;

public class InputReader {
    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            String value = readLine(prompt);
            try {
                int number = Integer.parseInt(value);
                if (number < min || number > max) {
                    System.out.printf("Valor fora do intervalo permitido (%d a %d).%n", min, max);
                    continue;
                }
                return number;
            } catch (NumberFormatException exception) {
                System.out.println("Entrada inválida: digite um número inteiro.");
            }
        }
    }

    public int readMoneyInCents(String prompt, long minCents, long maxCents) {
        while (true) {
            String value = readLine(prompt).replace(',', '.');
            if (!value.matches("\\d{1,7}(\\.\\d{1,2})?")) {
                System.out.println("Valor inválido: use o formato 12,50 (no máximo 2 casas decimais).");
                continue;
            }
            String[] parts = value.split("\\.");
            long cents = Long.parseLong(parts[0]) * 100;
            if (parts.length == 2) {
                String fraction = parts[1].length() == 1 ? parts[1] + "0" : parts[1];
                cents += Long.parseLong(fraction);
            }
            if (cents < minCents || cents > maxCents) {
                System.out.printf("Valor fora do intervalo permitido (%s a %s).%n",
                        Money.format(minCents), Money.format(maxCents));
                continue;
            }
            return (int) cents;
        }
    }

    public String readText(String prompt, int maxLength) {
        while (true) {
            String value = readLine(prompt);
            if (value.isEmpty()) {
                System.out.println("O campo não pode ficar vazio.");
                continue;
            }
            if (value.length() > maxLength) {
                System.out.printf("Texto muito longo (máximo de %d caracteres).%n", maxLength);
                continue;
            }
            return value;
        }
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            String value = readLine(prompt + " (s/n): ").toLowerCase();
            if (value.equals("s") || value.equals("sim")) {
                return true;
            }
            if (value.equals("n") || value.equals("nao") || value.equals("não")) {
                return false;
            }
            System.out.println("Responda com 's' ou 'n'.");
        }
    }
}
