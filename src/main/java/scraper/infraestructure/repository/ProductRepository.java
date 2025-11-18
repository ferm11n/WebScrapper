package scraper.infraestructure.repository;

import scraper.domain.model.Product;
import scraper.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductRepository {

    /**
     * Guarda una lista de productos evitando duplicados por URL.
     * Optimizado: carga todas las URLs existentes en una sola query.
     */
    public void saveProducts(List<Product> products) {

        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();

            // Obtener URLs existentes (para evitar una query por producto)
            Query<Object[]> q = session.createQuery(
                    "SELECT id, url FROM Product", Object[].class
            );
            List<Object[]> rows = q.list();

            // Cambiado a Long porque Product.id es Long
            Map<String, Long> existentes = new HashMap<>();
            for (Object[] row : rows) {
                Long id = (Long) row[0];
                String url = (String) row[1];
                existentes.put(url, id);
            }

            for (Product p : products) {

                if (!existentes.containsKey(p.getUrl())) {
                    // Nuevo producto
                    session.save(p);
                } else {
                    // Ya existe → actualizar
                    Long existingId = existentes.get(p.getUrl());
                    Product existing = session.get(Product.class, existingId);

                    existing.setCategoria(p.getCategoria());
                    existing.setPrice(p.getPrice());
                    existing.setSupermercado(p.getSupermercado());
                    existing.setTitle(p.getTitle());

                    session.merge(existing);
                }
            }

            tx.commit();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Guardar un producto individual sin duplicados
     */
    public void save(Product p) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();

            Query<Product> q = session.createQuery(
                    "FROM Product WHERE url = :url", Product.class
            );
            q.setParameter("url", p.getUrl());

            Product existing = q.uniqueResult();

            if (existing == null) {
                session.save(p);
            } else {
                existing.setCategoria(p.getCategoria());
                existing.setPrice(p.getPrice());
                existing.setSupermercado(p.getSupermercado());
                existing.setTitle(p.getTitle());
                session.merge(existing);
            }

            tx.commit();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
