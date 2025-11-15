package scraper.infraestructure.csv;

import scraper.domain.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvDataWriter {

    private static final Logger log = LoggerFactory.getLogger(CsvDataWriter.class);

    public void write(String filePath, List<Product> products) {
        log.info("Escribiendo CSV en {}", filePath);
        log.info("Total de productos a escribir: {}", products.size());

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filePath),
                        StandardCharsets.UTF_8
                ))) {
            
            //Escribir BOM UTF-8
            writer.write('\ufeff');

            // Encabezado
            writer.write("title,price,url");
            writer.newLine();

            // Filas
            for (Product p : products) {
                writer.write(
                        formatCsv(p.title()) + "," +
                        formatCsv(p.price()) + "," +
                        formatCsv(p.url())
                );
                writer.newLine();
            }

            log.info("CSV generado correctamente: {}", filePath);

        } catch (IOException e) {
            log.error("Error al escribir CSV: {}", e.getMessage());
            throw new RuntimeException("Error escribiendo CSV", e);
        }
    }

    private String formatCsv(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
