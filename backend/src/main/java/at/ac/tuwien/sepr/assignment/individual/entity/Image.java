package at.ac.tuwien.sepr.assignment.individual.entity;

/**
 * Represents an image in the persistent data store.
 */
public record Image(
    long id,
    String fileType,
    byte[] image
){

}
