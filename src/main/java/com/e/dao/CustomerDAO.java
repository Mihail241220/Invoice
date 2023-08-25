package com.e.dao;

import com.e.entity.Customer;
import com.e.entity.User;

import java.sql.SQLException;
import java.util.List;

public interface CustomerDAO {
    //create
    void add(Customer customer) throws SQLException;

    //read
    List<Customer> getAll(User user) throws SQLException;

    Customer getById(Long id) throws SQLException;

    //update
    void update(Customer customer) throws SQLException;

    //delete
    void remove(Customer customer) throws SQLException;

}
