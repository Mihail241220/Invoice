package com.e.entity.form;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;



@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class AReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pk_id")
    @Setter(AccessLevel.NONE)
    int id;
    @Column(name = "number")
    private String number;
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "amount")
    private double amount;
    @Column(name = "original")
    private boolean original;
    private @Column(name = "comment")
    String comment;

    public AReceipt(String number, int year, int month, int day, boolean original, String comment) {
        this.number = number;
        this.date = LocalDate.of(year, month, day);
        this.original = original;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return String.format("\nТН № %d от %s",number,date.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")));
    }
}


