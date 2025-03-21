package co.edu.javeriana.easymarket.ordersservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "order_product")
public class OrderProduct {
    @SequenceGenerator(name = "order_product_id_gen", sequenceName = "order_id_order_seq", allocationSize = 1)
    @EmbeddedId
    private OrderProductId id;

    @MapsId("idOrder")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_order", nullable = false)
    private Order idOrder;

    @Column(name = "price", nullable = false)
    private Float price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public OrderProduct( Order order, Product product, int quantity) {
        this.id = new OrderProductId(order.getId(), product.getCode());
        this.idOrder = order;
        this.quantity = quantity;
        this.price = product.getPrice() * quantity;
    }
}