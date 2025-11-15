package scraper;

import scraper.config.AppConfig;
import scraper.config.DriverFactory;
import scraper.domain.service.ScraperService;
import scraper.infraestructure.csv.CsvDataWriter;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        String url = AppConfig.get("target.url");
        
        String output = AppConfig.get("output.csv");

        log.info("URL objetivo: {}", url);
        log.info("Archivo de salida: {}", output);

        WebDriver driver = DriverFactory.create();
        ScraperService scraper = new ScraperService();
        CsvDataWriter writer = new CsvDataWriter();

        try {
            log.info("Iniciando proceso de scraping...");
            var products = scraper.scrapeProducts(driver, url);

            writer.write(output, products);

            log.info("Scraping finalizado correctamente.");
            log.info("Productos exportados a {}", output);

        } catch (Exception e) {
            log.error("Error en la ejecución: {}", e.getMessage());
            e.printStackTrace();

        } finally {
            log.info("Cerrando driver...");
            driver.quit();
        }
    }
}
