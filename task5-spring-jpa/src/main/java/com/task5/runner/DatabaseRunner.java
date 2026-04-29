package com.task5.runner;

import com.task5.entity.User;
import com.task5.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseRunner implements CommandLineRunner {

    private final UserService userService;

    @Autowired
    public DatabaseRunner(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=".repeat(70));
        System.out.println("ЗАДАНИЕ 5: SPRING DATA JPA + FLYWAY МИГРАЦИИ");
        System.out.println("=".repeat(70));

        System.out.println("\n>>> 1. ВСЕ ПОЛЬЗОВАТЕЛИ (из миграции):");
        userService.getAllUsers().forEach(user -> System.out.println("   • " + user));

        System.out.println("\n   Статистика:");
        System.out.println("   - Всего пользователей: " + userService.countTotalUsers());
        System.out.println("   - Активных: " + userService.countActiveUsers());

        System.out.println("\n>>> 2. СОЗДАНИЕ НОВЫХ ПОЛЬЗОВАТЕЛЕЙ:");

        User newUser1 = userService.createUser("new.user1", "new.user1@example.com", "Новый", "Пользователь1", 25);
        System.out.println("   ✓ Создан: " + newUser1);

        User newUser2 = userService.createUser("new.user2", "new.user2@example.com", "Новый", "Пользователь2", 30);
        System.out.println("   ✓ Создан: " + newUser2);

        User newUser3 = userService.createUser("new.user3");
        System.out.println("   ✓ Создан (только username): " + newUser3);

        System.out.println("\n>>> 3. ПОИСК ПОЛЬЗОВАТЕЛЕЙ:");

        System.out.println("\n   Поиск по ID=1:");
        userService.getUserById(1L).ifPresentOrElse(
                user -> System.out.println("   ✓ Найден: " + user),
                () -> System.out.println("   ✗ Не найден")
        );

        System.out.println("\n   Поиск по username='maria.sidorova':");
        userService.getUserByUsername("maria.sidorova").ifPresentOrElse(
                user -> System.out.println("   ✓ Найден: " + user),
                () -> System.out.println("   ✗ Не найден")
        );

        System.out.println("\n   Поиск по email='anna.morozova@example.com':");
        userService.getUserByEmail("anna.morozova@example.com").ifPresentOrElse(
                user -> System.out.println("   ✓ Найден: " + user),
                () -> System.out.println("   ✗ Не найден")
        );

        System.out.println("\n>>> 4. ФИЛЬТРАЦИЯ ПОЛЬЗОВАТЕЛЕЙ:");

        System.out.println("\n   Пользователи старше 30 лет:");
        userService.getUsersOlderThan(30).forEach(user -> System.out.println("   • " + user.getUsername() + " (" + user.getAge() + " лет)"));

        System.out.println("\n   Пользователи в возрасте от 25 до 30 лет:");
        userService.getUsersByAgeRange(25, 30).forEach(user -> System.out.println("   • " + user.getUsername() + " (" + user.getAge() + " лет)"));

        System.out.println("\n   Поиск по ключевому слову 'ivan':");
        userService.searchUsers("ivan").forEach(user -> System.out.println("   • " + user.getUsername()));

        System.out.println("\n>>> 5. ОБНОВЛЕНИЕ ПОЛЬЗОВАТЕЛЕЙ:");

        User updated = userService.updateUser(newUser3.getId(), "updated.user3");
        System.out.println("   ✓ Изменён username: " + updated);

        User updatedEmail = userService.updateUserEmail(newUser2.getId(), "new.email@example.com");
        System.out.println("   ✓ Изменён email: " + updatedEmail);

        User updatedInfo = userService.updateUserInfo(1L, "Иван", "Петрович", 29);
        System.out.println("   ✓ Обновлена информация: " + updatedInfo);

        System.out.println("\n>>> 6. ДЕАКТИВАЦИЯ ПОЛЬЗОВАТЕЛЕЙ:");

        userService.deactivateUser(2L);
        System.out.println("   ✓ Пользователь с ID=2 деактивирован");

        userService.deactivateUser(3L);
        System.out.println("   ✓ Пользователь с ID=3 деактивирован");

        System.out.println("\n   Активные пользователи после деактивации:");
        userService.getActiveUsers().forEach(user -> System.out.println("   • " + user.getUsername()));

        System.out.println("\n>>> 7. СТАТИСТИКА:");

        System.out.println("\n   - Всего пользователей: " + userService.countTotalUsers());
        System.out.println("   - Активных: " + userService.countActiveUsers());
        System.out.println("   - Неактивных: " + (userService.countTotalUsers() - userService.countActiveUsers()));

        Double avgAge = userService.getAverageAgeOfActiveUsers();
        if (avgAge != null) {
            System.out.printf("   - Средний возраст активных: %.2f%n", avgAge);
        }

        long ageRangeCount = userService.countUsersInAgeRange(25, 35);
        System.out.println("   - Пользователей в возрасте 25-35 лет: " + ageRangeCount);

        System.out.println("\n>>> 8. CUSTOM QUERIES:");

        System.out.println("\n   Список всех активных username:");
        userService.getAllActiveUsernames().forEach(username -> System.out.println("   • " + username));

        System.out.println("\n   Топ пользователей старше 25 лет (через native query):");
        userService.getTopUsersByAge(25, 5).forEach(user -> System.out.println("   • " + user.getUsername() + " (" + user.getAge() + " лет)"));

        System.out.println("\n>>> 9. ПАГИНАЦИЯ:");

        System.out.println("\n   Активные пользователи (страница 0, размер 5):");
        Page<User> page1 = userService.getActiveUsersPaginated(0, 5);
        page1.getContent().forEach(user -> System.out.println("   • " + user.getUsername()));
        System.out.println("   Всего страниц: " + page1.getTotalPages() + ", всего элементов: " + page1.getTotalElements());

        System.out.println("\n   Активные пользователи (страница 1, размер 5):");
        Page<User> page2 = userService.getActiveUsersPaginated(1, 5);
        page2.getContent().forEach(user -> System.out.println("   • " + user.getUsername()));

        System.out.println("\n>>> 10. МАССОВОЕ СОЗДАНИЕ:");

        List<User> batchUsers = Arrays.asList(
                new User("batch.user1", "batch1@test.com", "Batch", "User1", 22),
                new User("batch.user2", "batch2@test.com", "Batch", "User2", 24),
                new User("batch.user3", "batch3@test.com", "Batch", "User3", 26)
        );
        List<User> created = userService.createUsers(batchUsers);
        System.out.println("   ✓ Создано пользователей: " + created.size());
        created.forEach(user -> System.out.println("     - " + user));

        System.out.println("\n>>> 11. УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЕЙ:");

        System.out.println("\n   Удаление по username 'new.user3':");
        boolean deleted = userService.deleteUserByUsername("new.user3");
        System.out.println("   " + (deleted ? "✓ Удалён" : "✗ Не найден"));

        System.out.println("\n   Удаление неактивных пользователей:");
        int deletedInactive = userService.deleteInactiveUsers();
        System.out.println("   ✓ Удалено неактивных: " + deletedInactive);

        System.out.println("\n>>> 12. ФИНАЛЬНАЯ СТАТИСТИКА:");
        System.out.println("\n   - Всего пользователей в БД: " + userService.countTotalUsers());
        System.out.println("   - Активных: " + userService.countActiveUsers());

        System.out.println("\n   Все оставшиеся пользователи:");
        userService.getAllUsers().forEach(user -> System.out.println("   • " + user));

        System.out.println("\n" + "=".repeat(70));
        System.out.println("ВЫПОЛНЕНИЕ ЗАДАНИЯ 5 ЗАВЕРШЕНО");
        System.out.println("=".repeat(70));
    }
}