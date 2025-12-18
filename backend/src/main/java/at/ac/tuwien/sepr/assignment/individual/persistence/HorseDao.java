package at.ac.tuwien.sepr.assignment.individual.persistence;


import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseUpdateDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import java.util.List;

/**
 * Data Access Object for horses.
 * Implements access functionality to the application's persistent data store regarding horses.
 */
public interface HorseDao {
  /**
   * Get all horses stored in the persistent data store.
   *
   * @return a list of all stored horses
   */
  List<Horse> getAll();

  /**
   * Create the horse with the horse data {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to create
   * @return the creates horse
   */
  Horse create(HorseCreateDto horse);


  /**
   * Update the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse update(HorseUpdateDto horse) throws NotFoundException;


  /**
   * Get a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to get
   * @return the horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse getById(long id) throws NotFoundException;

  /**
   * Deletes a horse with the given ID from the database.
   *
   * @param id the ID of the horse to delete
   * @throws NotFoundException if the horse with the given ID does not exist
   */
  void delete(long id) throws NotFoundException;

  /**
   * Get the horses with given serach parameters.
   *
   * @param id id of horse the family tree is wanted
   * @param gen depth of the family tree
   * @return list of horses that are in the family of given horse
   */
  List<Horse> getHorseFamilyById(long id, int gen);

  /**
   * Get the horses with given serach parameters.
   *
   * @param horse search parameters
   * @return list of horses that matches the search parameters
   */
  List<Horse> searchHorses(HorseSearchDto horse);

}
