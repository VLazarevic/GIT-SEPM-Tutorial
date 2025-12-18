package at.ac.tuwien.sepr.assignment.individual.entity;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * Represents a horse in the persistent data store.
 */
public record Horse(
    Long id,
    String name,
    String breed,
    String color,
    String description,
    String type,
    Integer age,
    LocalDate dateOfBirth,
    Sex sex
) {

}
