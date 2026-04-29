package com.task3;

import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=".repeat(70));
        System.out.println("ЗАДАНИЕ 3: СОБСТВЕННЫЙ ПУЛ ПОТОКОВ");
        System.out.println("=".repeat(70));

        // ========== Демонстрация 1: Базовая работа пула ==========
        System.out.println("\n>>> Демонстрация 1: Базовая работа пула (3 потока, 8 задач)");
        demonstration1();

        // ========== Демонстрация 2: Выключение пула и отвергнутые задачи ==========
        System.out.println("\n>>> Демонстрация 2: shutdown() и отклонение новых задач");
        demonstration2();

        // ========== Демонстрация 3: awaitTermination ==========
        System.out.println("\n>>> Демонстрация 3: awaitTermination()");
        demonstration3();

        // ========== Демонстрация 4: Обработка ошибок в задачах ==========
        System.out.println("\n>>> Демонстрация 4: Обработка ошибок в задачах");
        demonstration4();

        // ========== Демонстрация 5: Конкурентное добавление задач ==========
        System.out.println("\n>>> Демонстрация 5: Конкурентное добавление задач");
        demonstration5();

        System.out.println("\n" + "=".repeat(70));
        System.out.println("ВЫПОЛНЕНИЕ ЗАДАНИЯ 3 ЗАВЕРШЕНО");
        System.out.println("=".repeat(70));
    }

    /**
     * Демонстрация базовой работы пула потоков
     */
    private static void demonstration1() throws InterruptedException {
        System.out.println("Создаём пул с 3 рабочими потоками...");
        CustomThreadPool threadPool = new CustomThreadPool(3);

        System.out.println("Добавляем 8 задач с разной задержкой:");
        for (int i = 1; i <= 8; i++) {
            long delay = (i % 3 == 0) ? 300 : 100;
            threadPool.execute(TaskExample.createDelayedTask("Task-" + i, delay));
            System.out.printf("  Задача Task-%d добавлена в очередь (задержка %d мс)%n", i, delay);
            Thread.sleep(20); // Небольшая пауза между добавлениями
        }

        Thread.sleep(2000); // Ждём выполнения всех задач

        System.out.println("Текущее состояние: ");
        System.out.println("  Queue size: " + threadPool.getQueueSize());
        System.out.println("  Active tasks: " + threadPool.getActiveTasksCount());
        System.out.println("  Is shutdown: " + threadPool.isShutdown());
        System.out.println("  Is terminated: " + threadPool.isTerminated());

        threadPool.shutdown();
        threadPool.awaitTermination();

        System.out.println("После shutdown и awaitTermination:");
        System.out.println("  Is shutdown: " + threadPool.isShutdown());
        System.out.println("  Is terminated: " + threadPool.isTerminated());
    }

    /**
     * Демонстрация работы shutdown и отклонения новых задач
     */
    private static void demonstration2() {
        CustomThreadPool threadPool = new CustomThreadPool(2);

        System.out.println("Добавляем 3 быстрых задачи:");
        for (int i = 1; i <= 3; i++) {
            threadPool.execute(TaskExample.createQuickTask("QuickTask-" + i));
        }

        System.out.println("Вызываем shutdown()...");
        threadPool.shutdown();

        System.out.println("Пытаемся добавить новую задачу после shutdown:");
        try {
            threadPool.execute(() -> System.out.println("Эта задача не должна выполниться"));
        } catch (IllegalStateException e) {
            System.out.println("  Ожидаемое исключение: " + e.getMessage());
        }

        System.out.println("Состояние пула:");
        System.out.println("  isShutdown: " + threadPool.isShutdown());
        System.out.println("  isTerminated: " + threadPool.isTerminated());
    }

    /**
     * Демонстрация awaitTermination
     */
    private static void demonstration3() throws InterruptedException {
        CustomThreadPool threadPool = new CustomThreadPool(2);

        System.out.println("Добавляем 5 долгих задач (по 400 мс каждая):");
        for (int i = 1; i <= 5; i++) {
            threadPool.execute(TaskExample.createDelayedTask("LongTask-" + i, 400));
        }

        System.out.println("Вызываем shutdown()...");
        threadPool.shutdown();

        System.out.println("Ожидаем завершения всех задач (awaitTermination)...");
        long startTime = System.currentTimeMillis();
        threadPool.awaitTermination();
        long endTime = System.currentTimeMillis();

        System.out.printf("  Все задачи завершены за %d мс%n", (endTime - startTime));
        System.out.println("  isTerminated: " + threadPool.isTerminated());
    }

    /**
     * Демонстрация обработки ошибок в задачах
     */
    private static void demonstration4() throws InterruptedException {
        CustomThreadPool threadPool = new CustomThreadPool(2);

        System.out.println("Добавляем задачи, часть из которых падает с ошибкой:");
        threadPool.execute(TaskExample.createFailingTask("SuccessTask-1", false));
        threadPool.execute(TaskExample.createFailingTask("FailingTask-1", true));
        threadPool.execute(TaskExample.createFailingTask("SuccessTask-2", false));
        threadPool.execute(TaskExample.createFailingTask("FailingTask-2", true));
        threadPool.execute(TaskExample.createFailingTask("SuccessTask-3", false));

        Thread.sleep(1500); // Ждём выполнения

        threadPool.shutdown();
        threadPool.awaitTermination();

        System.out.println("Все задачи выполнены (упавшие задачи НЕ остановили пул)");
    }

    /**
     * Демонстрация конкурентного добавления задач из разных потоков
     */
    private static void demonstration5() throws InterruptedException {
        CustomThreadPool threadPool = new CustomThreadPool(4);
        AtomicInteger counter = new AtomicInteger(0);

        System.out.println("Запускаем 5 потоков, каждый добавляет по 10 задач:");
        Thread[] producers = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int producerId = i;
            producers[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    threadPool.execute(TaskExample.createCounterTask(
                            "P" + producerId + "-T" + j, counter));
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.printf("  Продюсер %d добавил 10 задач%n", producerId);
            });
            producers[i].start();
        }

        // Ждём завершения всех продюсеров
        for (Thread producer : producers) {
            producer.join();
        }

        System.out.println("Все 50 задач добавлены в очередь.");
        System.out.println("Текущий размер очереди: " + threadPool.getQueueSize());

        Thread.sleep(2000); // Ждём выполнения

        threadPool.shutdown();
        threadPool.awaitTermination();

        System.out.println("Всего выполнено задач: " + counter.get() + " (ожидалось 50)");
    }
}