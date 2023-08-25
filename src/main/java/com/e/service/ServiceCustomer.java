package com.e.service;

import com.e.dao.CustomerDAO;
import com.e.db.SessionUtil;
import com.e.entity.Customer;
import com.e.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.List;


public class ServiceCustomer extends SessionUtil implements CustomerDAO {
    @Override
    public void add(Customer customer) throws SQLException {
        //open session with a transaction
        Session session = openTransactionSession();
        session.persist(customer);
        //close session with a transaction
        closeTransactionSession();
    }

    @Override
    public List<Customer> getAll(User user) throws SQLException {

        Session session = openTransactionSession();
        Query<Customer> query = session.createQuery("from Customer where user = :user", Customer.class).setParameter("user", user);
        List<Customer> list = query.getResultList();
        closeTransactionSession();
        return list;
    }

    @Override
    public Customer getById(Long id) throws SQLException {

        Session session = openTransactionSession();
        Query<Customer> query = session.createQuery("from Customer where pk_id= :id", Customer.class)
                .setParameter("id", id);
        Customer customer = query.getSingleResult();
        customer.getUser().initializedPath();
        closeTransactionSession();
        return customer;
    }

    @Override
    public void update(Customer customer) throws SQLException {
        Session session = openTransactionSession();
        session.merge(customer);
        closeTransactionSession();
    }

    @Override
    public void remove(Customer customer) throws SQLException {
        Session session = openTransactionSession();
        session.remove(customer);
        closeTransactionSession();
    }


}
