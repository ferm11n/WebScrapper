package scraper.infraestructure.repository;

import scraper.domain.model.Product;
import scraper.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ProductRepository {

    // Guardar lista de productos sin duplicados por URL
    public void saveProducts(List<Product> products) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            for (Product p : products) {
                // Verificar si ya existe por URL
                Query<Product> query = session.createQuery(
                    "FROM Product WHERE url = :url", Product.class);
                query.setParameter("url", p.getUrl());
                Product existing = query.uniqueResult();

                if (existing == null) {
                    session.save(p);
                } else {
                    //actualizar categoría o precio si cambió
                    existing.setCategoria(p.getCategoria());
                    existing.setPrice(p.getPrice());
                    session.update(existing);
                }
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    // Guardar producto individual sin duplicados
    public void save(Product p) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Query<Product> query = session.createQuery(
                "FROM Product WHERE url = :url", Product.class);
            query.setParameter("url", p.getUrl());
            Product existing = query.uniqueResult();

            if (existing == null) {
                session.save(p);
            } else {
                //actualizar categoría o precio si cambió
                existing.setCategoria(p.getCategoria());
                existing.setPrice(p.getPrice());
                session.update(existing);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
