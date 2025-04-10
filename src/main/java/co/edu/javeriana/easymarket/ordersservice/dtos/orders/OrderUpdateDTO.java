package co.edu.javeriana.easymarket.ordersservice.dtos.orders;

import co.edu.javeriana.easymarket.ordersservice.exceptions.error_messages.LogicErrorMessages;

public record OrderUpdateDTO(String address) {
    public OrderUpdateDTO {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException(LogicErrorMessages.OrderErrorMessages.invalidDataArgument(address));

        }
    }
}
