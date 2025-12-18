package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.ImageDto;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;

/**
 * REST controller for managing image-related operations.
 * Provides endpoints for getting images.
 */
@RestController
@RequestMapping(path = ImageEndpoint.BASE_PATH)
public class ImageEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/images";

  private final ImageService service;

  public ImageEndpoint(ImageService service) {
    this.service = service;
  }

  /**
   * Retrieves the details of a horse by its ID.
   *
   * @param id the unique identifier of the horse
   * @return the detailed information of the requested horse
   * @throws ResponseStatusException if the horse is not found
   */
  @GetMapping("{id}")
  public ResponseEntity<byte[]> getById(@PathVariable("id") long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    try {

      ImageDto result = service.getById(id);
      System.out.println("FileType: " + result.fileType() + " ID: " + result.id());
      return ResponseEntity.ok().contentType(MediaType.parseMediaType(result.fileType())).body(result.image());
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to get details of not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
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
}
