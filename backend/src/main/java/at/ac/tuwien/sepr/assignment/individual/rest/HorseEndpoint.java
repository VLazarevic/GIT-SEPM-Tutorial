package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.ImageCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateRestDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseFamilyDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseUpdateRestDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.ImageService;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for managing horse-related operations.
 * Provides endpoints for searching, retrieving, creating, updating, and deleting horses,
 * as well as fetching their family tree.
 */
@RestController
@RequestMapping(path = HorseEndpoint.BASE_PATH)
public class HorseEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/horses";

  private final HorseService service;
  private final ImageService imageService;

  @Autowired
  public HorseEndpoint(HorseService service, ImageService imageService) {
    this.service = service;
    this.imageService = imageService;
  }

  /**
   * Searches for horses based on the given search parameters.
   *
   * @param searchParameters the parameters to filter the horse search
   * @return a stream of {@link HorseListDto} matching the search criteria
   */
  @GetMapping
  public Stream<HorseListDto> searchHorses(HorseSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchParameters);
    return service.searchHorses(searchParameters);
  }

  /**
   * Retrieves the details of a horse by its ID.
   *
   * @param id the unique identifier of the horse
   * @return the detailed information of the requested horse
   * @throws ResponseStatusException if the horse is not found
   */
  @GetMapping("{id}")
  public HorseDetailDto getById(@PathVariable("id") long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    try {
      return service.getById(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to get details of not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }


  /**
   * Updates the details of an existing horse, including an optional imageId file.
   *
   * @param id       the ID of the horse to update
   * @param toUpdate the updated horse data
   * @return the updated horse details
   * @throws ValidationException     if validation fails
   * @throws ConflictException       if a conflict occurs while updating
   * @throws ResponseStatusException if the horse is not found
   */
  @PutMapping(path = "{id}")
  public HorseDetailDto update(
      @PathVariable("id") long id,
      @ModelAttribute HorseUpdateRestDto toUpdate)
      throws ValidationException, ConflictException, IOException {
    LOG.info("PUT " + BASE_PATH + "/{}", toUpdate);
    LOG.debug("Body of request:\n{}", toUpdate);
    try {
      Long fileId = processImageIfPresent(toUpdate.image());
      return service.update(toUpdate.toUpdateDtoWithId(id, fileId));
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to update not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Updates the details of an existing horse, including an optional imageId file.
   *
   * @param horse the horse to update
   * @return the created horse
   * @throws ValidationException if validation fails
   * @throws ConflictException   if a conflict occurs while updating
   * @throws IOException         if a file problem occurs
   */
  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public HorseDetailDto create(@ModelAttribute HorseCreateRestDto horse) throws ValidationException, ConflictException, IOException {
    LOG.info("POST " + BASE_PATH + "/{}", horse);
    LOG.debug("Body of request:\n{}", horse);
    Long fileId = processImageIfPresent(horse.image());
    System.out.println(horse.name());
    return service.create(horse.toHorseCreateDtoWithFile(fileId));
  }

  /**
   * Deletes a horse by its ID.
   *
   * @param id the ID of the horse to delete
   * @throws ResponseStatusException if the horse is not found
   */
  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") long id) {
    LOG.info("DELETE " + BASE_PATH + "/{}", id);
    try {
      service.delete(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to delete not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Retrieves the family tree of a horse by its ID.
   *
   * @param id The unique identifier of the horse
   * @param gen The number of generations to include in the family tree
   * @return The family tree of the specified horse
   * @throws NotFoundException If the horse with the given ID does not exist
   * @throws ValidationException If the generation parameter is invalid
   */
  @GetMapping("{id}/family")
  public HorseFamilyDto getHorseFamilyById(
      @PathVariable("id") long id,
      @RequestParam(name = "gen") Integer gen)
      throws NotFoundException, ValidationException {

    LOG.info("GET " + BASE_PATH + "/{}/family with gen {}", id, gen);
    return service.getHorseFamilyById(id, gen);
  }


  /**
   * Logs client-side errors with relevant details.
   *
   * @param status  the HTTP status code of the error
   * @param message a brief message describing the error
   * @param e       the exception that occurred
   */
  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }

  private Long processImageIfPresent(MultipartFile image) throws IOException {
    return image == null || image.isEmpty() ? null : imageService.create(new ImageCreateDto(
        image.getContentType(),
        image.getBytes()
    )).id();
  }

}
