package com.performance.api.repository;

import com.performance.api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByCategory(String category);
    
    List<Product> findByNameContaining(String name);
    
    // Performance issue: Query without proper indexing on price range
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    // Performance issue: Query that loads all products without pagination
    @Query("SELECT p FROM Product p ORDER BY p.price ASC")
    List<Product> findAllProductsOrderedByPrice();
    
    // Performance issue: Complex query with multiple JOINs that could be optimized
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.orderItems oi LEFT JOIN FETCH oi.order o LEFT JOIN FETCH o.user u")
    List<Product> findAllProductsWithOrderDetails();
    
    // Performance issue: Query that uses LIKE without proper indexing
    @Query("SELECT p FROM Product p WHERE p.description LIKE :keyword OR p.name LIKE :keyword")
    List<Product> searchProductsByKeyword(@Param("keyword") String keyword);
    
    // Performance issue: Query that loads unnecessary data
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0")
    List<Product> findAvailableProducts();
    
    // Performance issue: Query that uses subquery instead of JOIN
    @Query("SELECT p FROM Product p WHERE p.id IN (SELECT oi.productId FROM OrderItem oi WHERE oi.quantity > 5)")
    List<Product> findProductsWithHighQuantityOrders();
    
    // Performance issue: Query without proper WHERE clause optimization
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.price > :minPrice AND p.stockQuantity > 0 ORDER BY p.price")
    List<Product> findProductsByCategoryAndPriceAndStock(@Param("category") String category, 
                                                         @Param("minPrice") BigDecimal minPrice);
    
    // Performance issue: Query that loads all products with their images (large data)
    @Query("SELECT p FROM Product p WHERE p.productImages IS NOT NULL")
    List<Product> findProductsWithImages();
}
