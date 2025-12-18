package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.ImageCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.ImageDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Image;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.util.stream.Stream;

/**
 * Service for working with files.
 */
public interface ImageService {
  /**
   * Lists all files stored in the system.
   *
   * @return list of all stored files
   */
  Stream<Image> allFiles();


  /**
   * Create the file with the ID given in {@code file}
   * with the data given in {@code file}
   * in the persistent data store.
   *
   * @param file the file to update
   * @return the created file
   */
  ImageDto create(ImageCreateDto file);


  /**
   * Get the file with given ID, with more detail information.
   *
   * @param id the ID of the file to get
   * @return the file with ID {@code id}
   * @throws NotFoundException if the file with the given ID does not exist in the persistent data store
   */
  ImageDto getById(long id) throws NotFoundException;

  /**
   * Delete the file with the given ID
   *
   * @param id the ID of the file which gets deleted
   */
  void delete(long id) throws NotFoundException;
}
