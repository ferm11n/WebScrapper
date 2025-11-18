package scraper.domain.View;

import java.util.Scanner;

import scraper.domain.service.SupermarketScraper;

public class ConsoleMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final SupermarketScraper scraperService = new SupermarketScraper();

    public void start() throws InterruptedException {
        while (true) {
            mostrarMenuPrincipal();
            int opcion = leerOpcion();

            switch (opcion) {
                case 1 -> scraperService.scrapearTodo();
                case 2 -> menuSupermercados();
                case 3 -> {
                    System.out.println("Saliendo...");
                    return;
                }
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void mostrarMenuPrincipal() {
        System.out.println("==================================");
        System.out.println("       SISTEMA DE SCRAPING        ");
        System.out.println("==================================");
        System.out.println("1) Scrapear TODOS los supermercados");
        System.out.println("2) Scrapear por supermercado");
        System.out.println("3) Salir");
        System.out.print("Seleccione una opción: ");
    }

    private void menuSupermercados() throws InterruptedException {
        System.out.println("===== SUPERMERCADOS =====");
        System.out.println("1) Coto");
        System.out.println("2) La Anónima");
        System.out.println("3) Jumbo");
        System.out.println("4) Carrefour");
        System.out.println("5) Volver");
        System.out.print("Seleccione una opción: ");

        int opcion = leerOpcion();

        switch (opcion) {
            case 1 -> scraperService.scrapearSupermercado("COTO");
            case 2 -> scraperService.scrapearSupermercado("LA_ANONIMA");
            case 3 -> scraperService.scrapearSupermercado("VEA");
            case 4 -> scraperService.scrapearSupermercado("CARREFOUR");
            case 5 -> { return; }
            default -> System.out.println("Opción inválida.");
        }
    }

    private int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }
}
