package co.edu.javeriana.easymarket.ordersservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
public class OrderStatusId implements Serializable {
    @Column(name = "status", nullable = false, length = 45)
    private String status;

    @Column(name = "id_order", nullable = false)
    private Integer idOrder;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderStatusId entity = (OrderStatusId) o;
        return Objects.equals(this.idOrder, entity.idOrder) &&
                Objects.equals(this.status, entity.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOrder, status);
    }

    // Constructor
    public OrderStatusId(String status, Integer idOrder) {
        this.status = status;
        this.idOrder = idOrder;
    }

}