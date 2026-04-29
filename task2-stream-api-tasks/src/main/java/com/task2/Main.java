package com.task2;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("ЗАДАНИЕ 2: РАБОТА СО STREAM API");
        System.out.println("=".repeat(60));

        // ========== 1. Третье наибольшее число ==========
        System.out.println("\n1. Третье наибольшее число:");
        List<Integer> numbers = Arrays.asList(5, 2, 10, 9, 4, 3, 10, 1, 13);
        System.out.println("Исходный список: " + numbers);
        Integer thirdLargest = TaskSolver.findThirdLargest(numbers);
        System.out.println("3-е наибольшее число: " + thirdLargest);

        // ========== 2. Третье наибольшее уникальное число ==========
        System.out.println("\n2. Третье наибольшее УНИКАЛЬНОЕ число:");
        System.out.println("Исходный список: " + numbers);
        Integer thirdLargestUnique = TaskSolver.findThirdLargestUnique(numbers);
        System.out.println("3-е наибольшее уникальное число: " + thirdLargestUnique);

        // ========== 3. Три самых старших инженера ==========
        System.out.println("\n3. Три самых старших инженера:");
        List<Employee> employees = Arrays.asList(
                new Employee("Иван", 25, "Инженер"),
                new Employee("Петр", 45, "Инженер"),
                new Employee("Сидор", 35, "Инженер"),
                new Employee("Анна", 50, "Инженер"),
                new Employee("Мария", 28, "Менеджер"),
                new Employee("Ольга", 40, "Инженер"),
                new Employee("Дмитрий", 32, "Инженер")
        );
        employees.forEach(System.out::println);
        List<String> top3Engineers = TaskSolver.getTop3OldestEngineers(employees);
        System.out.println("Имена 3 самых старших инженеров: " + top3Engineers);

        // ========== 4. Средний возраст инженеров ==========
        System.out.println("\n4. Средний возраст инженеров:");
        double avgAge = TaskSolver.getAverageAgeOfEngineers(employees);
        System.out.printf("Средний возраст: %.2f%n", avgAge);

        // ========== 5. Самое длинное слово ==========
        System.out.println("\n5. Самое длинное слово в списке:");
        List<String> words = Arrays.asList("яблоко", "груша", "арбуз", "мандарин", "киви");
        System.out.println("Список слов: " + words);
        String longestWord = TaskSolver.findLongestWord(words);
        System.out.println("Самое длинное слово: " + longestWord);

        // ========== 6. Частота слов ==========
        System.out.println("\n6. Частота слов в строке:");
        String sentence = "мама мыла раму мама мыла пол папа мыл машину";
        System.out.println("Строка: " + sentence);
        Map<String, Long> frequencyMap = TaskSolver.countWordFrequency(sentence);
        frequencyMap.forEach((word, count) -> System.out.println("  '" + word + "' -> " + count + " раз(а)"));

        // ========== 7. Сортировка по длине и алфавиту ==========
        System.out.println("\n7. Сортировка слов по длине и алфавиту:");
        List<String> unsortedWords = Arrays.asList("собака", "кот", "слон", "бегемот", "кот", "аист");
        System.out.println("Исходный список: " + unsortedWords);
        List<String> sortedWords = TaskSolver.sortByLengthThenAlphabetically(unsortedWords);
        System.out.println("Отсортированный: " + sortedWords);

        // ========== 8. Самое длинное слово в массиве строк ==========
        System.out.println("\n8. Самое длинное слово среди всех слов в массиве строк:");
        String[] stringArray = {
                "яблоко груша слива",
                "арбуз дыня тыква",
                "мандарин апельсин банан"
        };
        System.out.println("Исходный массив строк:");
        for (String s : stringArray) {
            System.out.println("  " + s);
        }
        String longestFromArray = TaskSolver.findLongestWordInArray(stringArray);
        System.out.println("Самое длинное слово: " + longestFromArray);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("ВЫПОЛНЕНИЕ ЗАДАНИЯ 2 ЗАВЕРШЕНО");
        System.out.println("=".repeat(60));
    }
}