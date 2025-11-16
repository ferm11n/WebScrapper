package scraper.supermarkets;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import scraper.domain.model.Product;
import scraper.domain.parser.HtmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CotoScraper {

    private static final Logger log = LoggerFactory.getLogger(CotoScraper.class);
    private final HtmlParser parser = new HtmlParser();

    /**
     * Scrapea todas las páginas de una categoría de Coto
     */
    public List<Product> scrapeCategoria(WebDriver driver, String url, String categoria) {
        List<Product> allProducts = new ArrayList<>();

        log.info("Scrapeando categoría Coto: {}", url);
        driver.get(url);

        esperarProductos(driver);

        boolean haySiguiente = true;

        while (haySiguiente) {

            // --- SCROLL PARA CARGAR LOS PRODUCTOS DINÁMICOS ---
            scrollToLoad(driver);

            // --- EXTRAER PRODUCTOS ---
            List<WebElement> elements = driver.findElements(By.cssSelector(".producto-card"));
            log.info("Productos encontrados en esta página: {}", elements.size());

            for (WebElement el : elements) {
                try {
                    Product p = parser.parseProduct(el, categoria, "COTO");
                    allProducts.add(p);
                } catch (Exception e) {
                    log.error("Error parseando producto: {}", e.getMessage());
                }
            }

            // --- CAMBIO DE PÁGINA ---
            haySiguiente = clickSiguiente(driver);
        }

        return allProducts;
    }


    /**
     * Espera que existan productos en la página
     */
    private void esperarProductos(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(d -> d.findElements(By.cssSelector(".producto-card")).size() > 0);
    }


    /**
     * Scroll rápido hasta cargar todo
     */
    private void scrollToLoad(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        long lastHeight = (long) js.executeScript("return document.body.scrollHeight");

        for (int i = 0; i < 5; i++) { // reducido a 5 iteraciones → mucho más rápido
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            try { Thread.sleep(600); } catch (InterruptedException ignored) {}

            long newHeight = (long) js.executeScript("return document.body.scrollHeight");
            if (newHeight == lastHeight) break;
            lastHeight = newHeight;
        }
    }


    /**
     * Clic en SIGUIENTE y espera a que cambie la URL → ultra estable
     */
    private boolean clickSiguiente(WebDriver driver) {
        try {
            WebElement btn = null;

            // Buscar el botón "Siguiente" o ">"
            List<WebElement> links = driver.findElements(By.cssSelector("ul.pagination li a.page-link"));
            for (WebElement a : links) {
                String txt = a.getText().trim();
                if (txt.equals(">") || txt.equalsIgnoreCase("Siguiente")) {
                    btn = a;
                    break;
                }
            }

            if (btn == null) {
                log.info("No hay siguiente página.");
                return false;
            }

            String oldUrl = driver.getCurrentUrl();

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

            // Esperar cambio de URL (MUY FIABLE)
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.not(ExpectedConditions.urlToBe(oldUrl)));

            esperarProductos(driver);

            return true;

        } catch (Exception e) {
            log.warn("Fallo al cambiar página: {}", e.getMessage());
            return false;
        }
    }


    /**
     * Método requerido por tu Main
     */
    public List<Product> scrapeAllPages(WebDriver driver, String urlCategoria, String nombreCategoria, String supermercado) {
        return scrapeCategoria(driver, urlCategoria, nombreCategoria);
    }
}
