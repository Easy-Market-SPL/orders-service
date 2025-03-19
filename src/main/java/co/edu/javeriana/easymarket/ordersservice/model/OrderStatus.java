package co.edu.javeriana.easymarket.ordersservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_status")
public class OrderStatus {
    @EmbeddedId
    private OrderStatusId id;

    @MapsId("idOrder")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_order", nullable = false)
    private Order idOrder;

    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @Column(name = "notes", length = 200)
    private String notes;

    public OrderStatus(OrderStatusId orderStatusId) {
        this.id = orderStatusId;
        this.startDate = Instant.now();
    }
}
