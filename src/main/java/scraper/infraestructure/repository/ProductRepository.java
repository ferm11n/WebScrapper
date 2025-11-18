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

    public void saveProducts(List<Product> products) {

        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();

            // Obtener URLs existentes
            Query<Object[]> q = session.createQuery(
                    "SELECT id, url FROM Product", Object[].class
            );

            List<Object[]> rows = q.list();

            Map<String, Long> existentes = new HashMap<>();
            for (Object[] row : rows) {
                Long id = (Long) row[0];
                String url = (String) row[1];
                existentes.put(url, id);
            }

            for (Product p : products) {

                if (!existentes.containsKey(p.getUrl())) {
                    // Nuevo producto
                    session.persist(p); // ← CAMBIO CRÍTICO
                } else {
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
                session.persist(p); // ← CAMBIO CRÍTICO
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
