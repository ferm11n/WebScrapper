package scraper;

import scraper.domain.View.ConsoleMenu;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        ConsoleMenu menu = new ConsoleMenu();
        // muestra el menú inmediatamente
        menu.start();
    }
}