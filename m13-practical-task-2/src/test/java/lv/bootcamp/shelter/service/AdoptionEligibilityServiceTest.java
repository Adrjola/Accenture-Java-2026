package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.audit.AuditLogger;
import lv.bootcamp.shelter.audit.RejectionReason;
import lv.bootcamp.shelter.client.NotificationClient;
import lv.bootcamp.shelter.model.Adopter;
import lv.bootcamp.shelter.model.AdoptionResult;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.model.AnimalStatus;
import lv.bootcamp.shelter.model.AnimalType;
import lv.bootcamp.shelter.repository.AdopterRepository;
import lv.bootcamp.shelter.repository.AnimalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Write tests for AdoptionEligibilityService.
 * The class and mocks are set up — the rest is yours.
 */
@ExtendWith(MockitoExtension.class)
class AdoptionEligibilityServiceTest {

    @Mock
    private AdopterRepository adopterRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private AuditLogger auditLogger;

    @InjectMocks
    private AdoptionEligibilityService service;

    @Test
    void rejectsWhenAdopterIsNotFound() {
        when(adopterRepository.findById(1L)).thenReturn(Optional.empty());

        AdoptionResult result = service.evaluateAdoption(1L, 7L);

        assertRejected(result, RejectionReasons.ADOPTER_NOT_FOUND);
        verifyNoInteractions(animalRepository, auditLogger, notificationClient);
    }

    @Test
    void rejectsWhenAnimalIsNotFound() {
        when(adopterRepository.findById(1L)).thenReturn(Optional.of(adultAdopter()));
        when(animalRepository.findById(7L)).thenReturn(Optional.empty());

        AdoptionResult result = service.evaluateAdoption(1L, 7L);

        assertRejected(result, RejectionReasons.ANIMAL_NOT_FOUND);
        verifyNoInteractions(auditLogger, notificationClient);
    }

    @ParameterizedTest
    @EnumSource(value = AnimalStatus.class, names = {"RESERVED", "ADOPTED"})
    void rejectsWhenAnimalIsNotAvailable(AnimalStatus status) {
        when(adopterRepository.findById(1L)).thenReturn(Optional.of(adultAdopter()));
        when(animalRepository.findById(7L)).thenReturn(Optional.of(animal(status)));

        AdoptionResult result = service.evaluateAdoption(1L, 7L);

        assertRejected(result, RejectionReasons.ANIMAL_NOT_AVAILABLE);
        verifyNoInteractions(auditLogger, notificationClient);
    }

    @Test
    void rejectsUnderageAdopterAndLogsReason() {
        Adopter adopter = adultAdopter();
        adopter.setAge(17);
        when(adopterRepository.findById(1L)).thenReturn(Optional.of(adopter));
        when(animalRepository.findById(7L)).thenReturn(Optional.of(animal(AnimalStatus.AVAILABLE)));

        AdoptionResult result = service.evaluateAdoption(1L, 7L);

        assertRejected(result, RejectionReasons.UNDERAGE);
        verify(auditLogger).logRejection(1L, 7L, RejectionReason.UNDERAGE);
        verify(auditLogger, never()).logApproval(anyLong(), anyLong(), anyInt());
        verifyNoInteractions(notificationClient);
    }

    @Test
    void rejectsRegularPropertyAdopterAtThreePets() {
        Adopter adopter = adultAdopter();
        adopter.setCurrentPetCount(3);
        when(adopterRepository.findById(1L)).thenReturn(Optional.of(adopter));
        when(animalRepository.findById(7L)).thenReturn(Optional.of(animal(AnimalStatus.AVAILABLE)));

        AdoptionResult result = service.evaluateAdoption(1L, 7L);

        assertRejected(result, RejectionReasons.PET_LIMIT_REACHED);
        verify(auditLogger).logRejection(1L, 7L, RejectionReason.PET_LIMIT_REACHED);
        verifyNoInteractions(notificationClient);
    }

    @Test
    void allowsLargePropertyAdopterWithFourPets() {
        Adopter adopter = adultAdopter();
        adopter.setLargeProperty(true);
        adopter.setCurrentPetCount(4);
        when(adopterRepository.findById(1L)).thenReturn(Optional.of(adopter));
        when(animalRepository.findById(7L)).thenReturn(Optional.of(animal(AnimalStatus.AVAILABLE)));

        AdoptionResult result = service.evaluateAdoption(1L, 7L);

        assertThat(result.approved()).isTrue();
        assertThat(result.reason()).isEqualTo("Approved");
        verify(auditLogger).logApproval(eq(1L), eq(7L), anyInt());
        verify(notificationClient).sendApprovalNotification("adopter@example.com", "Bella");
    }

    @Test
    void rejectsLargePropertyAdopterAtFivePets() {
        Adopter adopter = adultAdopter();
        adopter.setLargeProperty(true);
        adopter.setCurrentPetCount(5);
        when(adopterRepository.findById(1L)).thenReturn(Optional.of(adopter));
        when(animalRepository.findById(7L)).thenReturn(Optional.of(animal(AnimalStatus.AVAILABLE)));

        AdoptionResult result = service.evaluateAdoption(1L, 7L);

        assertRejected(result, RejectionReasons.PET_LIMIT_REACHED);
        verify(auditLogger).logRejection(1L, 7L, RejectionReason.PET_LIMIT_REACHED);
        verifyNoInteractions(notificationClient);
    }

    @ParameterizedTest
    @EnumSource(value = AnimalType.class, names = {"BIRD", "RABBIT"})
    void rejectsExoticAnimalWhenAdopterHasNoPermit(AnimalType type) {
        when(adopterRepository.findById(1L)).thenReturn(Optional.of(adultAdopter()));
        when(animalRepository.findById(7L)).thenReturn(Optional.of(animal(type)));

        AdoptionResult result = service.evaluateAdoption(1L, 7L);

        assertRejected(result, RejectionReasons.EXOTIC_PERMIT_REQUIRED);
        verify(auditLogger).logRejection(1L, 7L, RejectionReason.NO_EXOTIC_PERMIT);
        verifyNoInteractions(notificationClient);
    }

    @Test
    void approvesExoticAnimalWhenAdopterHasPermit() {
        Adopter adopter = adultAdopter();
        adopter.setExoticPermit(true);
        when(adopterRepository.findById(1L)).thenReturn(Optional.of(adopter));
        when(animalRepository.findById(7L)).thenReturn(Optional.of(animal(AnimalType.RABBIT)));

        AdoptionResult result = service.evaluateAdoption(1L, 7L);

        assertThat(result.approved()).isTrue();
        assertThat(result.reason()).isEqualTo("Approved");
        verify(auditLogger).logApproval(eq(1L), eq(7L), anyInt());
        verify(notificationClient).sendApprovalNotification("adopter@example.com", "Bella");
    }

    private void assertRejected(AdoptionResult result, String reason) {
        assertThat(result.approved()).isFalse();
        assertThat(result.reason()).isEqualTo(reason);
    }

    private Adopter adultAdopter() {
        return new Adopter(
                1L,
                "Adrijus",
                "adopter@example.com",
                22,
                0,
                0,
                false,
                false
        );
    }

    private Animal animal(AnimalStatus status) {
        return new Animal(
                7L,
                "Bella",
                AnimalType.CAT,
                "Mixed",
                3,
                "Friendly",
                status
        );
    }

    private Animal animal(AnimalType type) {
        Animal animal = animal(AnimalStatus.AVAILABLE);
        animal.setType(type);
        return animal;
    }
}
