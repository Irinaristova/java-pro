package com.testframework.core;

import com.testframework.annotations.*;
import com.testframework.exceptions.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class TestRunner {

    public static Map<TestResult, List<Test>> runTests(Class<?> c) {
        Map<TestResult, List<Test>> results = new HashMap<>();
        for (TestResult tr : TestResult.values()) {
            results.put(tr, new ArrayList<>());
        }

        try {
            validateTestClass(c);
            
            List<Method> beforeSuiteMethods = findMethodsWithAnnotation(c, BeforeSuite.class, true);
            List<Method> afterSuiteMethods = findMethodsWithAnnotation(c, AfterSuite.class, true);
            List<Method> beforeEachMethods = findMethodsWithAnnotation(c, BeforeEach.class, false);
            List<Method> afterEachMethods = findMethodsWithAnnotation(c, AfterEach.class, false);
            List<TestMethodInfo> testMethods = collectTestMethods(c);

            Object testInstance = c.getDeclaredConstructor().newInstance();

            executeMethods(beforeSuiteMethods, null);

            for (TestMethodInfo testInfo : testMethods) {
                if (testInfo.disabled) {
                    results.get(TestResult.SKIPPED).add(
                            new Test(TestResult.SKIPPED, testInfo.name, null)
                    );
                    continue;
                }

                TestResult methodResult = TestResult.SUCCESS;
                Throwable exception = null;

                try {
                    executeMethods(beforeEachMethods, testInstance);
                    
                    try {
                        testInfo.method.invoke(testInstance);
                    } catch (Throwable e) {
                        Throwable cause = e.getCause();
                        if (cause instanceof TestAssertionError) {
                            methodResult = TestResult.FAILED;
                            exception = cause;
                        } else {
                            methodResult = TestResult.ERROR;
                            exception = cause != null ? cause : e;
                        }
                    }
                    
                    executeMethods(afterEachMethods, testInstance);
                } catch (Throwable e) {
                    methodResult = TestResult.ERROR;
                    exception = e;
                }

                results.get(methodResult).add(
                        new Test(methodResult, testInfo.name, exception)
                );
            }

            executeMethods(afterSuiteMethods, null);

        } catch (Throwable e) {
            throw new BadTestClassError("Failed to run tests: " + e.getMessage());
        }

        return results;
    }

    private static class TestMethodInfo {
        Method method;
        String name;
        int priority;
        boolean disabled;

        TestMethodInfo(Method method, String name, int priority, boolean disabled) {
            this.method = method;
            this.name = name;
            this.priority = priority;
            this.disabled = disabled;
        }
    }

    private static List<TestMethodInfo> collectTestMethods(Class<?> c) {
        List<TestMethodInfo> testMethods = new ArrayList<>();

        for (Method method : c.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Test.class)) {
                if (Modifier.isStatic(method.getModifiers())) {
                    throw new BadTestClassError(
                        String.format("@Test method '%s' must not be static", method.getName())
                    );
                }

                Test testAnnotation = method.getAnnotation(Test.class);
                String testName = testAnnotation.name().isEmpty() 
                    ? method.getName() 
                    : testAnnotation.name();
                int priority = testAnnotation.priority();
                boolean disabled = method.isAnnotationPresent(Disabled.class);

                testMethods.add(new TestMethodInfo(method, testName, priority, disabled));
            }
        }

        testMethods.sort((t1, t2) -> {
            if (t1.priority != t2.priority) {
                return Integer.compare(t2.priority, t1.priority);
            }
            return t1.method.getName().compareTo(t2.method.getName());
        });

        return testMethods;
    }

    private static List<Method> findMethodsWithAnnotation(Class<?> c, 
                                                            Class<? extends java.lang.annotation.Annotation> annotation,
                                                            boolean mustBeStatic) {
        List<Method> methods = new ArrayList<>();
        for (Method method : c.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                boolean isStatic = Modifier.isStatic(method.getModifiers());
                if (mustBeStatic && !isStatic) {
                    throw new BadTestClassError(
                        String.format("@%s method '%s' must be static",
                                annotation.getSimpleName(), method.getName())
                    );
                }
                if (!mustBeStatic && isStatic) {
                    throw new BadTestClassError(
                        String.format("@%s method '%s' must not be static",
                                annotation.getSimpleName(), method.getName())
                    );
                }
                methods.add(method);
            }
        }
        return methods;
    }

    private static void executeMethods(List<Method> methods, Object target) throws Throwable {
        for (Method method : methods) {
            try {
                if (Modifier.isStatic(method.getModifiers())) {
                    method.invoke(null);
                } else {
                    method.invoke(target);
                }
            } catch (Throwable e) {
                throw e.getCause() != null ? e.getCause() : e;
            }
        }
    }

    private static void validateTestClass(Class<?> c) {
        try {
            c.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new BadTestClassError(
                String.format("Test class '%s' must have a default constructor", c.getName())
            );
        }
    }
}