package com.e.dao;

import com.e.entity.User;
import com.e.entity.form.Invoice;

import java.sql.SQLException;
import java.util.List;


public interface InvoiceDAO {
    void add(Invoice invoice) throws SQLException;

    //read
    List<Invoice> getAll(User user) throws SQLException;

    List<Invoice> getByNumber(User user, int number) throws SQLException;

    Invoice getById(Long id) throws SQLException;

    //update
    void update(Invoice invoice) throws SQLException;

    //delete
    void remove(Invoice invoice) throws SQLException;
}
