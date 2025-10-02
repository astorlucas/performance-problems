package com.performance.api.service;

import com.performance.api.entity.Product;
import com.performance.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    // Performance issue: Fixed thread pool that can cause resource exhaustion
    private final ExecutorService executorService = Executors.newFixedThreadPool(50);
    
    // Performance issue: Memory leak - static list that grows indefinitely
    private static final List<Product> productCache = new ArrayList<>();
    
    public List<Product> getAllProducts() {
        // Performance issue: N+1 query problem - loads all products with their order items
        List<Product> products = productRepository.findAll();
        
        // Performance issue: Unnecessary processing for each product
        for (Product product : products) {
            processProductData(product);
        }
        
        return products;
    }
    
    public Optional<Product> getProductById(Long id) {
        // Performance issue: Always loads product with order items (eager loading)
        return productRepository.findById(id);
    }
    
    public Product createProduct(Product product) {
        // Performance issue: Creates large image data for every product
        product.setProductImages(generateLargeImageData());
        product.setSearchKeywords(generateSearchKeywords(product));
        
        // Performance issue: Adds to static cache without cleanup
        productCache.add(product);
        
        return productRepository.save(product);
    }
    
    public Product updateProduct(Long id, Product productDetails) {
        // Performance issue: Loads product with all related data
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Performance issue: Updates image data even if not needed
        product.setProductImages(generateLargeImageData());
        product.setSearchKeywords(generateSearchKeywords(productDetails));
        
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setCategory(productDetails.getCategory());
        product.setStockQuantity(productDetails.getStockQuantity());
        
        return productRepository.save(product);
    }
    
    public void deleteProduct(Long id) {
        // Performance issue: Loads product with all related data before deletion
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Performance issue: Processes product data before deletion
        processProductData(product);
        
        productRepository.delete(product);
    }
    
    public List<Product> searchProducts(String keyword) {
        // Performance issue: Multiple database queries instead of one optimized query
        List<Product> products = new ArrayList<>();
        products.addAll(productRepository.findByNameContaining(keyword));
        products.addAll(productRepository.searchProductsByKeyword("%" + keyword + "%"));
        
        // Performance issue: Removes duplicates inefficiently
        return removeDuplicates(products);
    }
    
    public List<Product> getProductsByCategory(String category) {
        // Performance issue: Loads all products with full order details
        return productRepository.findByCategory(category);
    }
    
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        // Performance issue: Loads all products with full order details
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    public List<Product> getAvailableProducts() {
        // Performance issue: Loads all products with full order details
        return productRepository.findAvailableProducts();
    }
    
    public List<Product> getProductsWithImages() {
        // Performance issue: Loads all products with large image data
        return productRepository.findProductsWithImages();
    }
    
    // Performance issue: Method that creates memory leak - ENHANCED
    private String generateLargeImageData() {
        StringBuilder sb = new StringBuilder();
        // Increased from 100,000 to 500,000 iterations for more noticeable memory consumption
        for (int i = 0; i < 500000; i++) {
            sb.append("Image data ").append(i).append(": base64encodeddata...");
            // Add more data per iteration to increase memory footprint
            sb.append("Additional metadata for image ").append(i).append(" with detailed information...");
            sb.append("More base64 encoded content that takes up significant memory space...\n");
        }
        return sb.toString();
    }
    
    // Performance issue: Inefficient string concatenation
    private String generateSearchKeywords(Product product) {
        // Generate reasonable search keywords within 255 character limit
        StringBuilder keywords = new StringBuilder();
        
        // Add basic product info
        keywords.append(product.getName()).append(" ");
        keywords.append(product.getCategory()).append(" ");
        if (product.getDescription() != null) {
            keywords.append(product.getDescription()).append(" ");
        }
        
        // Add some variations for better search
        keywords.append(product.getName().toLowerCase()).append(" ");
        keywords.append(product.getCategory().toLowerCase()).append(" ");
        
        // Add price range keywords
        if (product.getPrice() != null) {
            if (product.getPrice().compareTo(new BigDecimal("50")) < 0) {
                keywords.append("budget affordable cheap ");
            } else if (product.getPrice().compareTo(new BigDecimal("200")) < 0) {
                keywords.append("mid-range moderate ");
            } else {
                keywords.append("premium expensive luxury ");
            }
        }
        
        // Add stock status
        if (product.getStockQuantity() != null) {
            if (product.getStockQuantity() > 50) {
                keywords.append("in-stock available ");
            } else if (product.getStockQuantity() > 0) {
                keywords.append("limited stock ");
            } else {
                keywords.append("out-of-stock ");
            }
        }
        
        String result = keywords.toString().trim();
        
        // Ensure we don't exceed 255 characters
        if (result.length() > 255) {
            result = result.substring(0, 252) + "...";
        }
        
        return result;
    }
    
    // Performance issue: Inefficient processing method - ENHANCED
    private void processProductData(Product product) {
        // Performance issue: CPU-intensive operation - increased from 2000 to 10000 iterations
        for (int i = 0; i < 10000; i++) {
            String processed = product.getName() + "_processed_" + i;
            // Simulate more CPU-intensive processing
            for (int j = 0; j < 100; j++) {
                processed += "_subprocessed_" + j;
                // Additional CPU work
                Math.sqrt(i * j + 1);
            }
        }
        
        // Performance issue: Adds to static cache without cleanup - ENHANCED
        // Add multiple copies to make memory leak more noticeable
        productCache.add(product);
        productCache.add(new Product(product.getName() + "_copy1", product.getDescription(), 
                                   product.getPrice(), product.getCategory(), product.getStockQuantity()));
        productCache.add(new Product(product.getName() + "_copy2", product.getDescription(), 
                                   product.getPrice(), product.getCategory(), product.getStockQuantity()));
    }
    
    // Performance issue: Inefficient duplicate removal
    private List<Product> removeDuplicates(List<Product> products) {
        List<Product> uniqueProducts = new ArrayList<>();
        for (Product product : products) {
            boolean found = false;
            for (Product uniqueProduct : uniqueProducts) {
                if (uniqueProduct.getId().equals(product.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                uniqueProducts.add(product);
            }
        }
        return uniqueProducts;
    }
    
    // Performance issue: Async method that can cause resource exhaustion - ENHANCED
    public CompletableFuture<List<Product>> getAllProductsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            // Performance issue: CPU-intensive operation in async context - ENHANCED
            List<Product> products = productRepository.findAll();
            
            // Add more CPU-intensive work to make it more noticeable
            for (Product product : products) {
                processProductData(product);
                // Additional CPU work to make performance issues more visible
                for (int i = 0; i < 1000; i++) {
                    // Simulate complex calculations
                    double result = 0;
                    for (int j = 0; j < 100; j++) {
                        result += Math.pow(i, 2) + Math.sin(j) + Math.cos(i * j);
                    }
                }
            }
            return products;
        }, executorService);
    }
    
    // Performance issue: Method that creates memory leak
    public List<Product> getCachedProducts() {
        // Performance issue: Returns static cache that grows indefinitely
        return new ArrayList<>(productCache);
    }
}
