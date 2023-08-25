package com.e.entity.form.details;

import jakarta.persistence.*;
import com.e.entity.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Entity
@Inheritance
@Table(name = "warehouse")
public class Warehouse {
    @Id
    @Column(name = "position")
    String position;
    @Column(name = "quantity")
    private int quantity;
    @ManyToOne
    @JoinColumn(name = "fk_user")
    private User user;
    @Column(name = "date_minus")
    LocalDate dateMinus;

    private Warehouse() {
    }

    public Warehouse(String position, int quantity, User user) {
        this.position = position;
        this.quantity = quantity;
        this.user = user;
    }

    @Override
    public String toString() {
        if (dateMinus != null) {
            return String.format("%-20s | %5d шт.| %s ", position, quantity, dateMinus.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")));
        }
        return String.format("%-20s | %5d шт.|  ", position, quantity);
    }

}
