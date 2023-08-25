package com.e.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "customer")
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)

public class Customer extends LegalCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pk_id")
    int pk_id;
    @Column(name = "actual_region")
    private String actual_region;
    @Column(name = "actual_city")
    private String actual_city;
    @Column(name = "telephone")
    private String telephone;
    @Column(name = "email")
    private String email;
    @Column(name = "comment")
    private String comment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user")
    private User user;

    public Customer(User u) {
        user = u;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer customer = (Customer) object;
        if (getTitle() != null ? !getTitle().equals(customer.getTitle()) : customer.getTitle() != null) {
            return false;
        }
        return getInn() != null ? !getInn().equals(customer.getInn()) : customer.getInn() != null;
    }

    public String toString() {
        return """ 
                %s
                12. Актуальный регион: %s
                13. Актуальный город: %s
                14. Телефон: %s
                15. Email: %s
                16. Комментарий: %s    
                id:%d  
                _______________________________________________________________________    
                """.formatted(super.toString(), actual_region, actual_city, telephone, email, comment, pk_id);
    }

    @Override
    public int hashCode() {
        return 31 * Integer.getInteger(getInn()) + getTitle().hashCode();
    }

}



