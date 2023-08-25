package com.e.entity.form;

import com.e.entity.Customer;
import com.e.entity.User;
import com.e.entity.form.details.InvoiceDetails;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@Entity
@Table(name = "invoice")
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue
    @Column(name = "pk_id")
    private int id;
    @Column(name = "number")
    private int number;
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "amount")
    private double amount;
    @Column(name = "delivery")
    private String delivery;
    @Column(name = "payment")
    private boolean payment;
    @Column(name = "comment")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_customer")
    private Customer customer;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_invoice")
    private List<InvoiceDetails> position = new ArrayList<>();

    public void createSample(User user) {
        XlsxInvoice xlsx = new XlsxInvoice(user);
        xlsx.createSample();
        PdfInvoice pdf = new PdfInvoice(user);
        pdf.createSample();
    }

    public Invoice create(Customer customer, List<InvoiceDetails> position) {
        this.customer = customer;
        this.position.addAll(position);
        Form invoice = new XlsxInvoice(customer.getUser());
        invoice.create(customer, position);
        invoice = new PdfInvoice(customer.getUser());
        invoice.create(customer, position);
        customer.getUser()
                .setLastInvoice(customer.getUser().getLastInvoice() + 1);
        this.date = LocalDate.of(invoice.yearNum, invoice.monthNum, invoice.day);
        number = invoice.getNumber();
        amount = invoice.getSumAll();
        return this;
    }

    public Invoice create(Customer customer, List<InvoiceDetails> position, Calendar date, int number) {
        this.customer = customer;
        this.position.addAll(position);
        Form invoice = new XlsxInvoice(customer.getUser(), date, number);
        invoice.create(customer, position);
        invoice = new PdfInvoice(customer.getUser(), date, number);
        invoice.create(customer, position);
        customer.getUser()
                .setLastInvoice(customer.getUser().getLastInvoice() + 1);
        this.date = LocalDate.of(invoice.yearNum, invoice.monthNum, invoice.day);
        this.number = invoice.getNumber();
        amount = invoice.getSumAll();
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.SortToString());
        for (InvoiceDetails i : position) {
            sb.append("\n").append(i);
        }
        sb.append(payment ? "\n Оплачен" : "\n Не оплачен")
                .append("\n Доставка ")
                .append(delivery == null ? "-" : delivery)
                .append("\n Comment: ")
                .append(comment == null ? "-" : comment)
                .append("_______________________________________________________________________");
        return sb.toString();
    }

    public String SortToString() {
        return String.format("""
                Счет № %d от %s
                для компании %s
                на сумму %f
                 """, number, date.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")), customer.getTitle(), amount);
    }
}
