package com.task2;

import java.util.*;
import java.util.stream.Collectors;

public class TaskSolver {

    // 1. Найти 3-е наибольшее число
    public static Integer findThirdLargest(List<Integer> numbers) {
        if (numbers == null || numbers.size() < 3) {
            return null;
        }
        return numbers.stream()
                .sorted(Comparator.reverseOrder())
                .skip(2)
                .findFirst()
                .orElse(null);
    }

    // 2. Найти 3-е наибольшее уникальное число
    public static Integer findThirdLargestUnique(List<Integer> numbers) {
        if (numbers == null || numbers.size() < 3) {
            return null;
        }
        return numbers.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .skip(2)
                .findFirst()
                .orElse(null);
    }

    // 3. Получить имена 3 самых старших инженеров (в порядке убывания возраста)
    public static List<String> getTop3OldestEngineers(List<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            return Collections.emptyList();
        }
        return employees.stream()
                .filter(e -> "Инженер".equals(e.getPosition()))
                .sorted((e1, e2) -> Integer.compare(e2.getAge(), e1.getAge()))
                .limit(3)
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    // 4. Посчитать средний возраст инженеров
    public static double getAverageAgeOfEngineers(List<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            return 0.0;
        }
        return employees.stream()
                .filter(e -> "Инженер".equals(e.getPosition()))
                .mapToInt(Employee::getAge)
                .average()
                .orElse(0.0);
    }

    // 5. Найти самое длинное слово в списке
    public static String findLongestWord(List<String> words) {
        if (words == null || words.isEmpty()) {
            return null;
        }
        return words.stream()
                .max(Comparator.comparingInt(String::length))
                .orElse(null);
    }

    // 6. Подсчитать частоту слов в строке
    public static Map<String, Long> countWordFrequency(String sentence) {
        if (sentence == null || sentence.trim().isEmpty()) {
            return Collections.emptyMap();
        }
        return Arrays.stream(sentence.split("\\s+"))
                .collect(Collectors.groupingBy(
                        word -> word,
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }

    // 7. Отсортировать строки по длине, при равенстве - по алфавиту
    public static List<String> sortByLengthThenAlphabetically(List<String> words) {
        if (words == null || words.isEmpty()) {
            return Collections.emptyList();
        }
        return words.stream()
                .sorted(Comparator
                        .comparingInt(String::length)
                        .thenComparing(Comparator.naturalOrder()))
                .collect(Collectors.toList());
    }

    // 8. Найти самое длинное слово среди всех слов в массиве строк
    public static String findLongestWordInArray(String[] stringArray) {
        if (stringArray == null || stringArray.length == 0) {
            return null;
        }
        return Arrays.stream(stringArray)
                .flatMap(s -> Arrays.stream(s.split("\\s+")))
                .max(Comparator.comparingInt(String::length))
                .orElse(null);
    }
}