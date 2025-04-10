package co.edu.javeriana.easymarket.ordersservice.dtos;

import co.edu.javeriana.easymarket.ordersservice.exceptions.error_messages.LogicErrorMessages;

public record AddOrderProductDTO (Integer quantity) {
    public AddOrderProductDTO {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(LogicErrorMessages.OrderErrorMessages.invalidNumericArgument(String.valueOf(quantity)));
        }
    }
}


