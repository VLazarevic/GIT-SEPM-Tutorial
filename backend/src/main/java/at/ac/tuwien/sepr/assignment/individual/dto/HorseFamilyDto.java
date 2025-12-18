package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;

import java.time.LocalDate;

/**
 * Represents a Data Transfer Object (DTO) for creating a new horse entry.
 * This record encapsulates all necessary details for registering a horse.
 */
public class HorseFamilyDto {
  public Long id;
  public String name;
  public LocalDate dateOfBirth;
  public HorseFamilyDto father;
  public HorseFamilyDto mother;

  public HorseFamilyDto(Long id, String name, LocalDate dateOfBirth) {
    this.id = id;
    this.name = name;
    this.dateOfBirth = dateOfBirth;
  }
}