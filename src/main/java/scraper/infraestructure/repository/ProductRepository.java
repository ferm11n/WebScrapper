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

            Map<String, Integer> existentes = new HashMap<>();
            for (Object[] row : rows) {
                existentes.put((String) row[1], (Integer) row[0]);
            }

            for (Product p : products) {

                if (!existentes.containsKey(p.getUrl())) {
                    // Nuevo producto
                    session.save(p);
                } else {
                    // Ya existe → actualizar
                    Product existing = session.get(Product.class, existentes.get(p.getUrl()));

                    existing.setCategoria(p.getCategoria());
                    existing.setPrice(p.getPrice());
                    existing.setSupermercado(p.getSupermercado());

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
                session.merge(existing);
            }

            tx.commit();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
