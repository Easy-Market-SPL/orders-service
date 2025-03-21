package co.edu.javeriana.easymarket.ordersservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "order_status")
public class OrderStatus {
    @SequenceGenerator(name = "order_status_id_gen", sequenceName = "order_id_order_seq", allocationSize = 1)
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

    // Constructor
    public OrderStatus(OrderStatusId id, Order order) {
        this.id = id;
        this.idOrder = order;
        this.startDate = Instant.now();

        // TODO: Correct this, the end date of the status should be set when the status changes
        this.endDate = Instant.now();
    }

}