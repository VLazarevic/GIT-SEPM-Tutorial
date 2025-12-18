package at.ac.tuwien.sepr.assignment.individual.persistence;


import at.ac.tuwien.sepr.assignment.individual.dto.ImageCreateDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Image;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.util.List;

/**
 * Data Access Object for files.
 * Implements access functionality to the application's persistent data store regarding files.
 */
public interface ImageDao {
  /**
   * Get all files stored in the persistent data store.
   *
   * @return a list of all stored files
   */
  List<Image> getAll();

  /**
   * Create the file with the file data {@code file}
   * in the persistent data store.
   *
   * @param file the file to create
   * @return the creates file
   */
  Image create(ImageCreateDto file);


  /**
   * Delete the file with the ID given in {@code file}
   * with the data given in {@code file}
   * in the persistent data store.
   *
   * @param id the ID of the file to get
   * @throws NotFoundException if the Image with the given ID does not exist in the persistent data store
   */
  void delete(long id) throws NotFoundException;


  /**
   * Get a file by its ID from the persistent data store.
   *
   * @param id the ID of the file to get
   * @return the file
   * @throws NotFoundException if the Image with the given ID does not exist in the persistent data store
   */
  Image getById(long id) throws NotFoundException;
}
