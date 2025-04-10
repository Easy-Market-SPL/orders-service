package co.edu.javeriana.easymarket.ordersservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "email", nullable = false, length = 45)
    private String email;

    @Column(name = "fullname", nullable = false, length = 100)
    private String fullname;

    @Column(name = "username", nullable = false, length = 20)
    private String username;

    @Column(name = "rol", nullable = false, length = 45)
    private String rol;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

}