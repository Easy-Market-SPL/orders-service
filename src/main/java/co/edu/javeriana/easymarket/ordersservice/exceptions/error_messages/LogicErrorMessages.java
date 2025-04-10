package co.edu.javeriana.easymarket.ordersservice.exceptions.error_messages;

public class LogicErrorMessages {
    private LogicErrorMessages() {
        // Prevent instantiation
    }

    public static final class OrderErrorMessages{
        private OrderErrorMessages() {
            // Prevent instantiation
        }

        ///  METHODS FOR TAKE ERROR MESSAGES
        public static String getOrderNotFoundMessage(String reviewId) {
            return String.format("Order with ID %s not found", reviewId);
        }

        public static String getProductNotFoundMessage(String reviewId) {
            return String.format("Product with ID %s not found", reviewId);
        }

        public static String getUserNotFoundMessage(String reviewId) {
            return String.format("User with ID %s not found", reviewId);
        }

        public static String errorChangeOrderStatus(String orderId, String wantedStatus, String status) {
            return String.format("It is not possible to change the status of the order %s from %s to %s", orderId, status, wantedStatus);
        }

        public static String invalidDataArgument(String argumentName){
            return String.format("Invalid data provided. %s cannot be null or blank", argumentName);
        }

        public static String invalidNumericArgument(String argumentName){
            return String.format("Invalid data provided. %s cannot be null or negative", argumentName);
        }

        public static String invalidProductInOrder (String productId, String orderId){
            return String.format("Invalid data provided. Product %s is not in the order %s", productId, orderId);
        }

        public static String invalidConfirm (){
            return "Invalid data provided. The order cannot be confirmed.";
        }

        public static String invalidCoordinate(String coordinate){
            return String.format("Invalid data provided. The coordinate %s cannot be out of range [-180, 180]", coordinate);
        }

        public static String invalidPaymentAmount(String amount){
            return String.format("Invalid data provided. The payment amount %s cannot be less than 0", amount);
        }
    }
}
