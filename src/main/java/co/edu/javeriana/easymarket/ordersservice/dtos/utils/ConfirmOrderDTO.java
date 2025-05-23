package co.edu.javeriana.easymarket.ordersservice.dtos.utils;

import co.edu.javeriana.easymarket.ordersservice.exceptions.business_exceptions.UnauthorizedException;
import co.edu.javeriana.easymarket.ordersservice.exceptions.error_messages.LogicErrorMessages;

public record ConfirmOrderDTO (Integer shippingCost , Float paymentAmount, String confirmationDate) {
    public ConfirmOrderDTO {
        if (shippingCost == null || shippingCost < 0) {
            throw new UnauthorizedException(LogicErrorMessages.OrderErrorMessages.invalidNumericArgument(String.valueOf(shippingCost)));
        }

        if (paymentAmount == null || paymentAmount < 0) {
            throw new UnauthorizedException(
                    LogicErrorMessages.OrderErrorMessages.invalidPaymentAmount(String.valueOf(paymentAmount))
            );
        }

        if (confirmationDate == null || confirmationDate.isBlank()) {
            throw new UnauthorizedException(LogicErrorMessages.OrderErrorMessages.invalidDataArgument("confirmationDate"));
        }
    }
}
