package at.ac.tuwien.sepr.assignment.individual.service.impl;


import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseUpdateDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Validator for horse-related operations, ensuring that all horse data meets the required constraints.
 */
@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;

  public HorseValidator(HorseDao dao) {
    this.dao = dao;
  }

  /**
   * Validates a horse before updating, ensuring all fields meet constraints and checking for conflicts.
   *
   * @param horse the {@link HorseUpdateDto} to validate
   * @throws ValidationException if validation fails
   * @throws ConflictException   if conflicts with existing data are detected
   */
  public void validateForUpdate(HorseUpdateDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (horse.id() == null) {
      validationErrors.add("No ID given");
    }

    if (horse.description() != null) {
      if (horse.description().isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (horse.description().length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
    }

    validateParents(horse.motherId(), horse.fatherId(), validationErrors);

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }

  }

  /**
   * Validates a horse before creating, ensuring all fields meet constraints and checking for conflicts.
   *
   * @param horse the {@link HorseCreateDto} to validate
   * @throws ValidationException if validation fails
   * @throws ConflictException   if conflicts with existing data are detected
   */
  public void validateForCreate(HorseCreateDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForCreate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (horse.name() == null) {
      validationErrors.add("Horse name is not given");
    } else if (horse.name().isBlank()) {
      validationErrors.add("Horse name is blank");
    } else if (horse.name().length() > 4095) {
      validationErrors.add("Horse name too long: longer than 4095 characters");
    }

    if (horse.description() != null) {
      if (horse.description().length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
    }

    if (horse.dateOfBirth() == null) {
      validationErrors.add("Horse date of birth is not given");
    } else if (horse.dateOfBirth().isAfter(LocalDate.now())) {
      validationErrors.add("Horse date of birth is not allowed to be in the future");
    }

    if (horse.sex() == null) {
      validationErrors.add("Horse sex is not given");
    } else if (horse.sex().toString().compareToIgnoreCase("FEMALE") != 0 && horse.sex().toString().compareToIgnoreCase("MALE") != 0) {
      // it should not be possible to get into this case because sex is an enum
      validationErrors.add("Horse sex is must be MALE or FEMALE");
    }
    System.out.println("Motherid: " + horse.motherId());
    System.out.println("Fatherid: " + horse.fatherId());
    validateParents(horse.motherId(), horse.fatherId(), validationErrors);

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }
  }

  /**
   * Validates that the parents specified for a horse exist and have the correct sex.
   * For a mother, verifies that the horse exists and is female.
   * For a father, verifies that the horse exists and is male.
   * Any validation errors are added to the provided list.
   *
   * @param motherId the ID of the horse specified as mother, or null if none
   * @param fatherId the ID of the horse specified as father, or null if none
   * @param validationErrors the list to which any validation errors will be added
   */
  private void validateParents(Long motherId, Long fatherId, List<String> validationErrors) {
    // Check if parents exist and have the correct sex
    if (motherId != null) {
      try {
        Horse mother = dao.getById(motherId);
        if (mother.sex() != Sex.FEMALE) {
          validationErrors.add("Horse assigned as mother must be female");
        }
      } catch (NotFoundException e) {
        validationErrors.add("Horse with ID " + motherId + " specified as mother not found");
      }
    }

    if (fatherId != null) {
      try {
        Horse father = dao.getById(fatherId);
        if (father.sex() != Sex.MALE) {
          validationErrors.add("Horse assigned as father must be male");
        }
      } catch (NotFoundException e) {
        validationErrors.add("Horse with ID " + fatherId + " specified as father not found");
      }
    }
  }
}
