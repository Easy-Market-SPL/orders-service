package co.edu.javeriana.easymarket.ordersservice.exceptions.business_exceptions;

import co.edu.javeriana.easymarket.ordersservice.exceptions.BusinessException;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super(message);
    }
}