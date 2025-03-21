package co.edu.javeriana.easymarket.ordersservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class OrderProductId implements Serializable {
    @Column(name = "id_order", nullable = false)
    private Integer idOrder;

    @Column(name = "product_code", nullable = false, length = 200)
    private String productCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderProductId entity = (OrderProductId) o;
        return Objects.equals(this.idOrder, entity.idOrder) &&
                Objects.equals(this.productCode, entity.productCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOrder, productCode);
    }

    // Constructor
    public OrderProductId() {
    }

    // Constructor
    public OrderProductId(Integer idOrder, String productCode) {
        this.idOrder = idOrder;
        this.productCode = productCode;
    }
}