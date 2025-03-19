package co.edu.javeriana.easymarket.ordersservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusId implements Serializable {
    private static final long serialVersionUID = -6319354646823817466L;
    @Column(name = "id_order", nullable = false)
    private Integer idOrder;

    @Column(name = "status", nullable = false, length = 45)
    private String status;

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

}