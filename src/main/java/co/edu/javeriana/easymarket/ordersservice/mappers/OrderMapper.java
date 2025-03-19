package co.edu.javeriana.easymarket.ordersservice.mappers;

import co.edu.javeriana.easymarket.ordersservice.dtos.orders.OrderDTO;
import co.edu.javeriana.easymarket.ordersservice.model.Order;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public OrderMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public OrderDTO orderToOrderDTO(Order order){
        return modelMapper.map(order, OrderDTO.class);
    }

    public Order orderDTOToOrder(OrderDTO orderDTO){
        return modelMapper.map(orderDTO, Order.class);
    }
}
