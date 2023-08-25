package com.e.dao;

import com.e.entity.User;
import com.e.entity.form.Receipt;

import java.sql.SQLException;
import java.util.List;


public interface ReceiptDAO {
    void add(Receipt receipt) throws SQLException;

    //read
    List<Receipt> getAll(User user) throws SQLException;

    Receipt getById(Long id) throws SQLException;

    //update
    void update(Receipt receipt) throws SQLException;

    //delete
    void remove(Receipt receipt) throws SQLException;
}
