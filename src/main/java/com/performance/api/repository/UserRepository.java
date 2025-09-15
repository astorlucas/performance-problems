package com.performance.api.repository;

import com.performance.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    // Performance issue: N+1 query problem - this will trigger additional queries for each user's orders
    @Query("SELECT u FROM User u WHERE u.firstName = :firstName")
    List<User> findByFirstName(@Param("firstName") String firstName);
    
    // Performance issue: Inefficient query without proper indexing
    @Query("SELECT u FROM User u WHERE u.lastName LIKE :lastName")
    List<User> findByLastNameContaining(@Param("lastName") String lastName);
    
    // Performance issue: Complex query that could be optimized
    @Query("SELECT u FROM User u WHERE u.email LIKE :domain AND u.createdAt > :thirtyDaysAgo")
    List<User> findRecentUsersByEmailDomain(@Param("domain") String domain, @Param("thirtyDaysAgo") java.time.LocalDateTime thirtyDaysAgo);
    
    // Performance issue: Query that loads all users without pagination
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    List<User> findAllUsersOrderedByCreationDate();
    
    // Performance issue: Query that uses subquery instead of JOIN
    @Query("SELECT u FROM User u WHERE u.id IN (SELECT o.userId FROM Order o WHERE o.status = 'PENDING')")
    List<User> findUsersWithPendingOrders();
    
    // Performance issue: Query that loads unnecessary data
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product p")
    List<User> findAllUsersWithFullOrderDetails();
    
    // Performance issue: Query without proper WHERE clause optimization
    @Query("SELECT u FROM User u WHERE u.username = :username OR u.email = :email OR u.firstName = :firstName OR u.lastName = :lastName")
    List<User> findUsersByAnyField(@Param("username") String username, 
                                   @Param("email") String email, 
                                   @Param("firstName") String firstName, 
                                   @Param("lastName") String lastName);
}
