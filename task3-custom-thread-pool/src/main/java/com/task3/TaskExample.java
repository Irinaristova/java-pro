package com.task3;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Примеры задач для демонстрации работы пула потоков
 */
public class TaskExample {

    /**
     * Простая задача с задержкой
     */
    public static Runnable createDelayedTask(String name, long delayMs) {
        return () -> {
            System.out.printf("[%s] Задача '%s' начала выполнение в потоке %s%n",
                    getCurrentTime(), name, Thread.currentThread().getName());
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.printf("[%s] Задача '%s' была прервана%n", getCurrentTime(), name);
            }
            System.out.printf("[%s] Задача '%s' завершила выполнение%n",
                    getCurrentTime(), name);
        };
    }

    /**
     * Простая задача без задержки
     */
    public static Runnable createQuickTask(String name) {
        return () -> {
            System.out.printf("[%s] Задача '%s' выполняется в потоке %s%n",
                    getCurrentTime(), name, Thread.currentThread().getName());
        };
    }

    /**
     * Задача, которая может выбросить исключение
     */
    public static Runnable createFailingTask(String name, boolean fail) {
        return () -> {
            System.out.printf("[%s] Задача '%s' (willFail=%b) в потоке %s%n",
                    getCurrentTime(), name, fail, Thread.currentThread().getName());
            if (fail) {
                throw new RuntimeException("Искусственное исключение в задаче '" + name + "'");
            }
            System.out.printf("[%s] Задача '%s' успешно выполнена%n", getCurrentTime(), name);
        };
    }

    /**
     * Задача, считающая количество выполнений
     */
    public static Runnable createCounterTask(String name, AtomicInteger counter) {
        return () -> {
            int count = counter.incrementAndGet();
            System.out.printf("[%s] Задача '%s' выполнена %d раз(а) в потоке %s%n",
                    getCurrentTime(), name, count, Thread.currentThread().getName());
        };
    }

    /**
     * Получение текущего времени в формате HH:MM:SS.mmm
     */
    private static String getCurrentTime() {
        return java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }
}