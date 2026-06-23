package util;

public class MenuHelper {

    public static void printHeader(String title) {

        System.out.println("  " + title);
        System.out.println("\n");
    }

    public static void printSuccess(String message) {
        System.out.println("✓ Sucesso: " + message);
    }

    public static void printError(String message) {
        System.err.println("✗ Erro: " + message);
    }

    public static void printInfo(String message) {
        System.out.println("ℹ " + message);
    }

    public static void printSeparator() {
        System.out.println("-----------------------------------------");
    }

    public static int showMenu(String title, String... options) {
        printHeader(title);
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ") " + options[i]);
        }
        System.out.println("0) Sair");
        int choice = InputValidator.readInt("Escolha uma opção: ");
        if (choice < 0 || choice > options.length) {
            printError("Opção inválida!");
            return -1;
        }
        return choice;
    }
}

