package co.edu.javeriana.easymarket.ordersservice.dtos.utils;

import co.edu.javeriana.easymarket.ordersservice.exceptions.business_exceptions.UnauthorizedException;
import co.edu.javeriana.easymarket.ordersservice.exceptions.error_messages.LogicErrorMessages;

public record CreditPaymentDTO(Float paymentAmount) {
    public CreditPaymentDTO {
        if (paymentAmount == null || paymentAmount <= 0) {
            throw new UnauthorizedException(LogicErrorMessages.OrderErrorMessages.invalidPaymentAmount(String.valueOf(paymentAmount)));
        }
    }
}
