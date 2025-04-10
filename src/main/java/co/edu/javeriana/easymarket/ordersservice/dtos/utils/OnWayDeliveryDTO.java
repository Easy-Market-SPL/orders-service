package co.edu.javeriana.easymarket.ordersservice.dtos.utils;

import co.edu.javeriana.easymarket.ordersservice.exceptions.error_messages.LogicErrorMessages;

public record OnWayDeliveryDTO(String idDomiciliary, Float initialLatitude, Float initialLongitude) {
    public OnWayDeliveryDTO {
        if (idDomiciliary == null || idDomiciliary.isBlank()) {
            throw new IllegalArgumentException(LogicErrorMessages.OrderErrorMessages.invalidDataArgument(idDomiciliary));
        }

        if (initialLatitude == null || initialLatitude < -90 || initialLatitude > 90) {
            throw new IllegalArgumentException(LogicErrorMessages.OrderErrorMessages.invalidCoordinate(String.valueOf(initialLatitude)));
        }

        if (initialLongitude == null || initialLongitude < -180 || initialLongitude > 180) {
            throw new IllegalArgumentException(LogicErrorMessages.OrderErrorMessages.invalidCoordinate(String.valueOf(initialLongitude)));
        }
    }
}
