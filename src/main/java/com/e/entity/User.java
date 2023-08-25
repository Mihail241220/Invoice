package com.e.entity;

import com.e.entity.form.Invoice;
import com.e.entity.form.details.InvoiceDetails;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter

public class User extends LegalCard {
    final static public int PARTNERS = 0;
    final static public int INVOICE = 1;
    final static public int CONTRACTS = 2;
    final static public int STAMP1 = 3;
    final static public int STAMP2 = 4;

    @Column(name = "password")
    private String password;
    @Id
    @Column(name = "pk_login")
    private String login;
    @Column(name = "bottomtext")
    private String bottomText;
    @Transient
    private int year = 2000;
    @Column(name = "last_invoice")
    private int lastInvoice;
    @Transient
    private Customer customer;
    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    final private String[] PATHS = new String[5];

    public User() {
    }

    @Override
    public void searchData(String path) throws IOException {
        super.searchData(path);
        initializedPath();
        try {
            if (!Files.exists(Path.of(getTitle()))) {
                Files.createDirectory(Path.of(getTitle()));
                FileOutputStream fileOutputStream = new FileOutputStream(getTitle() + "\\"
                        + getTitle() + "." + path.substring(path.lastIndexOf(".") + 1));
                Files.copy(Path.of(path), fileOutputStream);
                fileOutputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Customer setCustomer(String path) throws IOException {
        customer = new Customer(this);
        customer.searchData(path);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(getPath(PARTNERS) + "\\"
                    + customer.getTitle() + "." + path.substring(path.lastIndexOf(".") + 1));

            Files.copy(Path.of(path), fileOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return customer;//TODO: создать factory
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer updateCustomer(Customer customer) {
        System.out.println(customer.toString());
        return customer;
    }

    /**
     * Saves stamps from paths String stamp1,String stamp2, in package whit User
     */
    public void addStamps(String stamp1, String stamp2) throws IOException {
        FileOutputStream stampStream = new FileOutputStream(getPath(STAMP1));
        Files.copy(Path.of(stamp1), stampStream);
        stampStream.close();
        stampStream = new FileOutputStream(getPath(STAMP2));
        Files.copy(Path.of(stamp2), stampStream);
        stampStream.close();
    }

    public void initializedPath() {
        if (PATHS[PARTNERS] == null || PATHS[PARTNERS].equals("null\\Partner")) {
            PATHS[PARTNERS] = getTitle() + "\\Partner";
            PATHS[INVOICE] = getTitle() + "\\Invoice";
            PATHS[CONTRACTS] = getTitle() + "\\Contract";
            PATHS[STAMP1] = getTitle() + "\\stamp1.png";
            PATHS[STAMP2] = getTitle() + "\\stamp2.png";

        }
        if (!Files.exists(Path.of(getTitle()))) {
            try {
                Files.createDirectory(Path.of(getTitle()));
                for (int i = 0; i < 3; i++) {
                    Files.createDirectory(Path.of(PATHS[i]));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Invoice createInvoice(List<InvoiceDetails> position) {
        if (!Files.exists(Path.of(getTitle() + "\\sample.xlsx"))) {
            Invoice invoice = new Invoice();
            invoice.createSample(this);
        }
        Invoice invoice = new Invoice();
        return invoice.create(customer, position);
    }

    public Invoice createInvoice(List<InvoiceDetails> position, Calendar date, int number) {
        if (!Files.exists(Path.of(getTitle() + "\\sample.xlsx"))) {
            Invoice invoice = new Invoice();
            invoice.createSample(this);
        }
        Invoice invoice = new Invoice();
        return invoice.create(customer, position, date, number);
    }

    public Invoice createInvoice(Customer customer, List<InvoiceDetails> position) {
        if (!Files.exists(Path.of(getTitle() + "\\sample.xlsx"))) {
            Invoice invoice = new Invoice();
            invoice.createSample(this);
        }
        this.customer = customer;
        Invoice invoice = new Invoice();
        invoice.create(customer, position);
        return invoice;
    }

    public String getPath(int dir) {
        return PATHS[dir];
    }

    @Override
    public String toString() {
        return """ 
                %s
                12. Логин: %s
                """.formatted(super.toString(), login);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof User)) {
            return false;
        }
        User user = (User) object;
        return user.getLogin() != null ? user.getLogin().equals(login) : login == null;
    }
}
