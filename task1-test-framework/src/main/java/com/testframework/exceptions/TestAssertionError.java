package com.testframework.exceptions;

public class TestAssertionError extends Error {
    public TestAssertionError(String message) {
        super(message);
    }
}