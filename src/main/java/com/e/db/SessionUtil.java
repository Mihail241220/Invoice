package com.e.db;

import org.hibernate.Session;
import org.hibernate.Transaction;


public class SessionUtil {

    private Session session;
    private Transaction transaction;

    protected Session getSession() {
        return session;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    protected Session openSession() {
        return HibernateUtil.getSessionFactory().openSession();
    }

    protected Session openTransactionSession() {
        session = openSession();
        transaction = session.beginTransaction();
        return session;
    }

    protected void closeSession() {
        session.close();
    }

    protected void closeTransactionSession() {
        transaction.commit();
        closeSession();
    }
}

