package co.edu.javeriana.easymarket.ordersservice.exceptions;

public abstract class BusinessException extends RuntimeException{
    public BusinessException(String message) {
        super(message);
    }
}