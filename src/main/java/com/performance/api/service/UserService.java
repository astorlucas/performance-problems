package com.performance.api.service;

import com.performance.api.entity.User;
import com.performance.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Performance issue: Fixed thread pool that can cause resource exhaustion
    private final ExecutorService executorService = Executors.newFixedThreadPool(100);
    
    // Performance issue: Memory leak - static list that grows indefinitely
    private static final List<User> userCache = new ArrayList<>();
    
    public List<User> getAllUsers() {
        // Performance issue: N+1 query problem - loads all users with their orders
        List<User> users = userRepository.findAll();
        
        // Performance issue: Unnecessary processing for each user
        for (User user : users) {
            processUserData(user);
        }
        
        return users;
    }
    
    public Optional<User> getUserById(Long id) {
        // Performance issue: Always loads user with orders (eager loading)
        return userRepository.findById(id);
    }
    
    public User createUser(User user) {
        // Performance issue: Creates large profile data for every user - ENHANCED
        user.setProfileData(generateLargeProfileData());
        
        // Performance issue: Adds to static cache without cleanup - ENHANCED
        userCache.add(user);
        // Add multiple copies to make memory leak more noticeable
        userCache.add(new User(user.getUsername() + "_copy1", user.getEmail(), 
                             user.getFirstName(), user.getLastName()));
        userCache.add(new User(user.getUsername() + "_copy2", user.getEmail(), 
                             user.getFirstName(), user.getLastName()));
        
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, User userDetails) {
        // Performance issue: Loads user with all related data
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        
        // Performance issue: Updates profile data even if not needed
        user.setProfileData(generateLargeProfileData());
        
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        // Performance issue: Loads user with all related data before deletion
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        
        // Performance issue: Processes user data before deletion
        processUserData(user);
        
        userRepository.delete(user);
    }
    
    public List<User> searchUsers(String keyword) {
        // Performance issue: Multiple database queries instead of one optimized query
        List<User> users = new ArrayList<>();
        if (userRepository.findByUsername(keyword).isPresent()) {
            users.add(userRepository.findByUsername(keyword).get());
        }
        if (userRepository.findByEmail(keyword).isPresent()) {
            users.add(userRepository.findByEmail(keyword).get());
        }
        users.addAll(userRepository.findByFirstName(keyword));
        users.addAll(userRepository.findByLastNameContaining("%" + keyword + "%"));

        // Use Set to efficiently remove duplicates
        return new ArrayList<>(new LinkedHashSet<>(users));
    }
    
    public List<User> getUsersWithPendingOrders() {
        // Performance issue: Loads all users with full order details
        return userRepository.findUsersWithPendingOrders();
    }
    
    public List<User> getRecentUsers(String emailDomain) {
        // Performance issue: Loads all users with full order details
        java.time.LocalDateTime thirtyDaysAgo = java.time.LocalDateTime.now().minusDays(30);
        return userRepository.findRecentUsersByEmailDomain(emailDomain, thirtyDaysAgo);
    }
    
    // Performance issue: Method that creates memory leak - ENHANCED
    private String generateLargeProfileData() {
        StringBuilder sb = new StringBuilder();
        // Increased from 50,000 to 200,000 iterations for more noticeable memory consumption
        for (int i = 0; i < 200000; i++) {
            sb.append("Profile data line ").append(i).append(": This is a large profile data...");
            // Add more data per iteration
            sb.append(" Additional user preferences and settings data...");
            sb.append(" More detailed profile information that consumes significant memory...\n");
        }
        return sb.toString();
    }
    
    // Performance issue: Inefficient processing method - ENHANCED
    private void processUserData(User user) {
        // Performance issue: CPU-intensive operation - increased from 1000 to 5000 iterations
        for (int i = 0; i < 5000; i++) {
            String processed = user.getUsername() + "_processed_" + i;
            // Simulate more CPU-intensive processing
            for (int j = 0; j < 50; j++) {
                processed += "_subprocessed_" + j;
                // Additional CPU work
                Math.sqrt(i * j + 1);
            }
        }
        
        // Performance issue: Adds to static cache without cleanup - ENHANCED
        userCache.add(user);
        // Add more copies to make memory leak more noticeable
        userCache.add(new User(user.getUsername() + "_processed_copy", user.getEmail(), 
                             user.getFirstName(), user.getLastName()));
    }
    
    // Performance issue: Inefficient duplicate removal
    private List<User> removeDuplicates(List<User> users) {
        List<User> uniqueUsers = new ArrayList<>();
        for (User user : users) {
            boolean found = false;
            for (User uniqueUser : uniqueUsers) {
                if (uniqueUser.getId().equals(user.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                uniqueUsers.add(user);
            }
        }
        return uniqueUsers;
    }
    
    // Performance issue: Async method that can cause resource exhaustion
    public CompletableFuture<List<User>> getAllUsersAsync() {
        return CompletableFuture.supplyAsync(() -> {
            // Performance issue: CPU-intensive operation in async context
            List<User> users = userRepository.findAll();
            for (User user : users) {
                processUserData(user);
            }
            return users;
        }, executorService);
    }
    
    // Performance issue: Method that creates memory leak
    public List<User> getCachedUsers() {
        // Performance issue: Returns static cache that grows indefinitely
        return new ArrayList<>(userCache);
    }
}
