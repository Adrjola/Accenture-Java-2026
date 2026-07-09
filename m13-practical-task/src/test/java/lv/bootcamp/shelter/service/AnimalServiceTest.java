package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.client.NotificationClient;
import lv.bootcamp.shelter.dto.AdoptionRequest;
import lv.bootcamp.shelter.dto.AnimalCreateRequest;
import lv.bootcamp.shelter.dto.AnimalResponse;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.model.AnimalStatus;
import lv.bootcamp.shelter.model.AnimalType;
import lv.bootcamp.shelter.repository.AnimalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private NotificationClient notificationClient;

    @Captor
    private ArgumentCaptor<Animal> animalCaptor;

    @Captor
    private ArgumentCaptor<List<Long>> animalIdsCaptor;

    @InjectMocks
    private AnimalService animalService;

    @Test
    void create_shouldSaveAnimalWithAvailableStatus() {
        AnimalCreateRequest request = new AnimalCreateRequest(
                "Rex", AnimalType.DOG, "Labrador", 4, "Friendly");

        when(animalRepository.save(any(Animal.class))).thenAnswer(invocation -> {
            Animal animal = invocation.getArgument(0);
            animal.setId(1L);
            return animal;
        });

        AnimalResponse response = animalService.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Rex");
        assertThat(response.status()).isEqualTo(AnimalStatus.AVAILABLE);

        verify(animalRepository).save(animalCaptor.capture());
        Animal savedAnimal = animalCaptor.getValue();
        assertThat(savedAnimal.getName()).isEqualTo("Rex");
        assertThat(savedAnimal.getStatus()).isEqualTo(AnimalStatus.AVAILABLE);
    }

    @Test
    void findById_shouldThrowWhenAnimalNotFound() {
        when(animalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> animalService.findById(99L))
                .isInstanceOf(AnimalNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void adopt_shouldChangeStatusAndSendNotification() {
        Animal animal = animal(1L, "Rex", AnimalStatus.AVAILABLE);
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(animalRepository.save(any(Animal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnimalResponse response = animalService.adopt(
                new AdoptionRequest(1L, "John", "john@example.com"));

        assertThat(response.status()).isEqualTo(AnimalStatus.ADOPTED);
        verify(notificationClient).sendAdoptionNotification(1L, "Rex", "john@example.com");
    }

    @Test
    void adopt_shouldThrowWhenAnimalAlreadyAdopted() {
        Animal animal = animal(1L, "Rex", AnimalStatus.ADOPTED);
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        assertThatThrownBy(() -> animalService.adopt(
                new AdoptionRequest(1L, "John", "john@example.com")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not available");

        verify(animalRepository, never()).save(any(Animal.class));
        verifyNoInteractions(notificationClient);
    }

    @Test
    void reserveMultiple_shouldNotifyWithReservedIds() {
        Animal rex = animal(1L, "Rex", AnimalStatus.AVAILABLE);
        Animal mia = animal(2L, "Mia", AnimalStatus.AVAILABLE);
        when(animalRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(rex, mia));
        when(animalRepository.save(any(Animal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<AnimalResponse> responses = animalService.reserveMultiple(List.of(1L, 2L));

        assertThat(responses)
                .extracting(AnimalResponse::status)
                .containsExactly(AnimalStatus.RESERVED, AnimalStatus.RESERVED);
        verify(notificationClient).sendBulkStatusNotification(animalIdsCaptor.capture(), org.mockito.ArgumentMatchers.eq("RESERVED"));
        assertThat(animalIdsCaptor.getValue()).containsExactly(1L, 2L);
    }

    private Animal animal(Long id, String name, AnimalStatus status) {
        return new Animal(id, name, AnimalType.DOG, "Mixed", 3, "Friendly", status);
    }
}
