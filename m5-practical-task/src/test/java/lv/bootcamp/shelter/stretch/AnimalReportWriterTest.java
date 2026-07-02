package lv.bootcamp.shelter.stretch;

import lv.bootcamp.shelter.model.Animal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AnimalReportWriter (stretch)")
class AnimalReportWriterTest {

    private final AnimalReportWriter writer = new AnimalReportWriter();

    @Test
    @DisplayName("writes report file that contains total count")
    void shouldWriteTotalCount() throws IOException {
        Path output = Files.createTempFile("report-test", ".txt");

        try {
            writer.writeReport(sampleAnimals(), output);

            String content = Files.readString(output, StandardCharsets.UTF_8);

            assertThat(content).contains("Total animals: 3");
        } finally {
            Files.deleteIfExists(output);
        }
    }

    @Test
    @DisplayName("writes per-species breakdown in alphabetical order")
    void shouldWriteSpeciesBreakdown() throws IOException {
        Path output = Files.createTempFile("report-test", ".txt");

        try {
            writer.writeReport(sampleAnimals(), output);

            String content = Files.readString(output, StandardCharsets.UTF_8);

            assertThat(content.indexOf("Cat:")).isLessThan(content.indexOf("Dog:"));
            assertThat(content).contains("Cat: 1 total, 1 vaccinated");
            assertThat(content).contains("Dog: 2 total, 1 vaccinated");
        } finally {
            Files.deleteIfExists(output);
        }
    }

    @Test
    @DisplayName("writes oldest animal per species")
    void shouldWriteOldestPerSpecies() throws IOException {
        Path output = Files.createTempFile("report-test", ".txt");

        try {
            writer.writeReport(sampleAnimals(), output);

            String content = Files.readString(output, StandardCharsets.UTF_8);

            assertThat(content).contains("Dog: Max (age 5)");
        } finally {
            Files.deleteIfExists(output);
        }
    }

    private List<Animal> sampleAnimals() {
        return List.of(
                new Animal("Buddy", "Dog", 3, true, LocalDate.of(2026, 1, 15)),
                new Animal("Max", "Dog", 5, false, LocalDate.of(2026, 1, 18)),
                new Animal("Luna", "Cat", 2, true, LocalDate.of(2026, 1, 16))
        );
    }
}
