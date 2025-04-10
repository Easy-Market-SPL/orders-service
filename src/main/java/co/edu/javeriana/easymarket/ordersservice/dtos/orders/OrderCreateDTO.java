package co.edu.javeriana.easymarket.ordersservice.dtos.orders;

import co.edu.javeriana.easymarket.ordersservice.exceptions.error_messages.LogicErrorMessages;

public record OrderCreateDTO (String idUser, String address){
    public OrderCreateDTO {
        if (idUser == null || idUser.isBlank()) {
            throw new IllegalArgumentException(LogicErrorMessages.OrderErrorMessages.invalidDataArgument(idUser));
        }
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException(LogicErrorMessages.OrderErrorMessages.invalidDataArgument(address));
        }
    }
}
