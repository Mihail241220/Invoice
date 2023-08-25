package com.e.dao;

import com.e.entity.User;
import com.e.entity.form.Arrival;

import java.sql.SQLException;
import java.util.List;


public interface ArrivalDAO {
    //create
    void add(Arrival arrival) throws SQLException;

    //read
    List<Arrival> getAll(User user) throws SQLException;

    Arrival getById(int id) throws SQLException;

    //update
    void update(Arrival arrival) throws SQLException;

    //delete
    void remove(Arrival arrival) throws SQLException;
}
