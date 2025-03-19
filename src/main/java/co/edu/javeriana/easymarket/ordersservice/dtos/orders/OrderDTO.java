package co.edu.javeriana.easymarket.ordersservice.dtos.orders;

import co.edu.javeriana.easymarket.ordersservice.model.Order;
import co.edu.javeriana.easymarket.ordersservice.model.OrderProduct;
import co.edu.javeriana.easymarket.ordersservice.model.OrderStatus;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO implements Serializable {
    Integer id;
    String idUser;
    Instant creationDate;
    Float total;
    String address;
    Integer shippingCost;
    String transportCompany;
    String shippingGuide;
    String idDomiciliary;
    HashMap<String, Integer> products;
    HashMap<String, Instant> status;

    // Create constructor for OrderDTO
    public OrderDTO(Order order) {
        this.id = order.getId();
        this.idUser = order.getIdUser();
        this.creationDate = order.getCreationDate();
        this.total = order.getTotal();
        this.address = order.getAddress();
        this.shippingCost = order.getShippingCost();
        this.transportCompany = order.getTransportCompany();
        this.shippingGuide = order.getShippingGuide();
        this.idDomiciliary = order.getIdDomiciliary();

        // Create hashmap for order products
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            this.products.put(orderProduct.getId().getProductCode(), orderProduct.getQuantity());
        }

        // Create hashmap for order statuses
        for (OrderStatus orderStatus : order.getOrderStatuses()) {
            this.status.put(orderStatus.getId().getStatus(), orderStatus.getStartDate());
        }
    }

}