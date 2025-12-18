package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import java.lang.invoke.MethodHandles;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Mapper class responsible for converting {@link Horse} entities into various DTOs.
 */
@Component
public class HorseMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


  /**
   * Converts a {@link Horse} entity into a {@link HorseListDto}.
   * The given map of owners must contain the owner referenced by the horse.
   *
   * @param horse  the horse entity to convert
   * @param owners a map of horse owners by their ID
   * @return the converted {@link HorseListDto}
   */
  public HorseListDto entityToListDto(Horse horse, Map<Long, OwnerDto> owners) {
    LOG.trace("entityToDto({})", horse);
    if (horse == null) {
      return null;
    }

    return new HorseListDto(
        horse.id(),
        horse.name(),
        horse.description(),
        horse.dateOfBirth(),
        horse.sex(),
        getOwner(horse, owners),
        horse.motherId(),
        horse.fatherId()
    );
  }

  /**
   * Converts a {@link Horse} entity into a {@link HorseDetailDto}.
   * The given maps must contain the owners and parents referenced by the horse.
   *
   * @param horse   the horse entity to convert
   * @param owners  a map of horse owners by their ID
   * @return the converted {@link HorseDetailDto}
   */
  public HorseDetailDto entityToDetailDto(
      Horse horse,
      Map<Long, OwnerDto> owners) {
    LOG.trace("entityToDto({})", horse);
    if (horse == null) {
      return null;
    }

    return new HorseDetailDto(
        horse.id(),
        horse.name(),
        horse.description(),
        horse.dateOfBirth(),
        horse.sex(),
        getOwner(horse, owners),
        horse.imageId(),
        horse.motherId(),
        horse.fatherId()
    );
  }

  /**
   * Retrieves the owner DTO for a given horse from a map of owners.
   *
   * @param horse The horse entity for which to find the owner
   * @param owners A map of owner IDs to owner DTOs
   * @return The owner DTO associated with the horse, or null if the horse has no owner
   * @throws FatalException If the horse has an owner ID but the corresponding owner is not found in the map
   */
  private OwnerDto getOwner(Horse horse, Map<Long, OwnerDto> owners) {
    OwnerDto owner = null;
    var ownerId = horse.ownerId();
    if (ownerId != null) {
      if (!owners.containsKey(ownerId)) {
        throw new FatalException("Given owner map does not contain owner of this Horse (%d)".formatted(horse.id()));
      }
      owner = owners.get(ownerId);
    }
    return owner;
  }

}
