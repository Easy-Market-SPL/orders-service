package co.edu.javeriana.easymarket.ordersservice.dtos.utils;

import co.edu.javeriana.easymarket.ordersservice.exceptions.business_exceptions.UnauthorizedException;
import co.edu.javeriana.easymarket.ordersservice.exceptions.error_messages.LogicErrorMessages;

public record OnWayCompanyDTO(String transportCompany, String shippingGuide) {
    public OnWayCompanyDTO {
        if (transportCompany == null || transportCompany.isBlank()) {
            throw new UnauthorizedException(LogicErrorMessages.OrderErrorMessages.invalidDataArgument(transportCompany));
        }
        if (shippingGuide == null || shippingGuide.isBlank()) {
            throw new UnauthorizedException(LogicErrorMessages.OrderErrorMessages.invalidDataArgument(shippingGuide));
        }
    }
}
