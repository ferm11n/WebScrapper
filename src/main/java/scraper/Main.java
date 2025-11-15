package scraper;

import scraper.config.AppConfig;
import scraper.config.DriverFactory;
import scraper.domain.model.Product;
import scraper.domain.service.ScraperService;
import scraper.infraestructure.repository.ProductRepository;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        WebDriver driver = DriverFactory.create();
        ScraperService scraper = new ScraperService();
        ProductRepository repo = new ProductRepository();

        Map<String, String> categorias = AppConfig.getCategorias();

        try {
            for (Map.Entry<String, String> entry : categorias.entrySet()) {
                String nombreCategoria = entry.getKey();
                String urlCategoria = entry.getValue();

                log.info("Iniciando scraping de categoría: {} -> {}", nombreCategoria, urlCategoria);

                // Ahora scrapeAllPages maneja todas las páginas de la categoría
                List<Product> products = scraper.scrapeAllPages(driver, urlCategoria, nombreCategoria);

                // Guardamos todos los productos en la base de datos
                for (Product p : products) {
                    repo.save(p);
                }

                log.info("Scraping finalizado para categoría {}. Total productos guardados: {}", 
                        nombreCategoria, products.size());
            }

            log.info("Scraping finalizado para todas las categorías.");

        } catch (Exception e) {
            log.error("Error en la ejecución: {}", e.getMessage(), e);

        } finally {
            log.info("Cerrando driver...");
            driver.quit();
        }
    }
}
