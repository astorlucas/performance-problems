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
    
    // Performance issue: Method that creates memory leak
    private String generateLargeImageData() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            sb.append("Image data ").append(i).append(": base64encodeddata...\n");
        }
        return sb.toString();
    }
    
    // Performance issue: Inefficient string concatenation
    private String generateSearchKeywords(Product product) {
        String keywords = "";
        for (int i = 0; i < 10000; i++) {
            keywords += product.getName() + " " + product.getCategory() + " " + i + " ";
        }
        return keywords;
    }
    
    // Performance issue: Inefficient processing method
    private void processProductData(Product product) {
        // Performance issue: CPU-intensive operation
        for (int i = 0; i < 2000; i++) {
            String processed = product.getName() + "_processed_" + i;
            // Simulate processing
        }
        
        // Performance issue: Adds to static cache without cleanup
        productCache.add(product);
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
    
    // Performance issue: Async method that can cause resource exhaustion
    public CompletableFuture<List<Product>> getAllProductsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            // Performance issue: CPU-intensive operation in async context
            List<Product> products = productRepository.findAll();
            for (Product product : products) {
                processProductData(product);
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
