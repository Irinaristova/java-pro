package com.task6.runner;

import com.task6.service.ProductService;
import com.task6.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseRunner implements CommandLineRunner {

    private final UserService userService;
    private final ProductService productService;

    @Override
    public void run(String... args) {
        System.out.println("=".repeat(70));
        System.out.println("ЗАДАНИЕ 6: ПРОДУКТЫ КЛИЕНТОВ + REST API");
        System.out.println("=".repeat(70));

        System.out.println("\n>>> ПОЛЬЗОВАТЕЛИ В БД:");
        userService.getAllUsers().forEach(user -> 
            System.out.printf("   • %s (ID: %d)%n", user.getUsername(), user.getId())
        );

        System.out.println("\n>>> ПРОДУКТЫ В БД:");
        userService.getAllUsers().forEach(user -> {
            System.out.printf("%n   Продукты пользователя %s:%n", user.getUsername());
            productService.getProductsByUserId(user.getId()).forEach(product ->
                System.out.printf("      - %s: %s, баланс: %.2f ₽%n", 
                    product.getType(), product.getAccountNumber(), product.getBalance())
            );
        });

        System.out.println("\n" + "=".repeat(35));
        System.out.println("REST API ENDPOINTS");
        System.out.println("=".repeat(35));

        System.out.println("\n   GET    /api/products/user/{userId}");
        System.out.println("   GET    /api/products/{productId}");
        System.out.println("   GET    /api/products/account/{accountNumber}");
        System.out.println("   GET    /api/products/user/{userId}/type/{type}");
        System.out.println("   GET    /api/products/user/{userId}/total-balance");
        System.out.println("   GET    /api/products/user/{userId}/count");
        System.out.println("   POST   /api/products");
        System.out.println("   PUT    /api/products/{productId}/balance?balance={amount}");
        System.out.println("   DELETE /api/products/{productId}");

        System.out.println("\n" + "=".repeat(70));
        System.out.println("Сервер запущен на порту 8080");
        System.out.println("Примеры запросов:");
        System.out.println("  curl http://localhost:8080/api/products/user/1");
        System.out.println("  curl http://localhost:8080/api/products/1");
        System.out.println("=".repeat(70));
    }
}