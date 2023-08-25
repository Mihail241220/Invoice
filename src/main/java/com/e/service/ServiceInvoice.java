package com.e.service;

import com.e.dao.InvoiceDAO;
import com.e.db.SessionUtil;
import com.e.entity.User;
import com.e.entity.form.Invoice;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.List;


public class ServiceInvoice extends SessionUtil implements InvoiceDAO {
    @Override
    public void add(Invoice invoice) throws SQLException {
        Session session = openTransactionSession();
        session.persist(invoice);
        closeTransactionSession();
    }

    @Override
    public List<Invoice> getAll(User user) throws SQLException {
        Session session = openTransactionSession();
        Query<Invoice> query = session.createQuery("from Invoice as i" +
                " left join Customer  as c on c.id = i.customer where c.user = :user", Invoice.class).setParameter("user", user);
        List<Invoice> list = query.getResultList();
        closeTransactionSession();
        return list;
    }

    @Override
    public List<Invoice> getByNumber(User user, int number) throws SQLException {
        Session session = openTransactionSession();
        Query<Invoice> query = session.createQuery("from Invoice as i where i.number = :number" +
                        " left join Customer  as c on c.id = i.customer where c.user = :user  ", Invoice.class)
                .setParameter("user", user)
                .setParameter("number", number);
        List<Invoice> list = query.getResultList();
        closeTransactionSession();
        return list;
    }

    @Override
    public Invoice getById(Long id) throws SQLException {
        Session session = openTransactionSession();
        Query<Invoice> query = session.createQuery("from Invoice where id = :id", Invoice.class).setParameter("id", id);
        Invoice invoice = query.getSingleResult();
        closeTransactionSession();
        return invoice;
    }

    @Override
    public void update(Invoice invoice) throws SQLException {
        Session session = openTransactionSession();
        session.merge(invoice);
        closeTransactionSession();
    }

    @Override
    public void remove(Invoice invoice) throws SQLException {
        Session session = openTransactionSession();
        session.remove(invoice);
        closeTransactionSession();
    }
}
