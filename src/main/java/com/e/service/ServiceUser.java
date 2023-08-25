package com.e.service;

import com.e.dao.UserDAO;
import com.e.db.SessionUtil;
import com.e.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.List;


public class ServiceUser extends SessionUtil implements UserDAO {
    @Override
    public void add(User user) throws SQLException {
        Session session = openTransactionSession();
        session.persist(user);
        closeTransactionSession();
    }

    @Override
    public List<User> getAll() throws SQLException {
        Session session = openTransactionSession();
        Query<User> query = session.createQuery("from User", User.class);
        List<User> list = query.getResultList();
        closeTransactionSession();
        return list;
    }

    @Override
    public User getByLogin(String id) throws SQLException {
        Session session = openTransactionSession();
        Query<User> query = session.createQuery("from User where login = :id", User.class)
                .setParameter("id", id);
        if (query.getSingleResult() == null) {
            throw new SQLException();
        }
        User user = query.getSingleResult();
        user.initializedPath();
        closeTransactionSession();
        return user;
    }

    @Override
    public void update(User user) throws SQLException {
        Session session = openTransactionSession();
        session.merge(user);
        closeTransactionSession();
    }

    @Override
    public void remove(User user) throws SQLException {
        Session session = openTransactionSession();
        session.remove(user);
        closeTransactionSession();
    }


}
