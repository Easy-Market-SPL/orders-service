package co.edu.javeriana.easymarket.ordersservice.repository;

import co.edu.javeriana.easymarket.ordersservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}