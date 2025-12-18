package at.ac.tuwien.sepr.assignment.individual.dto;

/**
 * Represents a Data Transfer Object (DTO) for owner details.
 * This record encapsulates the essential information about an owner.
 */
public record ImageDto(
    long id,
    String fileType,
    byte[] image
){}
