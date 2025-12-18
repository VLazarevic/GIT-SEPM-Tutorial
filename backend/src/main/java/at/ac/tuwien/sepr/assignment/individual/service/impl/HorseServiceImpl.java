package at.ac.tuwien.sepr.assignment.individual.service.impl;


import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseUpdateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseFamilyDto;
import at.ac.tuwien.sepr.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.ImageDao;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import at.ac.tuwien.sepr.assignment.individual.service.OwnerService;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link HorseService} for handling imageId storage and retrieval.
 */
@Service
public class HorseServiceImpl implements HorseService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;
  private final ImageDao imageDao;
  private final HorseMapper mapper;
  private final HorseValidator validator;
  private final OwnerService ownerService;


  @Autowired
  public HorseServiceImpl(HorseDao dao,
                          HorseMapper mapper,
                          HorseValidator validator,
                          OwnerService ownerService,
                          ImageDao imageDao) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
    this.ownerService = ownerService;
    this.imageDao = imageDao;
  }

  /**
   * Retrieves all horses stored in the system.
   *
   * @return a stream of all stored horses as DTOs
   */
  @Override
  public Stream<HorseListDto> allHorses() {
    LOG.trace("allHorses()");
    var horses = dao.getAll();
    var ownerIds = horses.stream()
        .map(Horse::ownerId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
    Map<Long, OwnerDto> ownerMap;
    try {
      ownerMap = ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
    }
    return horses.stream()
        .map(horse -> mapper.entityToListDto(horse, ownerMap));
  }

  /**
   * Updates the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse as a detailed DTO
   * @throws NotFoundException if the horse with given ID does not exist in the persistent data store
   * @throws ValidationException if the update data given for the horse is in itself incorrect (description too long, no name, …)
   * @throws ConflictException if the update data given for the horse is in conflict the data currently in the system (owner does not exist, …)
   */
  @Override
  public HorseDetailDto update(HorseUpdateDto horse) throws NotFoundException, ValidationException, ConflictException {
    LOG.trace("update({})", horse);
    validator.validateForUpdate(horse);

    var updatedHorse = dao.update(horse);
    return mapper.entityToDetailDto(
        updatedHorse,
        ownerMapForSingleId(updatedHorse.ownerId()));
  }

  /**
   * Creates a horse with the information given in the parameter horse.
   *
   * @param horse the horse to create
   * @return the created horse as a detailed DTO
   * @throws ValidationException if the data given for the horse is in itself incorrect (description too long, no name, …)
   * @throws ConflictException if the data given for the horse is in conflict with the data currently in the system (owner does not exist, …)
   */
  public HorseDetailDto create(HorseCreateDto horse) throws ValidationException, ConflictException {
    LOG.trace("create({})", horse);
    validator.validateForCreate(horse);
    var createdHorse = dao.create(horse);
    return mapper.entityToDetailDto(
        createdHorse,
        ownerMapForSingleId(createdHorse.ownerId()));
  }

  /**
   * Get the horse with given ID, with more detail information.
   * This includes the owner of the horse, and its parents.
   * The parents of the parents are not included.
   *
   * @param id the ID of the horse to get
   * @return the horse with ID {@code id} as a detailed DTO
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   */
  @Override
  public HorseDetailDto getById(long id) throws NotFoundException {
    LOG.trace("details({})", id);
    Horse horse = dao.getById(id);
    return mapper.entityToDetailDto(
        horse,
        ownerMapForSingleId(horse.ownerId()));
  }

  /**
   * Deletes a horse with the given ID from the database.
   * If the horse has an associated image, that image is also deleted.
   *
   * @param id the ID of the horse to delete
   * @throws NotFoundException if the horse with the given ID does not exist
   */
  @Override
  public void delete(long id) throws NotFoundException {
    LOG.trace("delete({})", id);
    var imageID = dao.getById(id).imageId();
    dao.delete(id);
    if (imageID != 0) {
      LOG.trace("delete file({})", id);
      imageDao.delete(imageID);
    }
  }


  /**
   * Returns the family tree for a horse
   *
   * @param id    the ID of the root horse for the tree
   * @param gen the number of generations to fetch (must be positive)
   * @return the family tree with the specified horse as the root
   * @throws NotFoundException   if the horse with given ID does not exist in the persistent data store
   * @throws ValidationException if generations parameter is invalid
   */
  @Override
  public HorseFamilyDto getHorseFamilyById(long id, Integer gen) throws ValidationException, NotFoundException {
    LOG.trace("getHorseFamilyById({}, {})", id, gen);
    if (gen < 1) {
      throw new ValidationException("Invalid generations parameter, must be positive", null);
    }

    var map = new HashMap<Long, HorseFamilyDto>();
    for (Horse horse : dao.getHorseFamilyById(id, gen)) {
      map.put(horse.id(), new HorseFamilyDto(horse.id(), horse.name(), horse.dateOfBirth()));
    }

    for (Horse horse : dao.getHorseFamilyById(id, gen)) {
      if (horse.motherId() != 0) {
        map.get(horse.id()).mother = map.get(horse.motherId());
      }
      if (horse.fatherId() != 0) {
        map.get(horse.id()).father = map.get(horse.fatherId());
      }
    }

    return map.get(id);
  }

  /**
   * Searches for horses based on the provided search criteria.
   *
   * @param horse the search parameters to filter horses
   * @return a stream of horses matching the search criteria
   * @throws FatalException if a referenced owner is not found
   */
  @Override
  public Stream<HorseListDto> searchHorses(HorseSearchDto horse) {
    LOG.trace("searchHorses({})", horse);

    // Hole alle Pferde, die den Suchkriterien entsprechen
    List<Horse> wantedHorses = dao.searchHorses(horse);

    // Hole die Besitzer für alle gefundenen Pferde
    var ownerIds = wantedHorses.stream()
        .map(Horse::ownerId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());

    Map<Long, OwnerDto> ownerMap;
    try {
      ownerMap = ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
    }

    // Konvertiere alle Pferd-Entitäten in DTOs und gebe sie als Stream zurück
    return wantedHorses.stream()
        .map(h -> mapper.entityToListDto(h, ownerMap));
  }

  /**
   * Creates a map containing a single owner entry for the given owner ID.
   *
   * @param ownerId the ID of the owner to retrieve
   * @return a map with the owner ID as key and the owner DTO as value, or null if ownerId is null
   * @throws FatalException if the referenced owner is not found
   */
  private Map<Long, OwnerDto> ownerMapForSingleId(Long ownerId) {
    try {
      return ownerId == null
          ? null
          : Collections.singletonMap(ownerId, ownerService.getById(ownerId));
    } catch (NotFoundException e) {
      throw new FatalException("Owner %d referenced by horse not found".formatted(ownerId));
    }
  }

}
