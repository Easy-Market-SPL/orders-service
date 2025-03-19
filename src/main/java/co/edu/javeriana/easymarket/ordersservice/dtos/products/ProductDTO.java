package co.edu.javeriana.easymarket.ordersservice.dtos.products;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String code;
    private String name;
    private String description;
    private Float price;
    private String imgUrl;
}