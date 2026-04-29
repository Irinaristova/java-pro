package com.testframework.core;

public class Test {
    private final TestResult result;
    private final String name;
    private final Throwable exception;

    public Test(TestResult result, String name, Throwable exception) {
        this.result = result;
        this.name = name;
        this.exception = exception;
    }

    public TestResult getResult() {
        return result;
    }

    public String getName() {
        return name;
    }

    public Throwable getException() {
        return exception;
    }

    @Override
    public String toString() {
        return String.format("Test{name='%s', result=%s, exception=%s}",
                name, result, exception != null ? exception.getMessage() : "null");
    }
}