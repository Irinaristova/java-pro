package com.task4;

import com.task4.config.AppConfig;
import com.task4.model.User;
import com.task4.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("ЗАДАНИЕ 4: SPRING + JDBC + HIKARICP");
        System.out.println("=".repeat(70));

        // Создание Spring Context
        System.out.println("\n>>> Инициализация Spring Context...");
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        UserService userService = context.getBean(UserService.class);

        System.out.println("Spring Context успешно создан!");
        System.out.println("Bean UserService получен: " + userService.getClass().getSimpleName());

        // CREATE
        System.out.println("\n" + "-".repeat(50));
        System.out.println("1. CREATE операции (создание пользователей)");
        System.out.println("-".repeat(50));

        try {
            User user1 = userService.createUser("ivan_petrov");
            System.out.println("✓ Создан пользователь: " + user1);

            User user2 = userService.createUser("maria_sidorova");
            System.out.println("✓ Создан пользователь: " + user2);

            User user3 = userService.createUser("alexey_smirnov");
            System.out.println("✓ Создан пользователь: " + user3);

            User user4 = userService.createUser("elena_kozlova");
            System.out.println("✓ Создан пользователь: " + user4);

            User user5 = userService.createUser("dmitry_ivanov");
            System.out.println("✓ Создан пользователь: " + user5);
        } catch (Exception e) {
            System.out.println("✗ Ошибка при создании: " + e.getMessage());
        }

        // Попытка создать пользователя с уже существующим username
        System.out.println("\nПопытка создать дубликат пользователя 'ivan_petrov':");
        try {
            userService.createUser("ivan_petrov");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✗ Ожидаемая ошибка: " + e.getMessage());
        }

        // READ
        System.out.println("\n" + "-".repeat(50));
        System.out.println("2. READ операции (получение пользователей)");
        System.out.println("-".repeat(50));

        // Получение всех пользователей
        System.out.println("\nВсе пользователи в БД:");
        List<User> allUsers = userService.getAllUsers();
        allUsers.forEach(user -> System.out.println("  • " + user));

        // Получение пользователя по ID
        if (!allUsers.isEmpty()) {
            Long firstUserId = allUsers.get(0).getId();
            System.out.println("\nПоиск пользователя по ID=" + firstUserId + ":");
            Optional<User> foundById = userService.getUserById(firstUserId);
            foundById.ifPresentOrElse(
                    user -> System.out.println("  ✓ Найден: " + user),
                    () -> System.out.println("  ✗ Пользователь не найден")
            );
        }

        // Получение пользователя по username
        System.out.println("\nПоиск пользователя по username 'maria_sidorova':");
        Optional<User> foundByUsername = userService.getUserByUsername("maria_sidorova");
        foundByUsername.ifPresentOrElse(
                user -> System.out.println("  ✓ Найден: " + user),
                () -> System.out.println("  ✗ Пользователь не найден")
        );

        // Поиск несуществующего пользователя
        System.out.println("\nПоиск несуществующего пользователя (ID=9999):");
        Optional<User> notFound = userService.getUserById(9999L);
        System.out.println("  Результат: " + (notFound.isEmpty() ? "пусто (не найден)" : "найден"));

        // UPDATE
        System.out.println("\n" + "-".repeat(50));
        System.out.println("3. UPDATE операции (обновление пользователей)");
        System.out.println("-".repeat(50));

        System.out.println("\nОбновление пользователя 'ivan_petrov' -> 'ivan_petrov_updated':");
        try {
            Optional<User> userToUpdate = userService.getUserByUsername("ivan_petrov");
            if (userToUpdate.isPresent()) {
                Long userId = userToUpdate.get().getId();
                User updated = userService.updateUser(userId, "ivan_petrov_updated");
                System.out.println("  ✓ Обновлён: " + updated);
            } else {
                System.out.println("  ✗ Пользователь не найден");
            }
        } catch (Exception e) {
            System.out.println("  ✗ Ошибка: " + e.getMessage());
        }

        // Показать всех после обновления
        System.out.println("\nВсе пользователи после обновления:");
        userService.getAllUsers().forEach(user -> System.out.println("  • " + user));

        // DELETE
        System.out.println("\n" + "-".repeat(50));
        System.out.println("4. DELETE операции (удаление пользователей)");
        System.out.println("-".repeat(50));

        // Удаление по username
        System.out.println("\nУдаление пользователя 'elena_kozlova':");
        boolean deletedByUsername = userService.deleteUserByUsername("elena_kozlova");
        System.out.println(deletedByUsername ? "  ✓ Удалён успешно" : "  ✗ Не найден");

        // Удаление по ID
        System.out.println("\nУдаление пользователя 'alexey_smirnov' (по ID):");
        Optional<User> toDelete = userService.getUserByUsername("alexey_smirnov");
        if (toDelete.isPresent()) {
            boolean deletedById = userService.deleteUserById(toDelete.get().getId());
            System.out.println(deletedById ? "  ✓ Удалён успешно" : "  ✗ Не удалён");
        } else {
            System.out.println("  ✗ Пользователь не найден");
        }

        // Показать всех после удаления
        System.out.println("\nВсе пользователи после удалений:");
        userService.getAllUsers().forEach(user -> System.out.println("  • " + user));

        // STATISTICS
        System.out.println("\n" + "-".repeat(50));
        System.out.println("5. СТАТИСТИКА");
        System.out.println("-".repeat(50));

        long userCount = userService.getUserCount();
        System.out.println("  Общее количество пользователей: " + userCount);

        boolean exists = userService.userExists("maria_sidorova");
        System.out.println("  Существует пользователь 'maria_sidorova': " + exists);

        boolean notExists = userService.userExists(9999L);
        System.out.println("  Существует пользователь с ID=9999: " + notExists);

        System.out.println("\n" + "-".repeat(50));
        System.out.println("6. ДЕМО: массовое создание и удаление");
        System.out.println("-".repeat(50));

        System.out.println("\nСоздание 3 дополнительных пользователей:");
        for (int i = 1; i <= 3; i++) {
            User newUser = userService.createUser("temp_user_" + i);
            System.out.println("  ✓ Создан: " + newUser);
        }

        System.out.println("\nВсе пользователи перед массовым удалением:");
        userService.getAllUsers().forEach(user -> System.out.println("  • " + user));

        System.out.println("\nМассовое удаление всех пользователей...");
        long deletedCount = userService.deleteAllUsers();
        System.out.println("  ✓ Удалено пользователей: " + deletedCount);

        System.out.println("\nВсе пользователи после массового удаления:");
        List<User> remainingUsers = userService.getAllUsers();
        if (remainingUsers.isEmpty()) {
            System.out.println("  Таблица пуста");
        } else {
            remainingUsers.forEach(user -> System.out.println("  • " + user));
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("ВЫПОЛНЕНИЕ ЗАДАНИЯ 4 ЗАВЕРШЕНО");
        System.out.println("=".repeat(70));
    }
}