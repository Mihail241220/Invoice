package com.e.db;

import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

public class HibernateUtil {
    private @Getter
    static final SessionFactory sessionFactory = builderSessionFactory();

    private static SessionFactory builderSessionFactory() {
        try {
            return new Configuration().configure(new File("...src/main/resources/hibernate.cfg.xml")).buildSessionFactory();
        } catch (Throwable ex) {

            throw new ExceptionInInitializerError();
        }
    }

    public static void close() {
        sessionFactory.close();
    }
}
