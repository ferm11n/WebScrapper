package scraper.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String price;
    private String url;

    private String categoria; // nueva columna para la categoría

    public Product() {}

    public Product(String title, String price, String url, String categoria) {
        this.title = title;
        this.price = price;
        this.url = url;
        this.categoria = categoria;
    }

    // getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public String getUrl() { return url; }
    public String getCategoria() { return categoria; }

    // setters
    public void setTitle(String title) { this.title = title; }
    public void setPrice(String price) { this.price = price; }
    public void setUrl(String url) { this.url = url; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}
