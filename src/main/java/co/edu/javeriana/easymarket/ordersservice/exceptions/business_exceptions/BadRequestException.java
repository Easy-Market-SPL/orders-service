package co.edu.javeriana.easymarket.ordersservice.exceptions.business_exceptions;

import co.edu.javeriana.easymarket.ordersservice.exceptions.BusinessException;

public class BadRequestException extends BusinessException {
    public BadRequestException(String message) {
        super(message);
    }
}
