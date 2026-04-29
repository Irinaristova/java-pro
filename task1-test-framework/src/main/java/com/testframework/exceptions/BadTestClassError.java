package com.testframework.exceptions;

public class BadTestClassError extends Error {
    public BadTestClassError(String message) {
        super(message);
    }
}