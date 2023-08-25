package com.e.dao;

import com.e.entity.User;
import com.e.entity.form.details.Warehouse;

import java.sql.SQLException;
import java.util.List;


public interface WarehouseDAO {
    //read
    List<Warehouse> getAll(User user) throws SQLException;

    //create
    void add(Warehouse warehouse) throws SQLException;

    Warehouse getById(String position) throws SQLException;

    //update
    void update(Warehouse warehouse) throws SQLException;

    //delete
    void remove(Warehouse warehouse) throws SQLException;

    List<Warehouse> getArrivalList(User user) throws SQLException;
}
