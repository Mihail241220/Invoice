package com.e.entity.form.details;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "receipt_details")
@Getter
@NoArgsConstructor
public class ReceiptDetails extends Position {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pk_id")
    long id;
    public ReceiptDetails(String position, Double price, int quantity){
        super(position, price, quantity);
    }
}
