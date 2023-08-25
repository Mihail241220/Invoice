package com.e.service;

import com.e.dao.WarehouseDAO;
import com.e.db.SessionUtil;
import com.e.entity.User;
import com.e.entity.form.details.Warehouse;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.List;


public class ServiceWarehouse extends SessionUtil implements WarehouseDAO {
    @Override
    public List<Warehouse> getAll(User user) throws SQLException {
        Session session = openTransactionSession();
        Query<Warehouse> query = session.createQuery("from Warehouse where user = :user", Warehouse.class).setParameter("user", user);
        List<Warehouse> list = query.getResultList();
        closeTransactionSession();
        return list;
    }

    @Override
    public void add(Warehouse warehouse) throws SQLException {
        Session session = openTransactionSession();
        session.persist(warehouse);
        closeTransactionSession();
    }

    @Override
    public Warehouse getById(String position) throws SQLException {
        Session session = openTransactionSession();
        Query<Warehouse> query = session.createQuery("from Warehouse where position = :id", Warehouse.class).setParameter("id", position);
        Warehouse warehouse = query.getSingleResult();
        closeTransactionSession();
        return warehouse;
    }

    @Override
    public void update(Warehouse warehouse) throws SQLException {
        Session session = openTransactionSession();
        session.merge(warehouse);
        closeTransactionSession();

    }

    @Override
    public void remove(Warehouse warehouse) throws SQLException {
        Session session = openTransactionSession();
        session.remove(warehouse);
        closeTransactionSession();
    }

    public List<Warehouse> getArrivalList(User user) throws SQLException {
        Session session = openTransactionSession();
        Query<Warehouse> query = session.createQuery("from Warehouse where user = :user and quantity < 0", Warehouse.class).setParameter("user", user);
        List<Warehouse> list = query.getResultList();
        closeTransactionSession();
        return list;
    }
}
