package com.performance.api.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;
    
    @NotBlank
    @Size(max = 50)
    private String category;
    
    @NotNull
    @Column(name = "stock_quantity")
    private Integer stockQuantity;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Performance issue: Eager loading of order items
    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference("product-items")
    private List<OrderItem> orderItems = new ArrayList<>();
    
    // Performance issue: Large object in memory
    @Lob
    @Column(name = "product_images")
    private String productImages;
    
    // Performance issue: Unnecessary field that's always loaded
    @Column(name = "search_keywords")
    @Size(max = 255)
    private String searchKeywords;
    
    public Product() {}
    
    public Product(String name, String description, BigDecimal price, String category, Integer stockQuantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stockQuantity = stockQuantity;
        // Temporarily disabled for testing
        // this.productImages = generateLargeImageData();
        // this.searchKeywords = generateSearchKeywords();
    }
    
    // Performance issue: Method that creates memory leak
    private String generateLargeImageData() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5000; i++) {
            sb.append("Image data ").append(i).append(": base64encodeddata...\n");
        }
        return sb.toString();
    }
    
    // Performance issue: Inefficient string concatenation
    private String generateSearchKeywords() {
        String keywords = "";
        for (int i = 0; i < 1000; i++) {
            keywords += name + " " + category + " " + i + " ";
        }
        return keywords;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
    
    public String getProductImages() {
        return productImages;
    }
    
    public void setProductImages(String productImages) {
        this.productImages = productImages;
    }
    
    public String getSearchKeywords() {
        return searchKeywords;
    }
    
    public void setSearchKeywords(String searchKeywords) {
        this.searchKeywords = searchKeywords;
    }
}
