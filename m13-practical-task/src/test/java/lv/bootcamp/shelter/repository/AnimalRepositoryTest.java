package lv.bootcamp.shelter.repository;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.model.AnimalStatus;
import lv.bootcamp.shelter.model.AnimalType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Task: Repository tests with @DataJpaTest.
 *
 * Use entityManager.persist() + entityManager.flush() to set up test data.
 * Each test rolls back automatically — no cleanup needed.
 */
@DataJpaTest
class AnimalRepositoryTest {

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void save_shouldPersistAnimalAndGenerateId() {
        Animal animal = animal("Rex", AnimalType.DOG, AnimalStatus.AVAILABLE);

        Animal saved = animalRepository.save(animal);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Rex");
    }

    @Test
    void findByStatus_shouldReturnOnlyMatchingAnimals() {
        entityManager.persist(animal("Rex", AnimalType.DOG, AnimalStatus.AVAILABLE));
        entityManager.persist(animal("Mia", AnimalType.CAT, AnimalStatus.AVAILABLE));
        entityManager.persist(animal("Bella", AnimalType.CAT, AnimalStatus.ADOPTED));
        entityManager.flush();

        List<Animal> animals = animalRepository.findByStatus(AnimalStatus.AVAILABLE);

        assertThat(animals)
                .extracting(Animal::getName)
                .containsExactlyInAnyOrder("Rex", "Mia");
    }

    @Test
    void findByType_shouldReturnAnimalsOfGivenType() {
        entityManager.persist(animal("Rex", AnimalType.DOG, AnimalStatus.AVAILABLE));
        entityManager.persist(animal("Mia", AnimalType.CAT, AnimalStatus.AVAILABLE));
        entityManager.flush();

        List<Animal> animals = animalRepository.findByType(AnimalType.DOG);

        assertThat(animals)
                .extracting(Animal::getName)
                .containsExactly("Rex");
    }

    @Test
    void findByNameContainingIgnoreCase_shouldMatchPartialName() {
        entityManager.persist(animal("Rex", AnimalType.DOG, AnimalStatus.AVAILABLE));
        entityManager.persist(animal("Rexy Jr", AnimalType.DOG, AnimalStatus.AVAILABLE));
        entityManager.persist(animal("Mia", AnimalType.CAT, AnimalStatus.AVAILABLE));
        entityManager.flush();

        List<Animal> animals = animalRepository.findByNameContainingIgnoreCase("rex");

        assertThat(animals)
                .extracting(Animal::getName)
                .containsExactlyInAnyOrder("Rex", "Rexy Jr");
    }

    private Animal animal(String name, AnimalType type, AnimalStatus status) {
        return new Animal(null, name, type, "Mixed", 3, "Friendly", status);
    }
}
