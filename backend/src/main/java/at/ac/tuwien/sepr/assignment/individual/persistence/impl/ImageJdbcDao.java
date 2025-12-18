package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.ImageCreateDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Image;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.ImageDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC implementation of {@link ImageDao} for interacting with the database.
 */
@Repository
public class ImageJdbcDao implements ImageDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "image";

  private static final String SQL_SELECT_ALL =
      "SELECT * FROM " + TABLE_NAME;

  private static final String SQL_SELECT_BY_ID =
      "SELECT * FROM " + TABLE_NAME
          + " WHERE ID = :id";

  private static final String SQL_DELETE =
      "DELETE FROM " + TABLE_NAME + " WHERE id = :id";

  private static final String SQL_CREATE =
      "INSERT INTO " + TABLE_NAME + " (file_type, image) VALUES (:file_type, :image)";

  private final JdbcClient jdbcClient;

  @Autowired
  public ImageJdbcDao(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  /**
   * Retrieves all images from the database.
   *
   * @return A list of all images stored in the database
   */
  @Override
  public List<Image> getAll() {
    LOG.trace("getAll()");
    return jdbcClient.sql(SQL_SELECT_ALL)
        .query(this::mapRow)
        .list();
  }

  /**
   * Creates a new image record in the database.
   *
   * @param file The data transfer object containing the image data to store
   * @return A new Image entity containing the created image's data, including its generated ID
   */
  @Override
  public Image create(ImageCreateDto file) {
    LOG.trace("create({})", file);
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcClient
        .sql(SQL_CREATE)
        .param("file_type", file.fileType())
        .param("image", file.image())
        .update(keyHolder);

    return new Image(
        keyHolder.getKey().longValue(),
        file.fileType(),
        file.image()
    );
  }

  /**
   * Deletes an image from the database by its ID.
   *
   * @param id The ID of the image to delete
   * @throws NotFoundException If no image with the given ID exists
   */
  @Override
  public void delete(long id) throws NotFoundException {
    LOG.trace("delete({})", id);
    jdbcClient.sql(SQL_DELETE)
        .param("id", id).update();
  }

  /**
   * Retrieves a specific image by its ID.
   *
   * @param id The ID of the image to retrieve
   * @return The image with the specified ID
   * @throws NotFoundException If no image with the given ID exists
   * @throws FatalException If multiple images with the same ID are found (should never happen)
   */
  @Override
  public Image getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Image> images = jdbcClient.sql(SQL_SELECT_BY_ID)
        .param("id", id)
        .query(this::mapRow)
        .list();

    if (images.isEmpty()) {
      throw new NotFoundException("No images with ID %d found".formatted(id));
    }
    if (images.size() > 1) {
      // This should never happen!!
      throw new FatalException("Too many images with ID %d found".formatted(id));
    }

    return images.getFirst();
  }

  /**
   * Maps a database result row to an Image entity.
   *
   * @param result The database result set containing the image data
   * @param rownum The row number in the result set
   * @return An Image entity populated with data from the result set
   * @throws SQLException If an error occurs while accessing the result set
   */
  private Image mapRow(ResultSet result, int rownum) throws SQLException {
    return new Image(
        result.getLong("id"),
        result.getString("file_type"),
        result.getBytes("image"));
  }
}
