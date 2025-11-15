package scraper.config;

import java.io.IOException;
import java.io.InputStream;
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
}
