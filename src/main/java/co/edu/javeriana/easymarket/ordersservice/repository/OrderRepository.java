package co.edu.javeriana.easymarket.ordersservice.repository;

import co.edu.javeriana.easymarket.ordersservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}