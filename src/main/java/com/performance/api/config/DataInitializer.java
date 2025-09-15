package com.performance.api.config;

import com.performance.api.entity.Order;
import com.performance.api.entity.OrderItem;
import com.performance.api.entity.Product;
import com.performance.api.entity.User;
import com.performance.api.repository.OrderRepository;
import com.performance.api.repository.ProductRepository;
import com.performance.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize data if database is empty
        if (userRepository.count() == 0) {
            initializeData();
        }
    }

    private void initializeData() {
        // Create users
        User user1 = new User("john_doe", "john.doe@example.com", "John", "Doe");
        User user2 = new User("jane_smith", "jane.smith@example.com", "Jane", "Smith");
        User user3 = new User("bob_wilson", "bob.wilson@example.com", "Bob", "Wilson");
        
        userRepository.saveAll(Arrays.asList(user1, user2, user3));

        // Create products
        Product product1 = new Product("Laptop Pro", "High-performance laptop for professionals", new BigDecimal("1299.99"), "Electronics", 50);
        Product product2 = new Product("Wireless Mouse", "Ergonomic wireless mouse", new BigDecimal("29.99"), "Electronics", 200);
        Product product3 = new Product("Mechanical Keyboard", "RGB mechanical keyboard", new BigDecimal("149.99"), "Electronics", 75);
        
        productRepository.saveAll(Arrays.asList(product1, product2, product3));

        // Create orders
        Order order1 = new Order(user1.getId(), new BigDecimal("1329.98"), Order.OrderStatus.PENDING);
        Order order2 = new Order(user2.getId(), new BigDecimal("179.98"), Order.OrderStatus.CONFIRMED);
        
        orderRepository.saveAll(Arrays.asList(order1, order2));

        // Create order items
        OrderItem item1 = new OrderItem(order1.getId(), product1.getId(), 1, product1.getPrice());
        OrderItem item2 = new OrderItem(order1.getId(), product2.getId(), 1, product2.getPrice());
        OrderItem item3 = new OrderItem(order2.getId(), product3.getId(), 1, product3.getPrice());
        
        order1.setOrderItems(Arrays.asList(item1, item2));
        order2.setOrderItems(Arrays.asList(item3));
        
        orderRepository.saveAll(Arrays.asList(order1, order2));
    }
}
