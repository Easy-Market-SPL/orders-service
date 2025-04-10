package co.edu.javeriana.easymarket.ordersservice.controllers;

import co.edu.javeriana.easymarket.ordersservice.dtos.AddOrderProductDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.orders.OrderCreateDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.orders.OrderDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.orders.OrderUpdateDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.utils.ConfirmOrderDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.utils.OnWayCompanyDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.utils.OnWayDeliveryDTO;
import co.edu.javeriana.easymarket.ordersservice.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdersController {
    private final OrderService orderService;

    @Autowired
    public OrdersController(OrderService orderService) {
        this.orderService = orderService;
    }

    /// GET METHODS
    /// GET ALL ORDERS
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }

    /// GET ALL ORDERS BY USER
    @GetMapping("/{idUser}/user")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable String idUser) {
        List<OrderDTO> orders = orderService.getOrdersByUser(idUser);
        return ResponseEntity.ok(orders);
    }

    ///  GET ORDER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable int id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /// CREATE AN ORDER
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderCreateDTO orderDTO) {
        OrderDTO order = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(order);
    }

    /// UPDATE AN ORDER (ONLY ADDRESS)
    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable int id, @RequestBody OrderUpdateDTO orderDTO) {
        OrderDTO order = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok(order);
    }

    ///  UPDATE STATUS OF ORDER
    ///  UPDATE STATUS TO CONFIRMED
    @PutMapping("/{id}/confirm")
    public ResponseEntity<OrderDTO> confirmOrder(@PathVariable int id, @RequestBody ConfirmOrderDTO confirmOrderDTO) {
        OrderDTO order = orderService.confirmOrder(id, confirmOrderDTO.shippingCost(), confirmOrderDTO.paymentAmount());
        return ResponseEntity.ok(order);
    }

    /// UPDATE STATUS TO PREPARING
    @PutMapping("/{id}/preparing")
    public ResponseEntity<OrderDTO> preparingOrder(@PathVariable int id) {
        OrderDTO order = orderService.prepareOrder(id);
        return ResponseEntity.ok(order);
    }

    /// UPDATE STATUS TO ON THE WAY (DOMICILIARY)
    @PutMapping("/{id}/on-the-way/domiciliary")
    public ResponseEntity<OrderDTO> onTheWayDelivery(@PathVariable int id, @RequestBody OnWayDeliveryDTO delivery) {
        OrderDTO order = orderService.onTheWayDomiciliaryOrder(id, delivery);
        return ResponseEntity.ok(order);
    }

    /// UPDATE STATUS TO ON THE WAY (TRANSPORT COMPANY)
    @PutMapping("/{id}/on-the-way/company")
    public ResponseEntity<OrderDTO> onTheWayCompany(@PathVariable int id, @RequestBody OnWayCompanyDTO transportCompany) {
        OrderDTO order = orderService.onTheWayTransportCompanyOrder(id, transportCompany.transportCompany(), transportCompany.shippingGuide());
        return ResponseEntity.ok(order);
    }

    /// UPDATE STATUS TO DELIVERED
    @PutMapping("/{id}/delivered")
    public ResponseEntity<OrderDTO> deliveredOrder(@PathVariable int id) {
        OrderDTO order = orderService.deliveredOrder(id);
        return ResponseEntity.ok(order);
    }

    /// DELETE AN ORDER
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable int id) {
        orderService.deleteOrder(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /// METHODS FOR UPDATE PRODUCTS IN ORDER
    /// ADD PRODUCT TO AN ORDER
    @PutMapping("/{id_order}/products/{id_product}")
    public ResponseEntity<OrderDTO> addProductToOrder(@PathVariable int id_order, @PathVariable String id_product, @RequestBody AddOrderProductDTO addOrderProductDTO) {
        OrderDTO order = orderService.addProductToOrder(id_order, id_product, addOrderProductDTO.quantity());
        return ResponseEntity.ok(order);
    }

    /// DELETE PRODUCT FROM AN ORDER
    @DeleteMapping("/{id_order}/products/{id_product}")
    public ResponseEntity<OrderDTO> deleteProductFromOrder(@PathVariable int id_order, @PathVariable String id_product) {
        OrderDTO order = orderService.deleteProductFromOrder(id_order, id_product);
        return ResponseEntity.ok(order);
    }
}
