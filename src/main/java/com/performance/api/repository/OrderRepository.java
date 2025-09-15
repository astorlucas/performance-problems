package com.performance.api.repository;

import com.performance.api.entity.Order;
import com.performance.api.entity.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByUserId(Long userId);
    
    List<Order> findByStatus(OrderStatus status);
    
    // Performance issue: Query that loads all orders without pagination
    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findAllOrdersOrderedByDate();
    
    // Performance issue: Complex query with multiple JOINs
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user u LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product p")
    List<Order> findAllOrdersWithFullDetails();
    
    // Performance issue: Query that uses subquery instead of JOIN
    @Query("SELECT o FROM Order o WHERE o.userId IN (SELECT u.id FROM User u WHERE u.email LIKE :domain)")
    List<Order> findOrdersByUserEmailDomain(@Param("domain") String domain);
    
    // Performance issue: Query without proper indexing on date range
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Performance issue: Query that loads unnecessary data
    @Query("SELECT o FROM Order o WHERE o.totalAmount > :minAmount")
    List<Order> findOrdersByMinAmount(@Param("minAmount") BigDecimal minAmount);
    
    // Performance issue: Query that uses LIKE without proper indexing
    @Query("SELECT o FROM Order o WHERE o.orderNotes LIKE :keyword")
    List<Order> findOrdersByNotesKeyword(@Param("keyword") String keyword);
    
    // Performance issue: Query that loads all orders with their notes (large data)
    @Query("SELECT o FROM Order o WHERE o.orderNotes IS NOT NULL")
    List<Order> findOrdersWithNotes();
    
    // Performance issue: Query that uses multiple OR conditions without proper indexing
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' OR o.status = 'CONFIRMED' OR o.status = 'SHIPPED'")
    List<Order> findActiveOrders();
    
    // Performance issue: Query that loads all orders for a user without pagination
    @Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.orderDate DESC")
    List<Order> findUserOrdersOrderedByDate(@Param("userId") Long userId);
}
