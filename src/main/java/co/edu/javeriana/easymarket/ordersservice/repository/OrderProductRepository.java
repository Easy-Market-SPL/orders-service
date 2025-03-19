package co.edu.javeriana.easymarket.ordersservice.repository;

import co.edu.javeriana.easymarket.ordersservice.model.OrderProduct;
import co.edu.javeriana.easymarket.ordersservice.model.OrderProductId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, OrderProductId> {
}
