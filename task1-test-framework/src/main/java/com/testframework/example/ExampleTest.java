package com.testframework.example;

import com.testframework.annotations.*;
import com.testframework.exceptions.TestAssertionError;

public class ExampleTest {

    @BeforeSuite
    public static void beforeSuite() {
        System.out.println("=== Before Suite ===");
    }

    @AfterSuite
    public static void afterSuite() {
        System.out.println("=== After Suite ===");
    }

    @BeforeEach
    public void beforeEach() {
        System.out.println("  Before Each");
    }

    @AfterEach
    public void afterEach() {
        System.out.println("  After Each");
    }

    @Test(priority = 10)
    public void testSuccess() {
        System.out.println("    Running testSuccess (priority 10)");
    }

    @Test(name = "Custom Test Name", priority = 8)
    public void testWithCustomName() {
        System.out.println("    Running testWithCustomName (priority 8)");
    }

    @Test(priority = 5)
    public void testFailed() {
        System.out.println("    Running testFailed (priority 5)");
        throw new TestAssertionError("Assertion failed!");
    }

    @Test(priority = 3)
    public void testError() {
        System.out.println("    Running testError (priority 3)");
        throw new RuntimeException("Unexpected error");
    }

    @Disabled
    @Test
    public void testSkipped() {
        System.out.println("    This will not run");
    }
}