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

public class LaAnonimaScraper {

    private static final Logger log = LoggerFactory.getLogger(LaAnonimaScraper.class);
    private final HtmlParser parser = new HtmlParser();

    public List<Product> scrapeCategoria(WebDriver driver, String url, String categoria) throws InterruptedException {
        List<Product> allProducts = new ArrayList<>();

        log.info("Scrapeando categoría La Anónima: {}", url);

        // ============================================================
        // FIX: limpiar cookies + localStorage para forzar popup
        // ============================================================
        driver.get("https://www.laanonima.com.ar");

        driver.manage().deleteAllCookies();
        ((JavascriptExecutor) driver).executeScript(
                "window.localStorage.clear(); window.sessionStorage.clear();"
        );

        Thread.sleep(700);
        driver.navigate().refresh();
        Thread.sleep(1200);
        // ============================================================

        // Ahora sí cargar la categoría real
        driver.get(url);
        // esperar hidratación de React
        Thread.sleep(1500);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        manejarPopupCodigoPostal(driver, wait);

        esperarProductos(driver);
        scrollLentoHastaCargarTodo(driver);

        boolean haySiguiente = true;

        while (haySiguiente) {

            // EXTRAER PRODUCTOS
            List<WebElement> elements = driver.findElements(By.cssSelector("div.producto-item"));
            log.info("Productos encontrados en esta página: {}", elements.size());

            for (WebElement el : elements) {
                try {
                    Product p = parser.parseProduct(el, categoria, "LA_ANONIMA");
                    allProducts.add(p);
                } catch (Exception e) {
                    log.error("Error parseando producto: {}", e.getMessage());
                }
            }

            // SIGUIENTE PÁGINA
            haySiguiente = clickSiguiente(driver);

            if (haySiguiente) {
                esperarProductos(driver);
                scrollLentoHastaCargarTodo(driver);
            }
        }

        return allProducts;
    }


    private void esperarProductos(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(12))
                .until(d -> d.findElements(By.cssSelector("div.producto-item")).size() > 0);
    }


    private boolean clickSiguiente(WebDriver driver) {
        try {
            WebElement btn = null;

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

            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.not(ExpectedConditions.urlToBe(oldUrl)));

            esperarProductos(driver);

            return true;

        } catch (Exception e) {
            log.warn("Fallo al cambiar página: {}", e.getMessage());
            return false;
        }
    }


    public List<Product> scrapeAllPages(WebDriver driver, String urlCategoria, String nombreCategoria, String supermercado) throws InterruptedException {
        return scrapeCategoria(driver, urlCategoria, nombreCategoria);
    }


    private void scrollLentoHastaCargarTodo(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        int ciclosSinCambios = 0;
        int ultimoConteo = 0;

        while (ciclosSinCambios < 3) {
            int conteoActual = driver.findElements(By.cssSelector("div.producto-item")).size();

            if (conteoActual > ultimoConteo) {
                ultimoConteo = conteoActual;
                ciclosSinCambios = 0;
            } else {
                ciclosSinCambios++;
            }

            js.executeScript("window.scrollBy(0, 600);");

            try {
                Thread.sleep(1200);
            } catch (InterruptedException ignored) {}
        }

        System.out.println("Scroll finalizado. Productos detectados: " + ultimoConteo);
    }



    // ============================================================
    // POPUP: INGRESAR CÓDIGO POSTAL + SELECCIÓN DE SUCURSAL
    // ============================================================
    private void manejarPopupCodigoPostal(WebDriver driver, WebDriverWait wait) {
        try {
            // Input CP
            WebElement inputCP = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("idCodigoPostalUnificado"))
            );

            inputCP.clear();
            inputCP.sendKeys("8300");

            // Botón aceptar
            WebElement btnAceptar = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("button.boton-aceptar-cp"))
            );
            btnAceptar.click();
            System.out.println("Código postal ingresado ✔");

            Thread.sleep(900);

            // Popup sucursales
            WebElement popup = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("div.modal-content, div.contenedor-sucursales")
                    )
            );

            System.out.println("Popup de sucursales visible ✔");

            WebElement sucursal = null;

            // PRIORIDAD 1 — label for="sucursal_8"
            try {
                sucursal = popup.findElement(By.cssSelector("label[for='sucursal_8']"));
            } catch (Exception ignored) {}

            // PRIORIDAD 2 — data-sucursal
            if (sucursal == null) {
                try {
                    sucursal = popup.findElement(By.cssSelector("[data-sucursal='HIPERMERCADO NEUQUEN']"));
                } catch (Exception ignored) {}
            }

            // PRIORIDAD 3 — texto visible
            if (sucursal == null) {
                List<WebElement> labels = popup.findElements(By.cssSelector("label, div, span, u"));
                for (WebElement l : labels) {
                    if (l.getText().toUpperCase().contains("HIPERMERCADO NEUQUEN")) {
                        sucursal = l;
                        break;
                    }
                }
            }

            if (sucursal == null) {
                System.out.println("No se encontró la sucursal HIPERMERCADO NEUQUEN.");
                return;
            }

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});",
                    sucursal
            );

            Thread.sleep(500);

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", sucursal);
            System.out.println("Sucursal HIPERMERCADO NEUQUEN seleccionada");

            Thread.sleep(400);

            WebElement btnConfirmar = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("button.boton-confirmar-sucursal"))
            );

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnConfirmar);
            System.out.println("Sucursal confirmada ✔");

            wait.until(ExpectedConditions.invisibilityOf(popup));

        } catch (Exception e) {
            System.out.println("No apareció el popup o ya había una sucursal asignada.");
        }
    }

}
