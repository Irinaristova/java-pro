package com.task5.repository;

import com.task5.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    List<User> findByIsActiveTrue();
    
    List<User> findByIsActiveFalse();
    
    List<User> findByAgeGreaterThan(Integer age);
    
    List<User> findByAgeBetween(Integer ageFrom, Integer ageTo);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    long countByIsActiveTrue();
    
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword%")
    List<User> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT u FROM User u WHERE u.age >= :minAge ORDER BY u.age DESC")
    List<User> findUsersByMinAge(@Param("minAge") Integer minAge);
    
    @Query("SELECT u.username FROM User u WHERE u.isActive = true")
    List<String> findAllActiveUsernames();
    
    @Query("SELECT AVG(u.age) FROM User u WHERE u.isActive = true")
    Double getAverageAgeOfActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.age >= :minAge AND u.age <= :maxAge")
    long countUsersInAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);
    
    
    @Query(value = "SELECT * FROM users WHERE age > :minAge LIMIT :limit", nativeQuery = true)
    List<User> findTopUsersByAge(@Param("minAge") Integer minAge, @Param("limit") Integer limit);

    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isActive = false WHERE u.id = :id")
    void deactivateUser(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isActive = true WHERE u.id = :id")
    void activateUser(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.isActive = false")
    int deleteInactiveUsers();
    
    Page<User> findByIsActiveTrue(Pageable pageable);
    
    Page<User> findByAgeGreaterThan(Integer age, Pageable pageable);
}