package org.example.menu;

import org.example.model.*;
import org.example.shelter.Shelter;

import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {
    private final Shelter<Animal> shelter;
    private final Scanner scanner =  new Scanner(System.in);
    public ConsoleMenu(Shelter<Animal> shelter) {
        this.shelter = shelter;
    }

    public void start(){
        boolean running = true;

        while (running) {
            printMenu();
            int option = Integer.parseInt(scanner.nextLine());

            switch (option) {
                case 1 -> addAnimal();
                case 2 -> printAnimals(shelter.getAllAnimals());
                case 3 -> findAnimalsBySpecies();
                case 4 -> printAnimals(shelter.findAvailableAnimals());
                case 5 -> markAnimalAsAdopted();
                case 0 -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    private void addAnimal() {
        System.out.println("Species (Dog/Cat/Bird):");
        String species = scanner.nextLine();

        System.out.println("Name:");
        String name = scanner.nextLine();

        System.out.println("Age:");
        int age = Integer.parseInt(scanner.nextLine());

        Animal animal = switch (species.toLowerCase()) {
            case "dog" -> new Dog(new AnimalId(), name, age);
            case "cat" -> new Cat(new AnimalId(), name, age);
            case "bird" -> new Bird(new AnimalId(), name, age);
            default -> null;
        };

        if (animal == null) {
            System.out.println("Unknown species");
            return;
        }

        shelter.addAnimal(animal);
        System.out.println("Animal added");
    }

    private void findAnimalsBySpecies() {
        System.out.println("Species:");
        String species = scanner.nextLine();

        printAnimals(shelter.findBySpecies(species));
    }

    private void markAnimalAsAdopted() {
        System.out.println("Animal id:");
        String id = scanner.nextLine();

        shelter.markAsAdopted(id);
        System.out.println("Animal marked as adopted");
    }

    private void printAnimals(List<Animal> animals) {
        if (animals.isEmpty()) {
            System.out.println("No animals found");
            return;
        }

        for (Animal animal : animals) {
            System.out.println(animal);
        }
    }

    private void printMenu(){
        System.out.println("""
                1. Add animal
                2. List all animals
                3. Find animals by species
                4. List available animals
                5. Mark animal as adopted
                0. Exit
                """);
    }
}
