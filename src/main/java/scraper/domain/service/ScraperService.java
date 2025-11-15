package scraper.domain.service;

import scraper.domain.model.Product;
import scraper.domain.parser.HtmlParser;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ScraperService {

    private static final Logger log = LoggerFactory.getLogger(ScraperService.class);

    private final HtmlParser parser = new HtmlParser();

    public List<Product> scrapeProducts(WebDriver driver, String url) {

        log.info("Iniciando scraping de {}", url);
        driver.get(url);

        //ESPERAR que Angular renderice los productos
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".producto-card")
            ));
        } catch (TimeoutException e) {
            log.error("No se encontraron elementos .producto-card dentro del tiempo esperado");
        }

        List<WebElement> elements = driver.findElements(By.cssSelector(".producto-card"));
        log.info("{} productos encontrados en la página inicial", elements.size());

        List<Product> products = new ArrayList<>();

        for (WebElement el : elements) {
            try {
                products.add(parser.parseProduct(el));
            } catch (Exception e) {
                log.error("Error parseando un elemento: {}", e.getMessage());
            }
        }

        return products;
    }
}
