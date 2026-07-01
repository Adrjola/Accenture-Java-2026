package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class ReportExportService {

    public void writeReport(Path outputPath, ShelterReportData reportData) {
        try {
            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }

            try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
                writer.write("Shelter CSV Import Report");
                writer.newLine();
                writer.write("Generated: " + LocalDate.now());
                writer.newLine();
                writer.write("Total imported: " + reportData.importResult().allAnimals().size());
                writer.newLine();
                writer.write("Total skipped: " + reportData.importResult().skippedRows());
                writer.newLine();
                writer.newLine();

                writer.write("Unique species: " + String.join(", ", reportData.uniqueSpecies()));
                writer.newLine();
                writer.newLine();

                writer.write("Per-species totals:");
                writer.newLine();
                for (String species : reportData.uniqueSpecies()) {
                    int total = reportData.animalsBySpecies().getOrDefault(species, List.of()).size();
                    long vaccinated = reportData.vaccinatedCountsBySpecies().getOrDefault(species, 0L);
                    long unvaccinated = reportData.unvaccinatedCountsBySpecies().getOrDefault(species, 0L);

                    writer.write(species + ": total=" + total
                            + ", vaccinated=" + vaccinated
                            + ", unvaccinated=" + unvaccinated);
                    writer.newLine();
                }
                writer.newLine();

                writer.write("Oldest animal per species:");
                writer.newLine();
                for (String species : reportData.uniqueSpecies()) {
                    Animal oldestAnimal = reportData.oldestAnimalsBySpecies().get(species);

                    if (oldestAnimal == null) {
                        writer.write(species + ": unknown");
                    } else {
                        writer.write(species + ": " + oldestAnimal.getName() + " (" + oldestAnimal.getAge() + " years)");
                    }

                    writer.newLine();
                }
                writer.newLine();

                writer.write("needs-vet-input: ");
                writer.write(reportData.animalsNeedingVetInput().isEmpty()
                        ? "none"
                        : String.join(", ", reportData.animalsNeedingVetInput()));
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not write report: " + outputPath, exception);
        }
    }
}
