package com.e.service;

import com.e.dao.ReceiptDAO;
import com.e.db.SessionUtil;
import com.e.entity.User;
import com.e.entity.form.Receipt;
import com.e.entity.form.details.ReceiptDetails;
import com.e.entity.form.details.Warehouse;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.List;


public class ServiceReceipt extends SessionUtil implements ReceiptDAO {

    @Override
    public void add(Receipt receipt) throws SQLException {
        Session session = openTransactionSession();
        session.persist(receipt);
        for (ReceiptDetails i : receipt.getPosition()) {
            if (session.createQuery("from Warehouse where position = :positionR", Warehouse.class)
                    .setParameter("positionR", i.getPosition()).uniqueResult() != null) {
                Query<Warehouse> query = session.createNativeQuery("update Warehouse set quantity= quantity-:quantityR where position = :positionR", Warehouse.class)
                        .setParameter("quantityR", i.getQuantity())
                        .setParameter("positionR", i.getPosition());
                int x = query.executeUpdate();

            } else {
                Warehouse warehouse = new Warehouse(i.getPosition(), i.getQuantity(), receipt.getInvoice()
                        .getCustomer()
                        .getUser());
                new ServiceWarehouse().add(warehouse);
            }
        }
        closeTransactionSession();
    }

    @Override
    public List<Receipt> getAll(User user) throws SQLException {
        Session session = openTransactionSession();
        Query<Receipt> query = session.createQuery("from Receipt as r" +
                " left join Invoice as i on i.id = r.invoice" +
                " left join Customer as c on c.id = i.customer where c.user = :user", Receipt.class).setParameter("user", user);
        List<Receipt> list = query.getResultList();
        closeTransactionSession();
        return list;
    }

    @Override
    public Receipt getById(Long id) throws SQLException {
        Session session = openTransactionSession();
        Query<Receipt> query = session.createQuery("from Receipt where id = :id", Receipt.class).setParameter("id", id);
        Receipt receipt = query.getSingleResult();
        closeTransactionSession();
        return receipt;
    }

    @Override
    public void update(Receipt receipt) throws SQLException {
        remove(receipt);
        add(receipt);
    }

    @Override
    public void remove(Receipt receipt) throws SQLException {
        Session session = openTransactionSession();
        session.remove(receipt);
        for (ReceiptDetails i : receipt.getPosition()) {
            session.createNativeQuery("update Warehouse set quantity= quantity+:quantityR where position = :positionR", Warehouse.class)
                    .setParameter("quantityR", i.getQuantity())
                    .setParameter("positionR", i.getPosition());
        }
        closeTransactionSession();
    }
}
