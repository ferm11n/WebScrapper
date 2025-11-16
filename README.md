# 🛒 Java Web Scraping Project
Este proyecto es un scraper en Java que navega automáticamente por las páginas de productos de varios supermercados, extrae información relevante y la almacena en una base de datos.

El objetivo es obtener de forma consistente precios y datos de productos, avanzando por cada página mediante la paginación del sitio.

# Funcionalidades actuales
* Obtención del HTML de cada página del supermercado.
- Extracción de: Nombre del producto, Precio, Url, Categoria y Supermercado al que corresponde.
- Detección automática del botón "Siguiente" en la paginación.
- Avance página por página hasta que la paginación se agota.
- Logs por cada página indicando la cantidad de productos encontrados.

# Tecnologías usadas
- Java 17+
- Jsoup (parseo HTML)
- SLF4J / Logback (logging)
- Maven (gestion del proyecto)

# Cómo ejecutar
1. Instalar dependencias:
```sql
mvn clean install
```

2. Ejecutar el scraper:
```sql
mvn exec:java -Dexec.mainClass="Main"
```

3. El scraper comenzará en la URL configurada y recorrerá todas las páginas disponibles.
>**Nota Importante**: Las URL que configures, deberan seguir el mismo formato que esta en los archivos de ejemplo en /resources.

# Consideraciones Importantes
* El scraping debe respetar los términos del sitio.
* Puede requerir modificar headers y delays para evitar bloqueos.
* La estructura HTML del sitio puede cambiar sin previo aviso.

# Proximas mejoras
* Terminar CarrefourScraper, JumboScraper, LaAnonimaScraper
* Scraping concurrente para acelarar tiempos
* Retries automaticos ante fallas de red
* Cache local para evitar repeticion de requests.


