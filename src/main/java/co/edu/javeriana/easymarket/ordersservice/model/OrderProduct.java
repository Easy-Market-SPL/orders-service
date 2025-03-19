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

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    public OrderProduct(Order order, Product product, Integer quantity){
        this.id = new OrderProductId(order.getId(), product.getCode());
        this.setIdOrder(order);
        this.setQuantity(quantity);
        this.setPrice(product.getPrice() * quantity);
        this.setDescription(product.getName());
    }
}
