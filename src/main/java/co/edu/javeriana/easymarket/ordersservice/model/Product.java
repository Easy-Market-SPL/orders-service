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
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "code", nullable = false, length = 200)
    private String code;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "price", nullable = false)
    private Float price;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "img_url", length = 700)
    private String imgUrl;

}