package scraper.config;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

    public static WebDriver create() {

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--disable-gpu");
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");

        /* 
        // IMPORTANTE para evitar que La Anónima cierre la sesión
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--lang=es-AR");
        options.addArguments("Accept-Language=es-AR");

        // Evita cierre por redirecciones silenciosas
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        */

        return new ChromeDriver(options);
    }
}

