package com.task7.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
        System.out.println("=".repeat(60));
        System.out.println("Product Service started on port 8081");
        System.out.println("=".repeat(60));
    }
}