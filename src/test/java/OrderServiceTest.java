import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {
    private OrderRepository orderRepository;
    private PaymentService paymentService;
    private OrderService orderService;

    @BeforeEach
    void setup() {
        orderRepository = mock(OrderRepository.class);
        paymentService = mock(PaymentService.class);
        orderService = new OrderService(orderRepository, paymentService);
    }

    @Test
    void testPlaceOrderWhenPaymentIsSuccessful() {
        Order order = new Order(1, 100.0);
        when(paymentService.processPayment(order.getAmount())).thenReturn(true);

        boolean result = orderService.placeOrder(order);

        assertTrue(result);
        verify(orderRepository).save(order);
    }

    @Test
    void testPlaceOrderWhenPaymentFails() {
        Order order = new Order(1, 100.0);
        when(paymentService.processPayment(order.getAmount())).thenReturn(false);

        boolean result = orderService.placeOrder(order);

        assertFalse(result);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetOrderByIdFound() {
        Order order = new Order(1, 100.0);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.getOrderById(1);

        assertNotNull(foundOrder);
        assertEquals(order, foundOrder);
    }

    @Test
    void testGetOrderByIdNotFound() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        Order foundOrder = orderService.getOrderById(1);

        assertNull(foundOrder);
    }

    @Test
    void testCancelOrderSuccess() {
        Order order = new Order(1, 100.0);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        assertDoesNotThrow(() -> orderService.cancelOrder(1));
        verify(orderRepository).delete(order);
    }

    @Test
    void testCancelOrderNotFound() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderService.cancelOrder(1));
    }

    @Test
    void testListAllOrders() {
        Order order1 = new Order(1, 100.0);
        Order order2 = new Order(2, 200.0);
        List<Order> orders = Arrays.asList(order1, order2);
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> resultOrders = orderService.listAllOrders();

        assertNotNull(resultOrders);
        assertEquals(2, resultOrders.size());
        assertTrue(resultOrders.containsAll(orders));
    }
}
