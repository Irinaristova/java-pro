package com.task3;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Собственная реализация пула потоков.
 * 
 * Особенности:
 * - При создании сразу инициализирует и запускает указанное количество потоков
 * - Задачи хранятся в очереди LinkedList
 * - Поддержка shutdown() - новые задачи не принимаются
 * - Поддержка awaitTermination() - ожидание завершения всех задач
 */
public class CustomThreadPool {
    private final int poolSize;
    private final ThreadWorker[] workers;
    private final Queue<Runnable> taskQueue;
    private final ReentrantLock lock;
    private final Condition taskAvailable;
    private final Condition terminationCondition;
    private volatile boolean isShutdown;
    private volatile boolean isTerminated;
    private int activeTasks;

    public CustomThreadPool(int poolSize) {
        if (poolSize <= 0) {
            throw new IllegalArgumentException("Pool size must be positive");
        }

        this.poolSize = poolSize;
        this.workers = new ThreadWorker[poolSize];
        this.taskQueue = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.taskAvailable = lock.newCondition();
        this.terminationCondition = lock.newCondition();
        this.isShutdown = false;
        this.isTerminated = false;
        this.activeTasks = 0;

        initializeAndStartWorkers();
    }

    /**
     * Инициализация и запуск рабочих потоков
     */
    private void initializeAndStartWorkers() {
        for (int i = 0; i < poolSize; i++) {
            workers[i] = new ThreadWorker();
            workers[i].start();
        }
    }

    /**
     * Добавление задачи на выполнение
     */
    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("Task cannot be null");
        }

        lock.lock();
        try {
            if (isShutdown) {
                throw new IllegalStateException("Thread pool is already shutdown");
            }

            taskQueue.offer(task);
            activeTasks++;
            taskAvailable.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Завершение работы пула потоков
     * Новые задачи не принимаются
     */
    public void shutdown() {
        lock.lock();
        try {
            isShutdown = true;
            // Пробуждаем все ожидающие потоки
            taskAvailable.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Ожидание завершения всех задач в пуле
     */
    public void awaitTermination() throws InterruptedException {
        lock.lock();
        try {
            while (!isTerminated) {
                terminationCondition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Проверка, завершил ли пул свою работу
     */
    public boolean isTerminated() {
        lock.lock();
        try {
            return isTerminated;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Проверка, был ли пул остановлен (shutdown)
     */
    public boolean isShutdown() {
        lock.lock();
        try {
            return isShutdown;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Получение количества задач в очереди
     */
    public int getQueueSize() {
        lock.lock();
        try {
            return taskQueue.size();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Получение количества активных задач
     */
    public int getActiveTasksCount() {
        lock.lock();
        try {
            return activeTasks;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Проверка возможности завершения пула
     */
    private void checkTermination() {
        lock.lock();
        try {
            if (isShutdown && activeTasks == 0 && taskQueue.isEmpty()) {
                isTerminated = true;
                terminationCondition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Рабочий поток, который выполняет задачи из очереди
     */
    private class ThreadWorker extends Thread {
        @Override
        public void run() {
            while (true) {
                Runnable task = null;

                lock.lock();
                try {
                    // Ожидание задачи или завершения работы
                    while (!isShutdown && taskQueue.isEmpty()) {
                        taskAvailable.await();
                    }

                    // Выход из потока при shutdown и отсутствии задач
                    if (isShutdown && taskQueue.isEmpty()) {
                        break;
                    }

                    // Получение задачи из очереди
                    task = taskQueue.poll();
                    if (task != null) {
                        activeTasks--;
                    }
                } catch (InterruptedException e) {
                    // Восстанавливаем прерывание и выходим
                    Thread.currentThread().interrupt();
                    break;
                } finally {
                    lock.unlock();
                }

                // Выполнение задачи вне блокировки
                if (task != null) {
                    try {
                        task.run();
                    } catch (Exception e) {
                        System.err.println("Task execution error: " + e.getMessage());
                    } finally {
                        // Проверяем возможность завершения пула
                        checkTermination();
                    }
                }
            }

            // Поток завершает работу
            checkTermination();
        }
    }
}