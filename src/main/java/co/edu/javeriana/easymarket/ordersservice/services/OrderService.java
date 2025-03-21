package co.edu.javeriana.easymarket.ordersservice.services;

import co.edu.javeriana.easymarket.ordersservice.dtos.orders.OrderCreateDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.orders.OrderDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.orders.OrderUpdateDTO;
import co.edu.javeriana.easymarket.ordersservice.model.*;
import co.edu.javeriana.easymarket.ordersservice.repository.OrderProductRepository;
import co.edu.javeriana.easymarket.ordersservice.repository.OrderRepository;
import co.edu.javeriana.easymarket.ordersservice.repository.OrderStatusRepository;
import co.edu.javeriana.easymarket.ordersservice.repository.ProductRepository;
import co.edu.javeriana.easymarket.ordersservice.utils.OperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderService (OrderRepository orderRepository, OrderStatusRepository orderStatusRepository, OrderProductRepository orderProductRepository, ProductRepository productRepository){
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.orderProductRepository = orderProductRepository;
        this.productRepository = productRepository;
    }

    ///  Get all orders
    public List<OrderDTO> getOrders(){
        return orderRepository.findAll()
                .stream()
                .map(OrderDTO::new)
                .collect(Collectors.toList());
    }

    ///  Get orders by user
    public List<OrderDTO> getOrdersByUser(String idUser) throws OperationException {
        return orderRepository.findAll()
                .stream()
                .filter(order -> order.getIdUser().equals(idUser))
                .map(OrderDTO::new)
                .collect(Collectors.toList());
    }

    ///  Get a specific order by id
    public OrderDTO getOrderById(int id) throws OperationException {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) throw new OperationException(404, "Order not found");

        return new OrderDTO(order);
    }

    ///  Create an order
    public OrderDTO createOrder(OrderCreateDTO orderDTO) throws OperationException {
        Order order = new Order(orderDTO);
        // Save order in the database
        try {
            return new OrderDTO(orderRepository.save(order));
        }
        catch (Exception e){
            throw new OperationException(500, "Error creating order".concat(e.getMessage()));
        }
    }

    ///  Update an order
    public OrderDTO updateOrder(int id, OrderUpdateDTO orderDTO) throws OperationException {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) throw new OperationException(404, "Order not found");
        order.setAddress(orderDTO.address());

        // Save order in the database
        try {
            return new OrderDTO(orderRepository.save(order));
        }
        catch (Exception e){
            throw new OperationException(500, "Error updating order ".concat(e.getMessage()));
        }
    }

    ///  Status of the order
    /// Confirm order
    public OrderDTO confirmOrder(int id, Integer shippingCost) throws OperationException {
        Order order = orderRepository.findById(id).orElse(null);

        if (order == null) throw new OperationException(404, "Order not found");

        // Add confirmed status to the order and set shipping cost
        try {
            OrderStatusId orderStatusId = new OrderStatusId("confirmed", order.getId());
            OrderStatus orderStatus = new OrderStatus(orderStatusId, order);
            order.getOrderStatuses().add(orderStatus);

            orderStatusRepository.save(orderStatus);

            order.setShippingCost(shippingCost);
            float total = (order.getTotal() + shippingCost);
            order.setTotal(total);

            return new OrderDTO(orderRepository.save(order));
        } catch (Exception e) {
            throw new OperationException(500, "Error updating status to Confirmed ".concat(e.getMessage()));
        }
    }

    /// Prepare order
    public OrderDTO prepareOrder(int id) throws OperationException {
        Order order = orderRepository.findById(id).orElse(null);

        if (order == null) throw new OperationException(404, "Order not found");

        // Add prepared status to the order and set end date of the previous status
        try{
            // Add prepared status to the order and set end date of the previous status
            OrderStatus orderStatus = new OrderStatus(new OrderStatusId("preparing", order.getId()), order);
            order.getOrderStatuses().add(orderStatus);
            orderStatusRepository.save(orderStatus);

            order.getOrderStatuses().stream()
                    .filter(previosOrderStatus -> previosOrderStatus.getId().getStatus().equals("confirmed"))
                    .findFirst()
                    .ifPresent(previosOrderStatus -> previosOrderStatus.setEndDate(orderStatus.getStartDate()));
        }
        catch (Exception e){
            throw new OperationException(500, "Wrong status");
        }

        // Save order in the database
        try {
            return new OrderDTO(orderRepository.save(order));
        }
        catch (Exception e){
            throw new OperationException(500, "Error updating status to preparing ".concat(e.getMessage()));
        }
    }
    ///  On the way as domiciliary
    public OrderDTO onTheWayDomiciliaryOrder(int id, String idDomiciliary) throws OperationException {
        Order order = orderRepository.findById(id).orElse(null);

        if (order == null) throw new OperationException(404, "Order not found");

        // Add delivered status to the order and set domiciliary and shipping guide
        try{
            orderStatusOnTheWay(order);
            order.setIdDomiciliary(idDomiciliary);
        }
        catch (Exception e){
            throw new OperationException(500, "Wrong status");
        }

        // Save order in the database
        try {
            return new OrderDTO(orderRepository.save(order));
        }
        catch (Exception e){
            throw new OperationException(500, "Error updating status to On The Way ".concat(e.getMessage()));
        }
    }
    ///  On the way as transport company
    public OrderDTO onTheWayTransportCompanyOrder(int id, String transportCompany, String shippingGuide) throws OperationException {
        Order order = orderRepository.findById(id).orElse(null);

        if (order == null) throw new OperationException(404, "Order not found");

        // Add delivered status to the order and set transport company and shipping guide
        try{
            orderStatusOnTheWay(order);

            order.setTransportCompany(transportCompany);
            order.setShippingGuide(shippingGuide);
        }
        catch (Exception e){
            throw new OperationException(500, "Wrong status");
        }

        // Save order in the database
        try {
            return new OrderDTO(orderRepository.save(order));
        }
        catch (Exception e){
            throw new OperationException(500, "Error updating status to On The Way".concat(e.getMessage()));
        }
    }

    ///  Aux Method for avoid duplicated code for the OnTheWay status
    private void orderStatusOnTheWay(Order order) {
        OrderStatus orderStatus = new OrderStatus(new OrderStatusId("on the way", order.getId()), order);
        order.getOrderStatuses().add(orderStatus);

        orderStatusRepository.save(orderStatus);

        order.getOrderStatuses().stream()
                .filter(previosOrderStatus -> previosOrderStatus.getId().getStatus().equals("preparing"))
                .findFirst()
                .ifPresent(previosOrderStatus -> previosOrderStatus.setEndDate(orderStatus.getStartDate()));
    }

    /// Delivered order
    public OrderDTO deliveredOrder(int id) throws OperationException {
        Order order = orderRepository.findById(id).orElse(null);

        if (order == null) throw new OperationException(404, "Order not found");

        // Add delivered status to the order
        try{
            OrderStatus orderStatus =  new OrderStatus(new OrderStatusId("delivered", order.getId()), order);
            order.getOrderStatuses().add(orderStatus);

            orderStatusRepository.save(orderStatus);

            order.getOrderStatuses().stream()
                    .filter(previosOrderStatus -> previosOrderStatus.getId().getStatus().equals("on the way"))
                    .findFirst()
                    .ifPresent(previosOrderStatus -> previosOrderStatus.setEndDate(orderStatus.getStartDate()));
        }
        catch (Exception e){
            throw new OperationException(500, "Wrong status");
        }
        // Save order in the database
        try {
            return new OrderDTO(orderRepository.save(order));
        }
        catch (Exception e){
            throw new OperationException(500, "Error updating status to delivered ".concat(e.getMessage()));
        }
    }

    ///  Delete an order
    public void deleteOrder(int id) throws OperationException {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) throw new OperationException(404, "Order not found");

        try {
            // Delete OrderProduct and OrderStatus related to the order using the repository where the id is of the order
            orderProductRepository.deleteAll(order.getOrderProducts());
            orderStatusRepository.deleteAll(order.getOrderStatuses());

            orderRepository.delete(order);
        } catch (Exception e) {
            throw new OperationException(500, "Error deleting order ".concat(e.getMessage()));
        }
    }

    ///  Methods to update the products of an order
    /// Add Product to Order
    public OrderDTO addProductToOrder(int id, String productCode, int quantity) throws OperationException {
        Order order = orderRepository.findById(id).orElse(null);
        Product product = productRepository.findById(productCode).orElse(null);

        if (order == null) throw new OperationException(404, "Order not found");
        if (product == null) throw new OperationException(404, "Product not found");

        if (order.getOrderProducts() == null) {
            order.setOrderProducts(new LinkedHashSet<>());
        }

        try {
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
        } catch (Exception e) {
            throw new OperationException(500, "Error adding product to order ".concat(e.getMessage()));
        }
    }


    ///  Delete product from order
    public OrderDTO deleteProductFromOrder(int id, String productCode) throws OperationException {
        // Find order and product by respective id
        Order order = orderRepository.findById(id).orElse(null);
        Product product = productRepository.findById(productCode).orElse(null);

        if (order == null) throw new OperationException(404, "Order not found");
        if (product == null) throw new OperationException(404, "Product not found");

        OrderProduct orderProduct = order.getOrderProducts().stream()
                .filter(op -> op.getId().getProductCode().equals(productCode))
                .findFirst()
                .orElse(null);

        if (orderProduct == null) throw new OperationException(404, "Product not found in order");

        try {
            // If it exists, delete the OrderProduct
            order.getOrderProducts().remove(orderProduct);

            // Calculate the total of the order after deleting the product
            order.calculateTotal();

            // Save the updated order without the deleted product
            orderRepository.save(order);

            // Delete the OrderProduct from the database
            orderProductRepository.delete(orderProduct);

            // Return the updated OrderDTO
            return new OrderDTO(order);
        } catch (Exception e) {
            throw new OperationException(500, "Error deleting product from order ".concat(e.getMessage()));
        }
    }
}
