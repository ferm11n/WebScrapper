package scraper.domain.parser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import scraper.domain.model.Product;

public class HtmlParser {

    public Product parseProduct(WebElement el, String categoria, String supermercado) {

        String title = "";
        String price = "";
        String url = "";

        // ----------------------
        // 1. EXTRAER TITULO
        // ----------------------
        try {
            // Caso principal
            title = el.findElement(By.cssSelector(".product-name, .nombre-producto"))
                      .getText()
                      .trim();
        } catch (Exception ignored) {}

        // Fallback
        if (title.isEmpty()) {
            try {
                title = el.findElement(By.cssSelector("p, h2, h3"))
                          .getText()
                          .trim();
            } catch (Exception ignored) {}
        }

        // ----------------------
        // 2. EXTRAER PRECIO
        // ----------------------
        try {
            price = el.findElement(By.cssSelector(".product-price, .precio, .precio-decimal"))
                      .getText()
                      .trim();
        } catch (Exception ignored) {}

        // Fallback
        if (price.isEmpty()) {
            try {
                price = el.findElement(By.xpath(".//*[contains(text(), '$')]"))
                          .getText()
                          .trim();
            } catch (Exception ignored) {}
        }

        // ----------------------
        // 3. EXTRAER URL
        // ----------------------
        try {
            url = el.findElement(By.cssSelector("a"))
                    .getAttribute("href");
        } catch (Exception ignored) {}

        // ----------------------
        // 4. VALIDACIONES
        // ----------------------
        if (title == null || title.isBlank()) title = "SIN TITULO";
        if (price == null || price.isBlank()) price = "$0";
        if (url == null) url = "https://www.cotodigital.com.ar";

        return new Product(title, price, url, categoria, supermercado);
    }
}
