package util;

import java.util.Scanner;

public class InputValidator {

    private static final Scanner scanner = new Scanner(System.in);

    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.err.println("Erro: Digite um número inteiro válido.");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.err.println("Erro: Digite um número decimal válido.");
            }
        }
    }

    public static String readNome(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.err.println("Erro: Nome não pode estar vazio.");
                continue;
            }
            if (!isValidName(input)) {
                System.err.println("Erro: Nome pode conter apenas letras e espaços.");
                continue;
            }
            return input;
        }
    }

    public static String readCPF(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.err.println("Erro: CPF não pode estar vazio.");
                continue;
            }
            if (!isValidCPFFormat(input)) {
                System.err.println("Erro: CPF deve ter formato XXX.XXX.XXX-XX ou XXXXXXXXXXX (apenas dígitos/pontos/hífen).");
                continue;
            }
            return input;
        }
    }

    public static String readTelefone(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.err.println("Erro: Telefone não pode estar vazio.");
                continue;
            }
            return input;
        }
    }

    public static String readPlaca(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.isEmpty()) {
                System.err.println("Erro: Placa não pode estar vazia.");
                continue;
            }
            if (!isValidPlaca(input)) {
                System.err.println("Erro: Placa deve ter formato ABC-1234 (3 letras, hífen, 4 números).");
                continue;
            }
            return input;
        }
    }

    public static String readTexto(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static boolean isValidName(String name) {
        return name.matches("^[a-zA-ZáéíóúàâêôãõçÁÉÍÓÚÀÂÊÔÃÕÇ ]+$");
    }

    private static boolean isValidCPFFormat(String cpf) {
        // aceita XXX.XXX.XXX-XX ou XXXXXXXXXXX
        return cpf.matches("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$") || cpf.matches("^\\d{11}$");
    }

    private static boolean isValidPlaca(String placa) {
        // ABC-1234 ou ABC1234
        return placa.matches("^[A-Z]{3}-?\\d{4}$");
    }

    public static void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }
}

