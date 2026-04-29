package com.task7.product.entity;

public enum ProductType {
    CARD("Карта"),
    ACCOUNT("Счет");

    private final String description;

    ProductType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}