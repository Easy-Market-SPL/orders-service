package co.edu.javeriana.easymarket.ordersservice.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperationException extends RuntimeException {
    private final Integer code;
    public OperationException(final Integer code, final String message) {
        super(message);
        this.code = code;
    }
}
