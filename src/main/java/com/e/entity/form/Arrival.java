package com.e.entity.form;

import com.e.entity.User;
import com.e.entity.form.details.ArrivalDetails;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Table (name = "arrival")
@NoArgsConstructor
@Getter
@Setter
public class Arrival extends AReceipt{

    @Column(name = "provider")
    private String provider;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_arrival")
    private List<ArrivalDetails> position;

    @ManyToOne
    @JoinColumn(name = "fk_user")
    private User user;

    public Arrival ( String number, List<ArrivalDetails> position, int year, int month,int day, User user, boolean original, String comment) {
        super(number, year, month, day, original, comment);
        this.position = position;
        this.user = user;
        for (ArrivalDetails i : position){
            super.setAmount(getAmount()+i.getPrice());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString())
                .append("\n Позиции :\n");
        for (ArrivalDetails i : position) {
            sb.append("\n").append(i);
        }
        sb.append("\n Сумма ").append(super.getAmount())
                .append(isOriginal() ? "\n Есть оригинал" : "\n Нет оригинала")
                .append("\n Comment: ")
                .append(getComment() == null ? "\n -  \n" : getComment());

        return sb.toString();
    }

}
