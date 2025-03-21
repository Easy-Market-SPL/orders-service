package co.edu.javeriana.easymarket.ordersservice.repository;

import co.edu.javeriana.easymarket.ordersservice.model.OrderStatus;
import co.edu.javeriana.easymarket.ordersservice.model.OrderStatusId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, OrderStatusId> {
}