package lv.bootcamp.shelter.task6;

import lv.bootcamp.shelter.model.Animal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("IntakeService")
class IntakeServiceTest {

    @Mock
    private AnimalRepository repository;

    @InjectMocks
    private IntakeService service;

    private final Animal buddy = new Animal("Buddy", "Dog", 3, true, LocalDate.of(2026, 1, 15));

    @Nested
    @DisplayName("intake")
    class Intake {

        @Test
        @DisplayName("saves valid animal and returns it")
        void shouldSaveValidAnimal() {
            when(repository.save(buddy)).thenReturn(buddy);

            Animal result = service.intake(buddy);

            assertThat(result.getName()).isEqualTo("Buddy");
            verify(repository).save(buddy);
        }

        @Test
        @DisplayName("throws for null animal without calling repository")
        void shouldThrowForNullAnimal() {
            assertThatThrownBy(() -> service.intake(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("must not be null");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("throws for invalid animal without calling repository")
        void shouldThrowForInvalidAnimal() {
            Animal invalid = new Animal("", "Dog", 3, true, LocalDate.now());

            assertThatThrownBy(() -> service.intake(invalid))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Name");

            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("findByName")
    class FindByName {

        @Test
        @DisplayName("returns animal when repository finds it")
        void shouldReturnAnimalWhenFound() {
            when(repository.findByName("Buddy")).thenReturn(Optional.of(buddy));

            Animal result = service.findByName("Buddy");

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Buddy");
            verify(repository).findByName("Buddy");
        }

        @Test
        @DisplayName("returns null when repository does not find it")
        void shouldReturnNullWhenNotFound() {
            when(repository.findByName("Ghost")).thenReturn(Optional.empty());

            Animal result = service.findByName("Ghost");

            assertThat(result).isNull();
            verify(repository).findByName("Ghost");
        }

        @Test
        @DisplayName("throws for blank name without calling repository")
        void shouldThrowForBlankName() {
            assertThatThrownBy(() -> service.findByName(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Name");

            verify(repository, never()).findByName(any());
        }
    }

    @Nested
    @DisplayName("findBySpecies")
    class FindBySpecies {

        @Test
        @DisplayName("returns list from repository for valid species")
        void shouldReturnAnimalsForValidSpecies() {
            when(repository.findBySpecies("Dog")).thenReturn(List.of(buddy));

            List<Animal> result = service.findBySpecies("Dog");

            assertThat(result).containsExactly(buddy);
            verify(repository).findBySpecies("Dog");
        }

        @Test
        @DisplayName("returns empty list for blank species without calling repository")
        void shouldReturnEmptyForBlankSpecies() {
            List<Animal> result = service.findBySpecies("");

            assertThat(result).isEmpty();
            verify(repository, never()).findBySpecies(any());
        }

        @Test
        @DisplayName("returns empty list for null species without calling repository")
        void shouldReturnEmptyForNullSpecies() {
            List<Animal> result = service.findBySpecies(null);

            assertThat(result).isEmpty();
            verify(repository, never()).findBySpecies(any());
        }
    }

    @Nested
    @DisplayName("count")
    class Count {

        @Test
        @DisplayName("returns the size of all animals from repository")
        void shouldReturnCountFromRepository() {
            Animal luna = new Animal("Luna", "Cat", 2, true, LocalDate.of(2026, 1, 16));
            Animal max = new Animal("Max", "Dog", 5, false, LocalDate.of(2026, 1, 18));
            when(repository.findAll()).thenReturn(List.of(buddy, luna, max));

            int result = service.count();

            assertThat(result).isEqualTo(3);
            verify(repository).findAll();
        }

        @Test
        @DisplayName("returns 0 when repository is empty")
        void shouldReturnZeroWhenEmpty() {
            when(repository.findAll()).thenReturn(List.of());

            int result = service.count();

            assertThat(result).isZero();
            verify(repository).findAll();
        }
    }
}
