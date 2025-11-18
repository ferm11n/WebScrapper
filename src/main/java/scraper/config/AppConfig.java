package scraper.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AppConfig {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = AppConfig.class.getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (in == null)
                throw new RuntimeException("No se encontró config.properties");

            props.load(in);

        } catch (IOException e) {
            throw new RuntimeException("Error cargando config.properties", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    // Nuevo método para obtener todas las categorías
    public static Map<String, String> getCategorias() {
        Map<String, String> categorias = new HashMap<>();
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith("category.")) {
                String nombreCategoria = key.substring("category.".length());
                categorias.put(nombreCategoria, props.getProperty(key));
            }
        }
        return categorias;
    }

    // Obtener categorías de La Anónima
    public static Map<String, String> getCategorias(String prefix) {
        Map<String, String> categorias = new HashMap<>();
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                String nombreCategoria = key.substring(prefix.length());
                categorias.put(nombreCategoria, props.getProperty(key));
            }
        }
        return categorias;
    }

}
