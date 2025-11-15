package scraper.domain.parser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import scraper.domain.model.Product;

public class HtmlParser {

    public Product parseProduct(WebElement el, String categoria) {

        String title = "";
        String price = "";
        String url = "";

        try {
            // Nombre del producto
            title = el.findElement(By.cssSelector(".centro-precios .nombre-producto"))
                      .getText()
                      .trim();
        } catch (Exception ignored) {}

        try {
            // Precio del producto
            price = el.findElement(By.cssSelector(".centro-precios .card-title"))
                      .getText()
                      .trim();
        } catch (Exception ignored) {}

        try {
            // URL del producto
            url = el.findElement(By.cssSelector("a")).getAttribute("href");
        } catch (Exception ignored) {}

        // Creamos el producto directamente con la categoría
        return new Product(title, price, url, categoria);
    }
}
