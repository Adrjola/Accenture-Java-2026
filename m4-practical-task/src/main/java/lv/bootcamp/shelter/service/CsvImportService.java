package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvImportService {

    private static final int EXPECTED_COLUMNS = 5;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("dd.MM.uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    public ImportResult importAnimals(Path inputPath) {
        log.info("Starting import from {}", inputPath);

        List<Animal> allAnimals = new ArrayList<>();
        int skippedRows = 0;

        try {
            List<String> lines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);

            for (int i = 1; i < lines.size(); i++) {
                int rowNumber = i + 1;
                String[] columns = lines.get(i).split(",", -1);

                if (columns.length != EXPECTED_COLUMNS) {
                    log.warn("Skipping row {}: expected {} columns, got {}", rowNumber, EXPECTED_COLUMNS, columns.length);
                    skippedRows++;
                    continue;
                }

                Animal animal = parseAnimal(columns, rowNumber);

                if (animal == null) {
                    skippedRows++;
                    continue;
                }

                allAnimals.add(animal);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read input file: " + inputPath, exception);
        }

        return new ImportResult(allAnimals, skippedRows);
    }

    private Animal parseAnimal(String[] columns, int rowNumber) {
        String name = columns[0].trim();
        String species = columns[1].trim();
        String ageText = columns[2].trim();
        String vaccinatedText = columns[3].trim();
        String intakeDateText = columns[4].trim();

        if (name.isBlank() || species.isBlank() || vaccinatedText.isBlank() || intakeDateText.isBlank()) {
            log.warn("Skipping row {}: required field is missing", rowNumber);
            return null;
        }

        Integer age = parseAge(ageText, rowNumber);
        if (!ageText.isBlank() && age == null) {
            return null;
        }

        Boolean vaccinated = parseVaccinated(vaccinatedText, rowNumber);
        if (vaccinated == null) {
            return null;
        }

        LocalDate intakeDate = parseDate(intakeDateText, rowNumber);
        if (intakeDate == null) {
            return null;
        }

        return new Animal(name, species, age, vaccinated, intakeDate);
    }

    private Integer parseAge(String ageText, int rowNumber) {
        if (ageText.isBlank()) {
            return null;
        }

        try {
            int age = Integer.parseInt(ageText);

            if (age < 0) {
                log.warn("Skipping row {}: age cannot be negative", rowNumber);
                return null;
            }

            return age;
        } catch (NumberFormatException exception) {
            log.warn("Skipping row {}: age is not numeric", rowNumber);
            return null;
        }
    }

    private Boolean parseVaccinated(String vaccinatedText, int rowNumber) {
        if (vaccinatedText.equalsIgnoreCase("true")) {
            return true;
        }

        if (vaccinatedText.equalsIgnoreCase("false")) {
            return false;
        }

        log.warn("Skipping row {}: vaccinated must be true or false", rowNumber);
        return null;
    }

    private LocalDate parseDate(String intakeDateText, int rowNumber) {
        try {
            return LocalDate.parse(intakeDateText, DATE_FORMATTER);
        } catch (DateTimeParseException exception) {
            log.warn("Skipping row {}: invalid intake date", rowNumber);
            return null;
        }
    }
}
