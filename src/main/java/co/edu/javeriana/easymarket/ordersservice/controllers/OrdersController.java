package co.edu.javeriana.easymarket.ordersservice.controllers;

import co.edu.javeriana.easymarket.ordersservice.dtos.AddOrderProductDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.Response;
import co.edu.javeriana.easymarket.ordersservice.dtos.orders.OrderCreateDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.orders.OrderDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.utils.ConfirmOrderDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.utils.OnWayCompanyDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.utils.OnWayDeliveryDTO;
import co.edu.javeriana.easymarket.ordersservice.services.OrderService;
import co.edu.javeriana.easymarket.ordersservice.utils.OperationException;
import org.springframework.beans.factory.annotation.Autowired;
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

    ///  Get Methods
    /// Get all orders
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }

    ///  Get all orders by user
    @GetMapping("/{idUser}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable String idUser) {
        try{
            List<OrderDTO> orders = orderService.getOrdersByUser(idUser);
            return ResponseEntity.ok(orders);
        } catch (OperationException e) {
            return ResponseEntity.status(e.getCode()).body(null);
        }
    }

    ///  Get order by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable int id) {
        try{
            OrderDTO order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (OperationException e) {
            return ResponseEntity.status(e.getCode()).body(new Response(e.getCode(), e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(500, e.getMessage()));
        }
    }

    ///  Create an order
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderCreateDTO orderDTO) {
        try{
            OrderDTO order = orderService.createOrder(orderDTO);
            return ResponseEntity.ok(order);
        } catch (OperationException e) {
            return ResponseEntity.status(e.getCode()).body(new Response(e.getCode(), e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(500, e.getMessage()));
        }
    }

    ///  Update status of an order
    ///  Confirm order
    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmOrder(@PathVariable int id, @RequestBody ConfirmOrderDTO confirmOrderDTO) {
        try{
            OrderDTO order = orderService.confirmOrder(id, confirmOrderDTO.shippingCost());
            return ResponseEntity.ok(order);
        } catch (OperationException e) {
            return ResponseEntity.status(e.getCode()).body(new Response(e.getCode(), e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(500, e.getMessage()));
        }
    }

    ///  Preparing order
    @PutMapping("/{id}/preparing")
    public ResponseEntity<?> preparingOrder(@PathVariable int id) {
        try{
            OrderDTO order = orderService.prepareOrder(id);
            return ResponseEntity.ok(order);
        } catch (OperationException e) {
            return ResponseEntity.status(e.getCode()).body(new Response(e.getCode(), e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(500, e.getMessage()));
        }
    }

    ///  On The Way (Delivery)
    @PutMapping("/{id}/on-the-way/delivery")
    public ResponseEntity<?> onTheWayDelivery(@PathVariable int id, @RequestBody OnWayDeliveryDTO delivery) {
        try{
            OrderDTO order = orderService.onTheWayDomiciliaryOrder(id, delivery.idDomiciliary());
            return ResponseEntity.ok(order);
        } catch (OperationException e) {
            return ResponseEntity.status(e.getCode()).body(new Response(e.getCode(), e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(500, e.getMessage()));
        }
    }

    ///  On The Way (Company)
    @PutMapping("/{id}/on-the-way/company")
    public ResponseEntity<?> onTheWayCompany(@PathVariable int id, @RequestBody OnWayCompanyDTO transportCompany) {
        try{
            OrderDTO order = orderService.onTheWayTransportCompanyOrder(id, transportCompany.transportCompany(), transportCompany.shippingGuide());
            return ResponseEntity.ok(order);
        } catch (OperationException e) {
            return ResponseEntity.status(e.getCode()).body(new Response(e.getCode(), e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(500, e.getMessage()));
        }
    }

    ///  Delivered order
    @PutMapping("/{id}/delivered")
    public ResponseEntity<?> deliveredOrder(@PathVariable int id) {
        try{
            OrderDTO order = orderService.deliveredOrder(id);
            return ResponseEntity.ok(order);
        } catch (OperationException e) {
            return ResponseEntity.status(e.getCode()).body(new Response(e.getCode(), e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(500, e.getMessage()));
        }
    }

    ///  Delete an order
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable int id) {
        try{
            orderService.deleteOrder(id);
            return ResponseEntity.ok().build();
        } catch (OperationException e) {
            return ResponseEntity.status(e.getCode()).body(new Response(e.getCode(), e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(500, e.getMessage()));
        }
    }

    ///  Methods for update the order-products of an order
    ///  Add product to order
    @PostMapping("/{id_order}/products/{id_product}")
    public ResponseEntity<?> addProductToOrder(@PathVariable int id_order, @PathVariable String id_product, @RequestParam AddOrderProductDTO addOrderProductDTO) {
        try {
            OrderDTO order = orderService.addProductToOrder(id_order, id_product, addOrderProductDTO.quantity());
            return ResponseEntity.ok(order);
        } catch (OperationException e) {
            return ResponseEntity.status(e.getCode()).body(new Response(e.getCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(500, e.getMessage()));
        }
    }
    ///  Delete product from order
    @DeleteMapping("/{id_order}/products/{id_product}")
    public ResponseEntity<?> deleteProductFromOrder(@PathVariable int id_order, @PathVariable String id_product) {
        try{
            OrderDTO order = orderService.deleteProductFromOrder(id_order, id_product);
            return ResponseEntity.ok(order);
        } catch (OperationException e) {
            return ResponseEntity.status(e.getCode()).body(new Response(e.getCode(), e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(500, e.getMessage()));
        }
    }
}
