package com.e.entity.form.details;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "arrival_details")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArrivalDetails extends Position{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pk_id")
    long id;

}
