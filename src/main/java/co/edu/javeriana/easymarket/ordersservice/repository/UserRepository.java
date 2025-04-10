package co.edu.javeriana.easymarket.ordersservice.repository;

import co.edu.javeriana.easymarket.ordersservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}