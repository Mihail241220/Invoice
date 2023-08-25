package com.e.entity.form.details;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "invoice_details")
@Getter
@NoArgsConstructor

public class InvoiceDetails extends Position {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pk_id")
    long id;

   public InvoiceDetails(String position, Double price, int quantity){
       super(position, price, quantity);
   }
}
