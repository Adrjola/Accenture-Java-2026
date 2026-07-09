package lv.bootcamp.shelter.service;

/**
 * Thrown when a requested animal does not exist in the shelter.
 */
public class AnimalNotFoundException extends RuntimeException {

    public AnimalNotFoundException(Long id) {
        super("Animal not found: " + id);
    }
}
