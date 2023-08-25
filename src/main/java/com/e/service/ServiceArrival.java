package com.e.service;

import com.e.dao.ArrivalDAO;
import com.e.db.SessionUtil;
import com.e.entity.User;
import com.e.entity.form.Arrival;
import com.e.entity.form.details.ArrivalDetails;
import com.e.entity.form.details.Warehouse;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.List;


public class ServiceArrival extends SessionUtil implements ArrivalDAO {
    @Override
    public void add(Arrival arrival) throws SQLException {
        Session session = openTransactionSession();
        session.persist(arrival);

        for (ArrivalDetails i : arrival.getPosition()) {
            if (session.createQuery("from Warehouse where position = :positionA", Warehouse.class)
                    .setParameter("positionA", i.getPosition()).uniqueResult() != null) {
                Query<Warehouse> query = session.createNativeQuery("update Warehouse set quantity= quantity+:quantityA where position = :positionA", Warehouse.class)
                        .setParameter("quantityA", i.getQuantity())
                        .setParameter("positionA", i.getPosition());
                int x = query.executeUpdate();
            } else {
                Warehouse warehouse = new Warehouse(i.getPosition(), i.getQuantity(), arrival.getUser());
                new ServiceWarehouse().add(warehouse);
            }
        }
        closeTransactionSession();
    }

    @Override
    public List<Arrival> getAll(User user) throws SQLException {
        Session session = openTransactionSession();
        Query<Arrival> query = session.createQuery("from Arrival where user =:user", Arrival.class)
                .setParameter("user", user);
        List<Arrival> list = query.getResultList();
        closeTransactionSession();
        return list;
    }

    @Override
    public Arrival getById(int id) throws SQLException {
        Session session = openTransactionSession();
        Query<Arrival> query = session.createQuery("from Arrival where position = :id", Arrival.class)
                .setParameter("id", id);
        Arrival arrival = query.getSingleResult();
        closeTransactionSession();
        return arrival;
    }

    @Override
    public void update(Arrival arrival) throws SQLException {
        remove(arrival);
        add(arrival);
    }

    @Override
    public void remove(Arrival arrival) throws SQLException {
        Session session = openTransactionSession();
        session.remove(arrival);
        for (ArrivalDetails i : arrival.getPosition()) {
            session.createNativeQuery("update Warehouse set quantity= quantity-:quantityA where position = :positionA", Warehouse.class)
                    .setParameter("quantityA", i.getQuantity())
                    .setParameter("positionA", i.getPosition());
        }
        closeTransactionSession();
    }
}
