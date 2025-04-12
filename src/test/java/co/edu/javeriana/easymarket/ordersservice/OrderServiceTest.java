package co.edu.javeriana.easymarket.ordersservice;

import co.edu.javeriana.easymarket.ordersservice.dtos.orders.OrderCreateDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.orders.OrderDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.orders.OrderUpdateDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.utils.CreditPaymentDTO;
import co.edu.javeriana.easymarket.ordersservice.dtos.utils.OnWayDeliveryDTO;
import co.edu.javeriana.easymarket.ordersservice.exceptions.business_exceptions.ResourceNotFoundException;
import co.edu.javeriana.easymarket.ordersservice.exceptions.business_exceptions.UnauthorizedException;
import co.edu.javeriana.easymarket.ordersservice.exceptions.error_messages.LogicErrorMessages;
import co.edu.javeriana.easymarket.ordersservice.model.Order;
import co.edu.javeriana.easymarket.ordersservice.model.OrderProduct;
import co.edu.javeriana.easymarket.ordersservice.model.OrderStatus;
import co.edu.javeriana.easymarket.ordersservice.model.OrderStatusId;
import co.edu.javeriana.easymarket.ordersservice.model.Product;
import co.edu.javeriana.easymarket.ordersservice.model.User;
import co.edu.javeriana.easymarket.ordersservice.repository.OrderProductRepository;
import co.edu.javeriana.easymarket.ordersservice.repository.OrderRepository;
import co.edu.javeriana.easymarket.ordersservice.repository.OrderStatusRepository;
import co.edu.javeriana.easymarket.ordersservice.repository.ProductRepository;
import co.edu.javeriana.easymarket.ordersservice.repository.UserRepository;
import co.edu.javeriana.easymarket.ordersservice.services.OrderService;
import co.edu.javeriana.easymarket.ordersservice.services.helpers.ValidationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock OrderStatusRepository orderStatusRepository;
    @Mock OrderProductRepository orderProductRepository;
    @Mock ProductRepository productRepository;
    @Mock UserRepository userRepository;

    @Spy ValidationService validationService;
    @InjectMocks
    OrderService orderService;

    private Order order;
    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user1");

        product = new Product();
        product.setCode("P1");
        product.setPrice(10f);

        order = new Order();
        order.setId(1);
        order.setIdUser(user.getId());
        order.setAddress("addr");
        order.setTotal(100f);
        order.setDebt(100f);
        order.setOrderProducts(new LinkedHashSet<>());
        order.setOrderStatuses(new LinkedHashSet<>());
    }

    // GET ALL ORDERS
    @Test
    void testGetOrders_returnsList() {
        when(orderRepository.findAll()).thenReturn(List.of(order));
        var list = orderService.getOrders();
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(order.getId(), list.getFirst().getId());
    }

    // CREATE ORDER
    @Test
    void testCreateOrder_success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        var dto = orderService.createOrder(new OrderCreateDTO(user.getId(), order.getAddress()));
        assertNotNull(dto);
        assertEquals(order.getId(), dto.getId());
    }

    @Test
    void testCreateOrder_userNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class,
                () -> orderService.createOrder(new OrderCreateDTO(user.getId(), order.getAddress()))
        );
        assertEquals(
                LogicErrorMessages.OrderErrorMessages.getUserNotFoundMessage(user.getId()),
                ex.getMessage()
        );
    }

    // GET ORDERS BY USER
    @Test
    void testGetOrdersByUser_filtersByUser() {
        var other = new Order(); other.setId(2); other.setIdUser("other");
        when(orderRepository.findAll()).thenReturn(List.of(order, other));

        var list = orderService.getOrdersByUser(user.getId());
        assertEquals(1, list.size());
        assertEquals(order.getId(), list.getFirst().getId());
    }

    // GET ORDER BY ID
    @Test
    void testGetOrderById_success() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        var dto = orderService.getOrderById(order.getId());
        assertNotNull(dto);
        assertEquals(order.getId(), dto.getId());
    }

    @Test
    void testGetOrderById_notFound() {
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        var ex = assertThrows(ResourceNotFoundException.class,
                () -> orderService.getOrderById(999)
        );
        assertEquals(
                LogicErrorMessages.OrderErrorMessages.getOrderNotFoundMessage("999"),
                ex.getMessage()
        );
    }

    // UPDATE ORDER
    @Test
    void testUpdateOrder_success() {
        var update = new OrderUpdateDTO("Cra. 7 #40-50");
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        var dto = orderService.updateOrder(order.getId(), update);
        assertEquals("Cra. 7 #40-50", dto.getAddress());
    }

    @Test
    void testUpdateOrder_notFound() {
        when(orderRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.updateOrder(999, new OrderUpdateDTO("addr"))
        );
    }

    // CONFIRM ORDER
    @Test
    @SneakyThrows
    void testConfirmOrder_success() {
        order.setTotal(100f);
        order.setOrderStatuses(new LinkedHashSet<>());

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderStatusRepository.save(any(OrderStatus.class))).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(order)).thenReturn(order);

        int shippingCost = 20;
        float paymentAmount = 10f;

        OrderDTO dto = orderService.confirmOrder(order.getId(), shippingCost, paymentAmount);

        assertEquals(shippingCost, dto.getShippingCost());

        float expectedDebt = (100f + shippingCost) - paymentAmount;
        assertEquals(expectedDebt, dto.getDebt());

        assertTrue(order.getOrderStatuses().stream()
                .anyMatch(s -> s.getId().getStatus().equals("confirmed")));
    }


    @Test
    void testConfirmOrder_alreadyConfirmed() {
        var os = new OrderStatus(new OrderStatusId("confirmed", order.getId()), order);
        order.setOrderStatuses(new LinkedHashSet<>(List.of(os)));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(UnauthorizedException.class,
                () -> orderService.confirmOrder(order.getId(), 20, 10f)
        );
    }

    // PREPARE ORDER
    @Test
    void testPrepareOrder_success() {
        var confirmed = new OrderStatus(new OrderStatusId("confirmed", order.getId()), order);
        order.setOrderStatuses(new LinkedHashSet<>(List.of(confirmed)));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderStatusRepository.save(any(OrderStatus.class))).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(order)).thenReturn(order);

        var dto = orderService.prepareOrder(order.getId());
        assertTrue(order.getOrderStatuses().stream()
                .anyMatch(s -> s.getId().getStatus().equals("preparing")));
        assertEquals(order.getId(), dto.getId());
    }

    @Test
    void testPrepareOrder_notConfirmed() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        assertThrows(UnauthorizedException.class,
                () -> orderService.prepareOrder(order.getId())
        );
    }

    // ON THE WAY DOMICILIARY
    @Test
    void testOnTheWayDomiciliaryOrder_success() {
        var prep = new OrderStatus(new OrderStatusId("preparing", order.getId()), order);
        order.setOrderStatuses(new LinkedHashSet<>(List.of(prep)));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        var input = new OnWayDeliveryDTO("dom1", 4.0f, -74.0f);
        var dto = orderService.onTheWayDomiciliaryOrder(order.getId(), input);
        assertEquals("dom1", dto.getIdDomiciliary());
        assertEquals(BigDecimal.valueOf(4.0), dto.getLat());
        assertEquals(BigDecimal.valueOf(-74.0), dto.getLng());
    }

    // ON THE WAY TRANSPORT COMPANY
    @Test
    void testOnTheWayTransportCompanyOrder_success() {
        var prep = new OrderStatus(new OrderStatusId("preparing", order.getId()), order);
        order.setOrderStatuses(new LinkedHashSet<>(List.of(prep)));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        var dto = orderService.onTheWayTransportCompanyOrder(order.getId(), "TC", "SG");
        assertEquals("TC", dto.getTransportCompany());
        assertEquals("SG", dto.getShippingGuide());
    }

    @Test
    void testOnTheWay_notPreparing() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        assertThrows(UnauthorizedException.class,
                () -> orderService.onTheWayDomiciliaryOrder(order.getId(), new OnWayDeliveryDTO("d", 0f, 0f))
        );
        assertThrows(UnauthorizedException.class,
                () -> orderService.onTheWayTransportCompanyOrder(order.getId(), "TC", "SG")
        );
    }

    // DELIVERED ORDER
    @Test
    void testDeliveredOrder_success() {
        var onWay = new OrderStatus(new OrderStatusId("on the way", order.getId()), order);
        order.setOrderStatuses(new LinkedHashSet<>(List.of(onWay)));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderStatusRepository.save(any(OrderStatus.class))).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(order)).thenReturn(order);

        var dto = orderService.deliveredOrder(order.getId());
        assertTrue(order.getOrderStatuses().stream()
                .anyMatch(s -> s.getId().getStatus().equals("delivered")));
        assertEquals(order.getId(), dto.getId());
    }

    @Test
    void testDeliveredOrder_notOnTheWay() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        assertThrows(UnauthorizedException.class,
                () -> orderService.deliveredOrder(order.getId())
        );
    }

    // DELETE ORDER
    @Test
    void testDeleteOrder_success() {
        order.setOrderStatuses(new LinkedHashSet<>());
        order.setOrderProducts(new LinkedHashSet<>());
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        orderService.deleteOrder(order.getId());
        verify(orderProductRepository).deleteAll(order.getOrderProducts());
        verify(orderStatusRepository).deleteAll(order.getOrderStatuses());
        verify(orderRepository).delete(order);
    }

    @Test
    void testDeleteOrder_notFound() {
        when(orderRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.deleteOrder(999)
        );
    }

    // UPDATE DEBT
    @Test
    void testUpdateDebt_success() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        var dto = orderService.updateDebt(order.getId(), new CreditPaymentDTO(50f));
        assertEquals(order.getDebt(), dto.getDebt());
    }

    @Test
    void testUpdateDebt_invalidPayment() {
        order.setDebt(10f);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        assertThrows(UnauthorizedException.class,
                () -> orderService.updateDebt(order.getId(), new CreditPaymentDTO(20f))
        );
    }

    // ADD PRODUCT TO ORDER
    @Test
    void testAddProductToOrder_newProduct() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(productRepository.findById(product.getCode())).thenReturn(Optional.of(product));
        when(orderProductRepository.save(any(OrderProduct.class))).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(order)).thenReturn(order);

        var dto = orderService.addProductToOrder(order.getId(), product.getCode(), 2);
        assertEquals(1, order.getOrderProducts().size());
        assertEquals(2, (int) dto.getProducts().get(product.getCode()));
    }

    @Test
    void testAddProductToOrder_existingProduct() {
        var op = new OrderProduct(order, product, 1);
        order.setOrderProducts(new LinkedHashSet<>(List.of(op)));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(productRepository.findById(product.getCode())).thenReturn(Optional.of(product));
        when(orderProductRepository.save(op)).thenReturn(op);
        when(orderRepository.save(order)).thenReturn(order);

        var dto = orderService.addProductToOrder(order.getId(), product.getCode(), 3);
        assertTrue(order.getOrderProducts().stream().anyMatch(x -> x.getQuantity() == 3));
        assertEquals(3, (int) dto.getProducts().get(product.getCode()));
    }

    @Test
    void testAddProductToOrder_orderNotFound() {
        when(orderRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.addProductToOrder(999, product.getCode(), 1)
        );
    }

    @Test
    void testAddProductToOrder_productNotFound() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(productRepository.findById("X")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.addProductToOrder(order.getId(), "X", 1)
        );
    }

    // DELETE PRODUCT FROM ORDER
    @Test
    void testDeleteProductFromOrder_success() {
        var op = new OrderProduct(order, product, 1);
        order.setOrderProducts(new LinkedHashSet<>(List.of(op)));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(productRepository.findById(product.getCode())).thenReturn(Optional.of(product));
        when(orderRepository.save(order)).thenReturn(order);

        var dto = orderService.deleteProductFromOrder(order.getId(), product.getCode());
        assertTrue(order.getOrderProducts().isEmpty());
        assertTrue(dto.getProducts().isEmpty());
    }

    @Test
    void testDeleteProductFromOrder_orderNotFound() {
        when(orderRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.deleteProductFromOrder(999, product.getCode())
        );
    }

    @Test
    void testDeleteProductFromOrder_productNotFound() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(productRepository.findById("X")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.deleteProductFromOrder(order.getId(), "X")
        );
    }

    @Test
    void testDeleteProductFromOrder_invalidProductInOrder() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(productRepository.findById(product.getCode())).thenReturn(Optional.of(product));
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.deleteProductFromOrder(order.getId(), product.getCode())
        );
    }
}
