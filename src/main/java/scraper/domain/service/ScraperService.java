package scraper.domain.service;

import scraper.domain.model.Product;
import scraper.domain.parser.HtmlParser;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ScraperService {

    private static final Logger log = LoggerFactory.getLogger(ScraperService.class);
    private final HtmlParser parser = new HtmlParser();

    /**
     * Scrapea todos los productos de todas las páginas de la categoría
     */
    public List<Product> scrapeAllPages(WebDriver driver, String url, String categoria) {
        List<Product> allProducts = new ArrayList<>();
        driver.get(url);

        boolean haySiguiente = true;
        int paginaActual = 1;

        while (haySiguiente) {
            log.info("Scrapeando página {} de categoría {}", paginaActual, categoria);

            // Scrapeamos productos de la página actual
            List<WebElement> elements = driver.findElements(By.cssSelector(".producto-card"));
            log.info("{} productos encontrados en la página actual", elements.size());

            for (WebElement el : elements) {
                try {
                    allProducts.add(parser.parseProduct(el, categoria));
                } catch (Exception e) {
                    log.error("Error parseando un producto: {}", e.getMessage());
                }
            }

            // Detectar botón "Siguiente"
            haySiguiente = false;
            List<WebElement> paginas = driver.findElements(By.cssSelector("ul.pagination li a.page-link"));

            for (WebElement pagina : paginas) {
                String texto = pagina.getText().trim();
                if (texto.equals(">") || texto.equals("Siguiente")) {
                    try {
                        String primerProductoAntes = elements.isEmpty() ? "" :
                                elements.get(0).findElement(By.cssSelector(".nombre-producto")).getText();

                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", pagina);
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", pagina);

                        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                        final String primerAntes = primerProductoAntes;

                        wait.until((ExpectedCondition<Boolean>) d -> {
                            List<WebElement> elems = d.findElements(By.cssSelector(".producto-card .nombre-producto"));
                            if (elems.isEmpty()) return false;
                            String primerActual = elems.get(0).getText();
                            return !primerActual.equals(primerAntes);
                        });

                        haySiguiente = true;
                        paginaActual++;
                    } catch (Exception e) {
                        log.warn("No se pudo avanzar a la siguiente página: {}", e.getMessage());
                    }
                    break;
                }
            }
        }

        log.info("Se completó el scraping de categoría {}. Total productos: {}", categoria, allProducts.size());
        return allProducts;
    }
}
