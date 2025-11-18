package scraper.domain.service;

import org.openqa.selenium.WebDriver;
import scraper.config.AppConfig;
import scraper.domain.model.Product;
import scraper.infraestructure.repository.ProductRepository;
import scraper.supermarkets.CotoScraper;
import scraper.supermarkets.LaAnonimaScraper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SupermarketScraper {

    private final ProductRepository repo = new ProductRepository();

    public void scrapearTodo() throws InterruptedException {
        System.out.println("Scrapeando TODOS los supermercados...");

        scrapearSupermercado("COTO");
        scrapearSupermercado("LA_ANONIMA");
        scrapearSupermercado("JUMBO");
        scrapearSupermercado("CARREFOUR");
    }

    public void scrapearSupermercado(String nombre) throws InterruptedException {
        System.out.println("Scrapeando " + nombre + "...");

        WebDriver driver = scraper.config.DriverFactory.create();

        try {

            switch (nombre) {

                case "COTO" -> {
                    CotoScraper scraper = new CotoScraper();
                    Map<String, String> categorias = AppConfig.getCategorias("category.");

                    for (var entry : categorias.entrySet()) {
                        List<Product> productos =
                                scraper.scrapeAllPages(driver, entry.getValue(), entry.getKey(), "COTO");
                        guardar(productos);
                    }
                }

                case "LA_ANONIMA" -> {
                    LaAnonimaScraper laa = new LaAnonimaScraper();
                    Map<String, String> categorias = AppConfig.getCategorias("LaAnonima.");

                    List<Product> all = new ArrayList<>();

                    for (var entry : categorias.entrySet()) {
                        List<Product> productos =
                                laa.scrapeAllPages(driver, entry.getValue(), entry.getKey(), "LA_ANONIMA");
                        all.addAll(productos);
                    }

                    guardar(all);
                }

                case "JUMBO" -> System.out.println("Scraper de VEA no implementado aún.");

                case "CARREFOUR" -> System.out.println("Scraper de Carrefour no implementado aún.");

                default -> System.out.println("Supermercado no reconocido.");
            }

        } finally {
            driver.quit();
        }

        System.out.println("Finalizado: " + nombre);
    }

    private void guardar(List<Product> productos) {
        for (Product p : productos) {
            repo.save(p);
        }
        System.out.println("Guardados: " + productos.size() + " productos.");
    }
}
