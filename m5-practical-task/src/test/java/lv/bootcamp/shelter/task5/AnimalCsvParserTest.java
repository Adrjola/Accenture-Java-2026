package lv.bootcamp.shelter.task5;

import lv.bootcamp.shelter.model.Animal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AnimalCsvParser")
class AnimalCsvParserTest {

    private AnimalCsvParser parser;

    @BeforeEach
    void setUp() {
        parser = new AnimalCsvParser();
    }

    @Nested
    @DisplayName("When parsing valid rows")
    class ValidRows {

        @Test
        @DisplayName("parses a complete row into an Animal")
        void shouldParseCompleteRow() {
            Optional<Animal> result = parser.parseRow("Buddy,Dog,3,true,2026-01-15");

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Buddy");
            assertThat(result.get().getSpecies()).isEqualTo("Dog");
            assertThat(result.get().getAge()).isEqualTo(3);
            assertThat(result.get().isVaccinated()).isTrue();
            assertThat(result.get().getIntakeDate()).isEqualTo(LocalDate.of(2026, 1, 15));
        }

        @Test
        @DisplayName("trims whitespace from fields")
        void shouldTrimWhitespace() {
            Optional<Animal> result = parser.parseRow("  Buddy , Dog , 3 , true , 2026-01-15 ");

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Buddy");
            assertThat(result.get().getSpecies()).isEqualTo("Dog");
        }

        @Test
        @DisplayName("parses vaccinated=false correctly")
        void shouldParseFalseVaccination() {
            Optional<Animal> result = parser.parseRow("Max,Dog,5,false,2026-01-18");

            assertThat(result).isPresent();
            assertThat(result.get().isVaccinated()).isFalse();
        }
    }

    @Nested
    @DisplayName("When parsing malformed rows")
    class MalformedRows {

        @Test
        @DisplayName("returns empty for null input")
        void shouldReturnEmptyForNull() {
            assertThat(parser.parseRow(null)).isEmpty();
        }

        @Test
        @DisplayName("returns empty for blank input")
        void shouldReturnEmptyForBlank() {
            assertThat(parser.parseRow("   ")).isEmpty();
        }

        @Test
        @DisplayName("returns empty when row has fewer than 5 fields")
        void shouldReturnEmptyForTooFewFields() {
            assertThat(parser.parseRow("Buddy,Dog,3")).isEmpty();
        }

        @Test
        @DisplayName("returns empty when name is missing")
        void shouldReturnEmptyForMissingName() {
            assertThat(parser.parseRow(",Dog,3,true,2026-01-15")).isEmpty();
        }

        @Test
        @DisplayName("returns empty when age is not a number")
        void shouldReturnEmptyForBadAge() {
            assertThat(parser.parseRow("Buddy,Dog,old,true,2026-01-15")).isEmpty();
        }

        @Test
        @DisplayName("returns empty when age is negative")
        void shouldReturnEmptyForNegativeAge() {
            assertThat(parser.parseRow("Buddy,Dog,-1,true,2026-01-15")).isEmpty();
        }

        @Test
        @DisplayName("returns empty when date is invalid")
        void shouldReturnEmptyForBadDate() {
            assertThat(parser.parseRow("Buddy,Dog,3,true,not-a-date")).isEmpty();
        }
    }

    @Nested
    @DisplayName("When handling edge cases")
    class EdgeCases {

        @Test
        @DisplayName("handles vaccinated field as any non-true string -> false")
        void shouldTreatNonTrueAsFalse() {
            Optional<Animal> result = parser.parseRow("Buddy,Dog,3,maybe,2026-01-15");

            assertThat(result).isPresent();
            assertThat(result.get().isVaccinated()).isFalse();
        }

        @Test
        @DisplayName("handles age 0 as valid")
        void shouldAcceptAgeZero() {
            Optional<Animal> result = parser.parseRow("Baby,Dog,0,true,2026-01-15");

            assertThat(result).isPresent();
            assertThat(result.get().getAge()).isZero();
        }
    }

    @Nested
    @DisplayName("When parsing a CSV file")
    class ParseFile {

        @Test
        @DisplayName("parses valid rows and counts skipped rows")
        void shouldParseFileAndCountSkipped() throws IOException {
            Path tempFile = Files.createTempFile("test-intake", ".csv");
            String content = """
                    name,species,age,vaccinated,intakeDate
                    Buddy,Dog,3,true,2026-01-15
                    Luna,Cat,2,true,2026-01-16
                    Max,Dog,5,false,2026-01-18
                    Bad,Dog,old,true,2026-01-19
                    """;

            try {
                Files.writeString(tempFile, content, StandardCharsets.UTF_8);

                AnimalCsvParser.ParseResult result = parser.parseFile(tempFile);

                assertThat(result.animals()).hasSize(3);
                assertThat(result.skippedRows()).isEqualTo(1);
            } finally {
                Files.deleteIfExists(tempFile);
            }
        }

        @Test
        @DisplayName("returns empty result for file with only a header")
        void shouldReturnEmptyForHeaderOnly() throws IOException {
            Path tempFile = Files.createTempFile("test-intake-header", ".csv");

            try {
                Files.writeString(tempFile, "name,species,age,vaccinated,intakeDate", StandardCharsets.UTF_8);

                AnimalCsvParser.ParseResult result = parser.parseFile(tempFile);

                assertThat(result.animals()).isEmpty();
                assertThat(result.skippedRows()).isZero();
            } finally {
                Files.deleteIfExists(tempFile);
            }
        }

        @Test
        @DisplayName("throws IOException for non-existent file")
        void shouldThrowForMissingFile() {
            Path missingFile = Path.of("does-not-exist.csv");

            assertThatThrownBy(() -> parser.parseFile(missingFile))
                    .isInstanceOf(IOException.class);
        }
    }
}
