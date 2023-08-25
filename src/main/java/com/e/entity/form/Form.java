package com.e.entity.form;

import com.e.entity.Customer;
import com.e.entity.User;
import com.e.entity.form.details.InvoiceDetails;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

@Getter
@Setter
public abstract class Form {

    protected int yearNum;
    protected String month;
    protected int monthNum;
    protected int day;
    protected int number;
    @Setter(AccessLevel.NONE)
    protected String InvoiceDir;

    @Setter(AccessLevel.NONE)
    protected Double sumAll = 0.0;

    @Setter(AccessLevel.NONE)
    protected long millis;
    @Transient
    protected User user;

    Form(User user) {
        this.user = user;
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(ZoneId.systemDefault()));
        yearNum = calendar.get(Calendar.YEAR);
        number = user.getLastInvoice();
        try {
            if (!Files.exists(Path.of(user.getPath(User.INVOICE) + "\\" + yearNum))) {
                Files.createDirectory(Path.of(user.getPath(User.INVOICE) + "\\" + yearNum));
            }
            InvoiceDir = (user.getPath(User.INVOICE) + "\\" + yearNum + "\\" + number);
            if (!Files.exists(Path.of(InvoiceDir))) {
                Files.createDirectory(Path.of(InvoiceDir));
            }
        } catch (IOException e) {
            System.out.println("Ошибка создания папки");
            e.printStackTrace();
        }
        monthNum = calendar.get(Calendar.MONTH);
        month = Month.get(monthNum);
        day = calendar.get(Calendar.DATE);
        millis = calendar.getTimeInMillis();

    }

    Form(User user, Calendar date, int number) {
        this.user = user;
        yearNum = date.get(Calendar.YEAR);
        try {
            if (!Files.exists(Path.of(user.getPath(User.INVOICE) + "\\" + yearNum))) {
                Files.createDirectory(Path.of(user.getPath(User.INVOICE) + "\\" + yearNum));
            }
            InvoiceDir = (user.getPath(User.INVOICE) + "\\" + yearNum + "\\" + number);
            if (!Files.exists(Path.of(InvoiceDir))) {
                Files.createDirectory(Path.of(InvoiceDir));
            }
        } catch (IOException e) {
            System.out.println("Ошибка создания папки");
            e.printStackTrace();
        }
        monthNum = date.get(Calendar.MONTH);
        month = Month.get(monthNum);
        day = date.get(Calendar.DATE);
        millis = date.getTimeInMillis();
    }

    public abstract void createSample();

    public abstract void create(Customer partner, List<InvoiceDetails> positions);
}
