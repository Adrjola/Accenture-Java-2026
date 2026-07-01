package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.util.*;
import java.util.stream.Collectors;

public class ShelterAnalyticsService {

    public ShelterReportData buildReportData(ImportResult importResult) {
        List<Animal> allAnimals = importResult.allAnimals();

        Set<String> uniqueSpecies = new TreeSet<>();
        Map<String, List<Animal>> animalsBySpecies = new HashMap<>();
        List<String> animalsNeedingVetInput = new ArrayList<>();

        uniqueSpecies = allAnimals.stream()
                .map(Animal::getSpecies)
                .collect(Collectors.toCollection(TreeSet::new));

        animalsBySpecies = allAnimals.stream()
                .collect(Collectors.groupingBy(
                        Animal::getSpecies,
                        TreeMap::new,
                        Collectors.toList()
                ));

        animalsNeedingVetInput = allAnimals.stream()
                .filter(animal -> !animal.isVaccinated())
                .map(animal -> animal.getName() + "(" + animal.getSpecies() + ")")
                .toList();

        Map<String, Long> vaccinatedCountsBySpecies = allAnimals.stream()
                .filter(Animal::isVaccinated)
                .collect(Collectors.groupingBy(
                        Animal::getSpecies,
                        TreeMap::new,
                        Collectors.counting()
                ));

        Map<String, Long> unvaccinatedCountsBySpecies = allAnimals.stream()
                .filter(animal -> !animal.isVaccinated())
                .collect(Collectors.groupingBy(
                        Animal::getSpecies,
                        TreeMap::new,
                        Collectors.counting()
                ));

        Map<String, Animal> oldestAnimalsBySpecies = allAnimals.stream()
                .filter(animal -> animal.getAge() != null)
                .collect(Collectors.groupingBy(
                        Animal::getSpecies,
                        TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(Animal::getAge)),
                                Optional::get
                        )
                ));

        return new ShelterReportData(
                importResult,
                uniqueSpecies,
                animalsBySpecies,
                vaccinatedCountsBySpecies,
                unvaccinatedCountsBySpecies,
                oldestAnimalsBySpecies,
                animalsNeedingVetInput
        );
    }
}
