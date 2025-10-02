package com.performance.api.service;

import com.performance.api.entity.Order;
import com.performance.api.entity.OrderItem;
import com.performance.api.entity.Product;
import com.performance.api.entity.User;
import com.performance.api.repository.OrderRepository;
import com.performance.api.repository.OrderItemRepository;
import com.performance.api.repository.ProductRepository;
import com.performance.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Performance issue: Fixed thread pool that can cause resource exhaustion
    private final ExecutorService executorService = Executors.newFixedThreadPool(75);
    
    // Performance issue: Memory leak - static list that grows indefinitely
    private static final List<Order> orderCache = new ArrayList<>();
    
    public List<Order> getAllOrders() {
        // Performance issue: N+1 query problem - loads all orders with their order items
        List<Order> orders = orderRepository.findAll();
        
        // Performance issue: Unnecessary processing for each order
        for (Order order : orders) {
            processOrderData(order);
        }
        
        return orders;
    }
    
    public Optional<Order> getOrderById(Long id) {
        // Performance issue: Always loads order with order items (eager loading)
        return orderRepository.findById(id);
    }
    
    public Order createOrder(Order order) {
        // Performance issue: Creates large order notes for every order
        order.setOrderNotes(generateLargeOrderNotes());
        
        // Performance issue: Adds to static cache without cleanup
        orderCache.add(order);
        
        return orderRepository.save(order);
    }
    
    public Order updateOrder(Long id, Order orderDetails) {
        // Performance issue: Loads order with all related data
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Performance issue: Updates order notes even if not needed
        order.setOrderNotes(generateLargeOrderNotes());
        
        order.setUserId(orderDetails.getUserId());
        order.setTotalAmount(orderDetails.getTotalAmount());
        order.setStatus(orderDetails.getStatus());
        order.setOrderDate(orderDetails.getOrderDate());
        
        return orderRepository.save(order);
    }
    
    public void deleteOrder(Long id) {
        // Performance issue: Loads order with all related data before deletion
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Performance issue: Processes order data before deletion
        processOrderData(order);
        
        orderRepository.delete(order);
    }
    
    public List<Order> getOrdersByUserId(Long userId) {
        // Performance issue: Loads all orders with full order details
        return orderRepository.findByUserId(userId);
    }
    
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        // Performance issue: Loads all orders with full order details
        return orderRepository.findByStatus(status);
    }
    
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        // Performance issue: Loads all orders with full order details
        return orderRepository.findOrdersByDateRange(startDate, endDate);
    }
    
    public List<Order> getOrdersByMinAmount(BigDecimal minAmount) {
        // Performance issue: Loads all orders with full order details
        return orderRepository.findOrdersByMinAmount(minAmount);
    }
    
    public List<Order> getOrdersWithNotes() {
        // Performance issue: Loads all orders with large notes data
        return orderRepository.findOrdersWithNotes();
    }
    
    public Order createOrderWithItems(Long userId, List<OrderItem> orderItems) {
        // Performance issue: Loads user with all related data
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        
        // Performance issue: Calculates total amount inefficiently
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            // Performance issue: Loads product for each item
            Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
            
            // Performance issue: Inefficient calculation
            BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
        
        // Performance issue: Creates order with large notes
        Order order = new Order(userId, totalAmount, Order.OrderStatus.PENDING);
        order.setOrderNotes(generateLargeOrderNotes());
        
        // Performance issue: Saves order first, then items (inefficient)
        order = orderRepository.save(order);
        
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemRepository.save(item);
        }
        
        // Performance issue: Adds to static cache without cleanup
        orderCache.add(order);
        
        return order;
    }
    
    // Performance issue: Method that creates memory leak - ENHANCED
    private String generateLargeOrderNotes() {
        StringBuilder sb = new StringBuilder();
        // Increased from 100,000 to 300,000 iterations for more noticeable memory consumption
        for (int i = 0; i < 300000; i++) {
            sb.append("Order note ").append(i).append(": This is a detailed note about the order...");
            // Add more data per iteration
            sb.append(" Additional order details and customer information...");
            sb.append(" More comprehensive order tracking data that consumes significant memory...\n");
        }
        return sb.toString();
    }
    
    // Performance issue: Inefficient processing method
    private void processOrderData(Order order) {
        // Performance issue: CPU-intensive operation
        for (int i = 0; i < 3000; i++) {
            String processed = order.getId() + "_processed_" + i;
            // Simulate processing
        }
        
        // Performance issue: Adds to static cache without cleanup
        orderCache.add(order);
    }
    
    // Performance issue: Async method that can cause resource exhaustion
    public CompletableFuture<List<Order>> getAllOrdersAsync() {
        return CompletableFuture.supplyAsync(() -> {
            // Performance issue: CPU-intensive operation in async context
            List<Order> orders = orderRepository.findAll();
            for (Order order : orders) {
                processOrderData(order);
            }
            return orders;
        }, executorService);
    }
    
    // Performance issue: Method that creates memory leak
    public List<Order> getCachedOrders() {
        // Performance issue: Returns static cache that grows indefinitely
        return new ArrayList<>(orderCache);
    }
}
