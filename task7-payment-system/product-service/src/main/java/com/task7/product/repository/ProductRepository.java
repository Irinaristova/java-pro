package com.task7.product.repository;

import com.task7.product.entity.Product;
import com.task7.product.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByUserId(Long userId);
    
    List<Product> findByUserIdAndIsActiveTrue(Long userId);
    
    List<Product> findByUserIdAndType(Long userId, ProductType type);
    
    Optional<Product> findByAccountNumber(String accountNumber);
    
    boolean existsByAccountNumber(String accountNumber);
    
    @Query("SELECT COALESCE(SUM(p.balance), 0) FROM Product p WHERE p.user.id = :userId AND p.isActive = true")
    BigDecimal getTotalBalanceByUserId(@Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.balance = :balance WHERE p.id = :id")
    int updateBalance(@Param("id") Long id, @Param("balance") BigDecimal balance);
}