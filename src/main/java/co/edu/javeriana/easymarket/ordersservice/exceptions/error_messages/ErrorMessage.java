package co.edu.javeriana.easymarket.ordersservice.exceptions.error_messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {
    private String message;
    private Instant timestamp;
    private Integer status;
    private String reason;
}