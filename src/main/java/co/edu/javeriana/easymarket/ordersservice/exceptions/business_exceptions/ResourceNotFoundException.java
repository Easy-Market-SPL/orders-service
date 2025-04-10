package co.edu.javeriana.easymarket.ordersservice.exceptions.business_exceptions;

import co.edu.javeriana.easymarket.ordersservice.exceptions.BusinessException;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
