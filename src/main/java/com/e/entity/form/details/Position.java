package com.e.entity.form.details;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class Position {

    @Column(name = "position")
    private String position;
    @Column(name = "price")
    private Double price;
    @Column (name = "quantity")
    private int quantity;

    @Override
    public String toString() {
        return " "+position + " |кол-во " + quantity + " |цена "+ price + " |сумм " +(price*quantity);
    }

}

