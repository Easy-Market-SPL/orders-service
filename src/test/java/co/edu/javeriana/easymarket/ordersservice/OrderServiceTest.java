package co.edu.javeriana.easymarket.ordersservice;

import co.edu.javeriana.easymarket.ordersservice.model.Order;
import co.edu.javeriana.easymarket.ordersservice.repository.*;
import co.edu.javeriana.easymarket.ordersservice.services.OrderService;
import co.edu.javeriana.easymarket.ordersservice.services.helpers.ValidationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OrderServiceTest {

    ///  MOCKS
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProductRepository orderProductRepository;

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private OrderService orderService;

    private Order order;

    @Test
    void contextLoads() {
        // This test will fail if the application context cannot start
    }
}
