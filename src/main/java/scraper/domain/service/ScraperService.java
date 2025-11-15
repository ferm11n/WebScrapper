package scraper.domain.service;

import scraper.domain.model.Product;
import scraper.domain.parser.HtmlParser;
import org.openqa.selenium.*;
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
     * Scrapea todos los productos de una categoría, recorriendo todas sus páginas.
     *
     * @param driver WebDriver inicializado
     * @param urlCategoria URL inicial de la categoría
     * @param categoria Nombre de la categoría
     * @return Lista de productos de todas las páginas
     */
    public List<Product> scrapeAllPages(WebDriver driver, String urlCategoria, String categoria) {

        driver.get(urlCategoria);
        List<Product> allProducts = new ArrayList<>();
        boolean haySiguiente = true;

        while (haySiguiente) {

            log.info("Scrapeando página: {}", driver.getCurrentUrl());

            // 1. Scrapeamos los productos visibles
            List<WebElement> elements = driver.findElements(By.cssSelector(".producto-card"));
            for (WebElement el : elements) {
                try {
                    Product p = parser.parseProduct(el, categoria);
                    allProducts.add(p);
                } catch (Exception e) {
                    log.warn("Error parseando producto: {}", e.getMessage());
                }
            }

            log.info("Productos encontrados hasta ahora en la categoría {}: {}", categoria, allProducts.size());

            // 2. Revisamos si hay botón "Siguiente"
            List<WebElement> paginas = driver.findElements(By.cssSelector("ul.pagination li a.page-link"));
            haySiguiente = false;

            for (WebElement pagina : paginas) {
                String texto = pagina.getText().trim();
                if (texto.equals(">") || texto.equals("Siguiente")) {
                    try {
                        // Hacemos scroll y clic con JS
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", pagina);

                        // Guardamos un identificador del primer producto actual para esperar al cambio
                        String primerAntes = elements.isEmpty() ? "" : elements.get(0).getText();
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", pagina);

                        // Esperamos a que se cargue la nueva página
                        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                        final String primerProductoAntes = primerAntes;
                        wait.until((ExpectedCondition<Boolean>) d -> {
                            List<WebElement> elems = d.findElements(By.cssSelector(".producto-card .nombre-producto"));
                            if (elems.isEmpty()) return false;
                            String primerActual = elems.get(0).getText();
                            return !primerActual.equals(primerProductoAntes);
                        });

                        haySiguiente = true;
                    } catch (Exception e) {
                        log.warn("No se pudo avanzar a la siguiente página: {}", e.getMessage());
                    }
                    break;
                }
            }
        }

        return allProducts;
    }
}
