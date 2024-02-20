package by.itclass.utils;

import by.itclass.exceptions.CompetitionException;
import by.itclass.model.Animal;
import by.itclass.model.Cat;
import by.itclass.model.Dog;
import lombok.experimental.UtilityClass;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static by.itclass.constants.CompetitionConstants.*;

@UtilityClass
public class CompetitionUtils {
    private static final String PATH_TO_FILE = "src/by/itclass/resources/animals.txt";

    public static void parseFile(List<Cat> cats, List<Dog> dogs,
                                 Map<String, String> errors) {
        try (Scanner sc = new Scanner(new FileReader(PATH_TO_FILE))){
            while (sc.hasNextLine()) {
                fillingCollections(sc.nextLine(), cats, dogs, errors);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File hasn't been found by path " + PATH_TO_FILE);
            System.exit(1);
        }
    }

    private static void fillingCollections(String textLine, List<Cat> cats,
                                           List<Dog> dogs, Map<String, String> errors) {
        try {
            var animal = AnimalFactory.getInstance(textLine);
            if (animal instanceof Cat) {
                cats.add((Cat) animal);
            } else {
                dogs.add((Dog) animal);
            }
        } catch (CompetitionException e) {
            processException(errors, e);
        }
    }

    private static void processException(Map<String, String> errors, CompetitionException e) {
        var pattern = Pattern.compile(EMAIL_REGEX);
        var matcher = pattern.matcher(e.getErrorLine());
        if (matcher.find()) {
            errors.put(matcher.group(), String.format("Error in line \"%s\" - %s", e.getErrorLine(), e.getCause().getMessage()));
        }
    }

    public static <T> List<T> sortByBirthDate(List<T> animals) {
        return animals.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public static void printResults(List<Cat> cats, List<Dog> dogs, Map<String, String> errors) {
        System.out.println("Cats list size = " + cats.size());
        printCollection(cats);
        System.out.println("-------------------------------------");
        System.out.println("Dogs list size = " + dogs.size());
        printCollection(dogs);
        System.out.println("-------------------------------------");
        printMap(errors);
    }

    public static void printResults(List<Cat> youngCats, List<Dog> youngDogs, List<Cat> oldCats,
                                    List<Dog> oldDogs, Map<String, String> errors) {
        System.out.println("First day participants:");
        System.out.println("Cats list size = " + youngCats.size());
        printCollection(youngCats);
        System.out.println("Dogs list size = " + youngDogs.size());
        printCollection(youngDogs);
        System.out.println("-------------------------------------");
        System.out.println("Second day participants:");
        System.out.println("Cats list size = " + oldCats.size());
        printCollection(oldCats);
        System.out.println("Dogs list size = " + oldDogs.size());
        printCollection(oldDogs);
        printMap(errors);
    }

    private static <T> void printCollection(List<T> collection) {
        collection.forEach(System.out::println);
    }

    private static void printMap(Map<String, String> map) {
        if (!map.isEmpty()) {
            System.out.println("-------------------------------------");
            System.out.println("Errors quantity = " + map.size());
            map.forEach((key,value) -> System.out.println(key + "; " + value));
        }
    }

    public static <T extends Animal> List<T> filterAnimals(List<T> participants, boolean isYoung) {
        return participants.stream()
                .filter(isYoung ? YOUNG_PREDICATE : OLD_PREDICATE)
                .collect(Collectors.toList());
    }
}
