package co.edu.javeriana.easymarket.ordersservice.model;

import co.edu.javeriana.easymarket.ordersservice.dtos.orders.OrderCreateDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"order\"")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order", nullable = false)
    private Integer id;

    @Column(name = "id_user", nullable = false, length = 36)
    private String idUser;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(name = "total", nullable = false)
    private Float total;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "shipping_cost")
    private Integer shippingCost;

    @Column(name = "transport_company", length = 45)
    private String transportCompany;

    @Column(name = "shipping_guide", length = 100)
    private String shippingGuide;

    @Column(name = "id_domiciliary", length = 36)
    private String idDomiciliary;

    // One-to-Many relationships
    @OneToMany(mappedBy = "idOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderProduct> orderProducts = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderStatus> orderStatuses = new LinkedHashSet<>();

    // Create order from OrderCreateDTO
    public Order (OrderCreateDTO orderDTO){
        this.idUser = orderDTO.idUser();
        this.creationDate = Instant.now();
        this.total = 0.0F;
        this.orderProducts = new LinkedHashSet<>();
        this.orderStatuses = new LinkedHashSet<>();
    }

    // Create a method for calculating the total of the order according to the products
    public void calculateTotal(){
        this.total = 0.0F;
        for (OrderProduct orderProduct : this.orderProducts) {
            this.total += orderProduct.getPrice();
        }
    }
}
