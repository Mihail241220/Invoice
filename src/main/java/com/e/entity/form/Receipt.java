package com.e.entity.form;

import com.e.entity.form.details.InvoiceDetails;
import com.e.entity.form.details.ReceiptDetails;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "receipt")
@Getter
@Setter
@NoArgsConstructor
public class Receipt extends AReceipt {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_receipt")
    List<ReceiptDetails> position;
    @ManyToOne
    @JoinColumn(name = "fk_invoice")
    Invoice invoice;

    public Receipt(Invoice invoice) {
        for (InvoiceDetails i : invoice.getPosition()) {
            position.add(new ReceiptDetails(i.getPosition(), i.getPrice(), i.getQuantity()));
        }
        this.invoice = invoice;
    }

    public Receipt(String number, List<ReceiptDetails> position, int year, int month, int day, Invoice invoice, boolean original, String comment) {
        super(number, year, month, day, original, comment);
        this.position = position;
        this.invoice = invoice;
        for (ReceiptDetails i : position) {
            super.setAmount(getAmount() + i.getPrice());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString())
                .append("\n Позиции :\n");
        for (ReceiptDetails i : position) {
            sb.append("\n").append(i);
        }
        sb.append("\n Сумма ").append(super.getAmount())
                .append(isOriginal() ? "\n Есть оригинал" : "\n Нет оригинала")
                .append("\n Comment: ")
                .append(getComment() == null ? "\n -  \n" : getComment());
        return sb.toString();
    }
}
