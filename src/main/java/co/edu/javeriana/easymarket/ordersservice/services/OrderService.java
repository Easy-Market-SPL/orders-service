package co.edu.javeriana.easymarket.ordersservice.services;

import co.edu.javeriana.easymarket.ordersservice.dtos.orders.OrderCreateDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.orders.OrderDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.orders.OrderUpdateDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.utils.CreditPaymentDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.utils.OnWayDeliveryDTO;
import co.edu.javeriana.easymarket.ordersservice.exceptions.business_exceptions.ResourceNotFoundException;
import co.edu.javeriana.easymarket.ordersservice.exceptions.business_exceptions.UnauthorizedException;
import co.edu.javeriana.easymarket.ordersservice.exceptions.error_messages.LogicErrorMessages;
import co.edu.javeriana.easymarket.ordersservice.model.*;
import co.edu.javeriana.easymarket.ordersservice.repository.*;
import co.edu.javeriana.easymarket.ordersservice.services.helpers.ValidationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {

    // Repositories
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Validation Service
    private final ValidationService validatorService;

    /// GET ALL ORDERS
    public List<OrderDTO> getOrders(){
        return orderRepository.findAll()
                .stream()
                .map(OrderDTO::new)
                .collect(Collectors.toList());
    }

    /// CREATE AN ORDER
    public OrderDTO createOrder(OrderCreateDTO orderDTO){
        // Validate user
        validatorService.validateExists(userRepository.findById(orderDTO.idUser()), LogicErrorMessages.OrderErrorMessages.getUserNotFoundMessage(orderDTO.idUser()));

        Order order = new Order(orderDTO);
        return new OrderDTO(orderRepository.save(order));
    }

    ///  GET ORDERS BY USER
    public List<OrderDTO> getOrdersByUser(String idUser) {
        return orderRepository.findAll()
                .stream()
                .filter(order -> order.getIdUser().equals(idUser))
                .map(OrderDTO::new)
                .collect(Collectors.toList());
    }

    /// GET A SPECIFIC ORDER BY ID
    public OrderDTO getOrderById(int id){
        validatorService.validateExists(orderRepository.findById(id), LogicErrorMessages.OrderErrorMessages.getOrderNotFoundMessage(String.valueOf(id)));

        Order order = orderRepository.findById(id).orElseThrow();
        return new OrderDTO(order);
    }


    /// UPDATE AN ORDER (ONLY ADDRESS)
    public OrderDTO updateOrder(int id, OrderUpdateDTO orderDTO) {
        validatorService.validateExists(orderRepository.findById(id), LogicErrorMessages.OrderErrorMessages.getOrderNotFoundMessage(String.valueOf(id)));

        Order order = orderRepository.findById(id).orElseThrow();
        order.setAddress(orderDTO.address());
        return new OrderDTO(orderRepository.save(order));

    }

    /// UPDATE STATUS OF ORDER
    /// UPDATE STATUS OF ORDER TO CONFIRM
    public OrderDTO confirmOrder(int id, Integer shippingCost, Float paymentAmount) throws UnauthorizedException {
        validatorService.validateExists(orderRepository.findById(id), LogicErrorMessages.OrderErrorMessages.getOrderNotFoundMessage(String.valueOf(id)));
        Order order = orderRepository.findById(id).orElseThrow();

        // CHECK IF THE STATUSES ARE EMPTY
        if (!order.getOrderStatuses().isEmpty()) {
            throw new UnauthorizedException(LogicErrorMessages.OrderErrorMessages.invalidConfirm());
        }

        OrderStatusId orderStatusId = new OrderStatusId("confirmed", order.getId());
        OrderStatus orderStatus = new OrderStatus(orderStatusId, order);
        order.getOrderStatuses().add(orderStatus);

        orderStatusRepository.save(orderStatus);

        order.setShippingCost(shippingCost);
        float total = (order.getTotal() + shippingCost);
        order.setTotal(total);

        order.setDebt(total - paymentAmount);

        return new OrderDTO(orderRepository.save(order));
    }

    /// UPDATE STATUS TO PREPARING
    public OrderDTO prepareOrder(int id) throws UnauthorizedException {
        validatorService.validateExists(orderRepository.findById(id), LogicErrorMessages.OrderErrorMessages.getOrderNotFoundMessage(String.valueOf(id)));
        Order order = orderRepository.findById(id).orElseThrow();

        // CHECK IF THE LAST STATUS IS CONFIRMED
        if (order.getOrderStatuses().isEmpty() || order.getOrderStatuses().stream().noneMatch(orderStatus -> orderStatus.getId().getStatus().equals("confirmed"))) {
            // Take the value of last status
            String lastStatus = order.getOrderStatuses().stream()
                    .map(OrderStatus::getId)
                    .map(OrderStatusId::getStatus)
                    .reduce((first, second) -> second)
                    .orElse("none");

            throw new UnauthorizedException(LogicErrorMessages.OrderErrorMessages.errorChangeOrderStatus(String.valueOf(id), "preparing", lastStatus));
        }

        OrderStatus orderStatus = new OrderStatus(new OrderStatusId("preparing", order.getId()), order);
        order.getOrderStatuses().add(orderStatus);
        orderStatusRepository.save(orderStatus);

        order.getOrderStatuses().stream()
                .filter(previosOrderStatus -> previosOrderStatus.getId().getStatus().equals("confirmed"))
                .findFirst()
                .ifPresent(previosOrderStatus -> previosOrderStatus.setEndDate(orderStatus.getStartDate()));

        return new OrderDTO(orderRepository.save(order));

    }
    /// UPDATE STATUS TO ON THE WAY (DOMICILIARY)
    public OrderDTO onTheWayDomiciliaryOrder(int id, OnWayDeliveryDTO delivery) {
        validatorService.validateExists(orderRepository.findById(id), LogicErrorMessages.OrderErrorMessages.getOrderNotFoundMessage(String.valueOf(id)));
        Order order = orderRepository.findById(id).orElseThrow();

        orderStatusOnTheWay(order);
        order.setIdDomiciliary(delivery.idDomiciliary());
        order.setLat(BigDecimal.valueOf(delivery.initialLatitude()));
        order.setLng(BigDecimal.valueOf(delivery.initialLongitude()));

        return new OrderDTO(orderRepository.save(order));

    }
    /// UPDATE STATUS TO ON THE WAY (TRANSPORT COMPANY)
    public OrderDTO onTheWayTransportCompanyOrder(int id, String transportCompany, String shippingGuide) {
        validatorService.validateExists(orderRepository.findById(id), LogicErrorMessages.OrderErrorMessages.getOrderNotFoundMessage(String.valueOf(id)));
        Order order = orderRepository.findById(id).orElseThrow();

        orderStatusOnTheWay(order);

        order.setTransportCompany(transportCompany);
        order.setShippingGuide(shippingGuide);

        return new OrderDTO(orderRepository.save(order));
    }

    ///  AUX METHOD FOR AVOID REPEATED CODE ON UPDATE STATUS TO ON THE WAY
    private void orderStatusOnTheWay(Order order) {
        // CHECK IF THE LAST STATUS IS PREPARING
        if (order.getOrderStatuses().isEmpty() || order.getOrderStatuses().stream().noneMatch(orderStatus -> orderStatus.getId().getStatus().equals("preparing"))) {
            String lastStatus = order.getOrderStatuses().stream()
                    .map(OrderStatus::getId)
                    .map(OrderStatusId::getStatus)
                    .reduce((first, second) -> second)
                    .orElse("none");
            throw new UnauthorizedException(LogicErrorMessages.OrderErrorMessages.errorChangeOrderStatus(String.valueOf(order.getId()), "on the way", lastStatus));
        }

        OrderStatus orderStatus = new OrderStatus(new OrderStatusId("on the way", order.getId()), order);
        order.getOrderStatuses().add(orderStatus);

        orderStatusRepository.save(orderStatus);

        order.getOrderStatuses().stream()
                .filter(previosOrderStatus -> previosOrderStatus.getId().getStatus().equals("preparing"))
                .findFirst()
                .ifPresent(previosOrderStatus -> previosOrderStatus.setEndDate(orderStatus.getStartDate()));
    }

    /// UPDATE STATUS TO DELIVERED
    public OrderDTO deliveredOrder(int id) throws UnauthorizedException {
        validatorService.validateExists(orderRepository.findById(id), LogicErrorMessages.OrderErrorMessages.getOrderNotFoundMessage(String.valueOf(id)));
        Order order = orderRepository.findById(id).orElseThrow();

        // CHECK IF THE LAST STATUS IS ON THE WAY
        if (order.getOrderStatuses().isEmpty() || order.getOrderStatuses().stream().noneMatch(orderStatus -> orderStatus.getId().getStatus().equals("on the way"))) {
            String lastStatus = order.getOrderStatuses().stream()
                    .map(OrderStatus::getId)
                    .map(OrderStatusId::getStatus)
                    .reduce((first, second) -> second)
                    .orElse("none");
            throw new UnauthorizedException(LogicErrorMessages.OrderErrorMessages.errorChangeOrderStatus(String.valueOf(id), "delivered", lastStatus));
        }

        OrderStatus orderStatus =  new OrderStatus(new OrderStatusId("delivered", order.getId()), order);
        order.getOrderStatuses().add(orderStatus);
        orderStatusRepository.save(orderStatus);

        order.getOrderStatuses().stream()
                .filter(previosOrderStatus -> previosOrderStatus.getId().getStatus().equals("on the way"))
                .findFirst()
                .ifPresent(previosOrderStatus -> previosOrderStatus.setEndDate(orderStatus.getStartDate()));

        return new OrderDTO(orderRepository.save(order));
    }

    ///  DELETE AN ORDER
    public void deleteOrder(int id) {
        validatorService.validateExists(orderRepository.findById(id), LogicErrorMessages.OrderErrorMessages.getOrderNotFoundMessage(String.valueOf(id)));
        Order order = orderRepository.findById(id).orElseThrow();

        // Delete OrderProduct and OrderStatus related to the order using the repository where the id is of the order
        orderProductRepository.deleteAll(order.getOrderProducts());
        orderStatusRepository.deleteAll(order.getOrderStatuses());

        orderRepository.delete(order);
    }

    /// UPDATE DEBT OF AN ORDER
    public OrderDTO updateDebt(int id, CreditPaymentDTO paymentDTO) throws UnauthorizedException{
        validatorService.validateExists(orderRepository.findById(id), LogicErrorMessages.OrderErrorMessages.getOrderNotFoundMessage(String.valueOf(id)));
        Order order = orderRepository.findById(id).orElseThrow();

        // Validate payment amount is less than or equal to debt
        if (paymentDTO.paymentAmount() > order.getDebt()) {
            throw new UnauthorizedException(LogicErrorMessages.OrderErrorMessages.invalidCreditDebtPayment(String.valueOf(order.getDebt()), String.valueOf(paymentDTO.paymentAmount())));
        }

        // If the process is successful, update the debt
        order.setDebt(order.getDebt() - paymentDTO.paymentAmount());

        // Return the order with the updated debt
        return new OrderDTO(orderRepository.save(order));
    }

    /// METHODS TO UPDATE THE ORDER PRODUCTS
    /// ADD PRODUCT TO ORDER
    public OrderDTO addProductToOrder(int id, String productCode, int quantity) {
        // Validate exists product and order
        validatorService.validateExists(orderRepository.findById(id), LogicErrorMessages.OrderErrorMessages.getOrderNotFoundMessage(String.valueOf(id)));
        validatorService.validateExists(productRepository.findById(productCode), LogicErrorMessages.OrderErrorMessages.getProductNotFoundMessage(productCode));

        Order order = orderRepository.findById(id).orElseThrow();
        Product product = productRepository.findById(productCode).orElseThrow();

        if (order.getOrderProducts() == null) {
            order.setOrderProducts(new LinkedHashSet<>());
        }

        OrderProduct orderProduct = order.getOrderProducts().stream()
                .filter(op -> op.getId().getProductCode().equals(productCode))
                .findFirst()
                .orElse(null);

        if (orderProduct != null) {
            orderProduct.setQuantity(quantity);
            orderProduct.setPrice(product.getPrice() * orderProduct.getQuantity());
            orderProductRepository.save(orderProduct);
        } else {
            orderProduct = new OrderProduct(order, product, quantity);
            order.getOrderProducts().add(orderProduct);
            orderProductRepository.save(orderProduct);
        }

        order.calculateTotal();
        orderRepository.save(order);

        return new OrderDTO(order);
    }


    /// DELETE PRODUCT FROM ORDER
    public OrderDTO deleteProductFromOrder(int id, String productCode) throws ResourceNotFoundException {
        // Find order, product and order product by respective id
        validatorService.validateExists(orderRepository.findById(id), LogicErrorMessages.OrderErrorMessages.getOrderNotFoundMessage(String.valueOf(id)));
        validatorService.validateExists(productRepository.findById(productCode), LogicErrorMessages.OrderErrorMessages.getProductNotFoundMessage(productCode));

        Order order = orderRepository.findById(id).orElseThrow();

        OrderProduct orderProduct = order.getOrderProducts().stream()
                .filter(op -> op.getId().getProductCode().equals(productCode))
                .findFirst()
                .orElse(null);

        if (orderProduct == null) throw new ResourceNotFoundException(LogicErrorMessages.OrderErrorMessages.invalidProductInOrder(productCode, String.valueOf(id)));

        order.getOrderProducts().remove(orderProduct);
        order.calculateTotal();

        orderProductRepository.delete(orderProduct);
        orderRepository.save(order);

        return new OrderDTO(order);
    }
}
