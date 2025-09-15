package com.performance.api.repository;

import com.performance.api.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrderId(Long orderId);
    
    List<OrderItem> findByProductId(Long productId);
    
    // Performance issue: Query that loads all order items without pagination
    @Query("SELECT oi FROM OrderItem oi ORDER BY oi.createdAt DESC")
    List<OrderItem> findAllOrderItemsOrderedByDate();
    
    // Performance issue: Complex query with multiple JOINs
    @Query("SELECT oi FROM OrderItem oi LEFT JOIN FETCH oi.order o LEFT JOIN FETCH oi.product p LEFT JOIN FETCH o.user u")
    List<OrderItem> findAllOrderItemsWithFullDetails();
    
    // Performance issue: Query that uses subquery instead of JOIN
    @Query("SELECT oi FROM OrderItem oi WHERE oi.productId IN (SELECT p.id FROM Product p WHERE p.category = :category)")
    List<OrderItem> findOrderItemsByProductCategory(@Param("category") String category);
    
    // Performance issue: Query without proper indexing on price range
    @Query("SELECT oi FROM OrderItem oi WHERE oi.unitPrice BETWEEN :minPrice AND :maxPrice")
    List<OrderItem> findOrderItemsByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    // Performance issue: Query that loads unnecessary data
    @Query("SELECT oi FROM OrderItem oi WHERE oi.quantity > :minQuantity")
    List<OrderItem> findOrderItemsByMinQuantity(@Param("minQuantity") Integer minQuantity);
    
    // Performance issue: Query that uses multiple OR conditions without proper indexing
    @Query("SELECT oi FROM OrderItem oi WHERE oi.quantity = 1 OR oi.quantity = 2 OR oi.quantity = 3")
    List<OrderItem> findOrderItemsBySpecificQuantities();
    
    // Performance issue: Query that loads all order items for a product without pagination
    @Query("SELECT oi FROM OrderItem oi WHERE oi.productId = :productId ORDER BY oi.createdAt DESC")
    List<OrderItem> findProductOrderItemsOrderedByDate(@Param("productId") Long productId);
}
