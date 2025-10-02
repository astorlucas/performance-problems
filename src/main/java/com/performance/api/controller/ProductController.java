package com.performance.api.controller;

import com.performance.api.entity.Product;
import com.performance.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            // Performance issue: No pagination, loads all products
            List<Product> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        try {
            // Performance issue: Loads product with all related data
            Optional<Product> product = productService.getProductById(id);
            return product.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        try {
            // Performance issue: Creates product with large image data
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product productDetails) {
        try {
            // Performance issue: Loads product with all related data before update
            Product updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            // Performance issue: Loads product with all related data before deletion
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        try {
            // Performance issue: Multiple database queries for search
            List<Product> products = productService.searchProducts(keyword);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        try {
            // Performance issue: Loads all products with full order details
            List<Product> products = productService.getProductsByCategory(category);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice, 
            @RequestParam BigDecimal maxPrice) {
        try {
            // Performance issue: Loads all products with full order details
            List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        try {
            // Performance issue: Loads all products with full order details
            List<Product> products = productService.getAvailableProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/with-images")
    public ResponseEntity<List<Product>> getProductsWithImages() {
        try {
            // Performance issue: Loads all products with large image data
            List<Product> products = productService.getProductsWithImages();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/async")
    public ResponseEntity<CompletableFuture<List<Product>>> getAllProductsAsync() {
        try {
            // Performance issue: Async method that can cause resource exhaustion
            CompletableFuture<List<Product>> products = productService.getAllProductsAsync();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/cached")
    public ResponseEntity<List<Product>> getCachedProducts() {
        try {
            // Performance issue: Returns static cache that grows indefinitely
            List<Product> products = productService.getCachedProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Product service is running");
    }
    
    @GetMapping("/stress-test")
    public ResponseEntity<String> stressTest() {
        try {
            // Performance issue: ENHANCED - Triggers multiple performance problems at once
            // This endpoint will cause significant CPU and memory consumption
            
            // 1. Load all products (N+1 queries)
            List<Product> products = productService.getAllProducts();
            
            // 2. Process each product multiple times (CPU intensive)
            for (int round = 0; round < 3; round++) {
                for (Product product : products) {
                    // Simulate heavy processing
                    for (int i = 0; i < 1000; i++) {
                        String processed = product.getName() + "_stress_" + round + "_" + i;
                        Math.sqrt(i * round + 1);
                    }
                }
            }
            
            // 3. Create additional products to trigger memory leaks
            for (int i = 0; i < 10; i++) {
                Product stressProduct = new Product(
                    "Stress Test Product " + i,
                    "This is a stress test product that will consume memory and CPU",
                    new BigDecimal("99.99"),
                    "Stress Test",
                    100
                );
                productService.createProduct(stressProduct);
            }
            
            // 4. Trigger async operations (thread pool exhaustion)
            CompletableFuture<List<Product>> asyncProducts = productService.getAllProductsAsync();
            
            return ResponseEntity.ok("Stress test completed. Check memory and CPU usage!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Stress test failed: " + e.getMessage());
        }
    }
}
