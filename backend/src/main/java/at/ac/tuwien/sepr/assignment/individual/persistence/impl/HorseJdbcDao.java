package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseUpdateDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

/**
 * JDBC implementation of {@link HorseDao} for interacting with the database.
 */
@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "horse";

  private static final String SQL_SELECT_ALL =
      "SELECT * FROM " + TABLE_NAME;

  private static final String SQL_SELECT_BY_ID =
      "SELECT * FROM " + TABLE_NAME
          + " WHERE ID = :id";

  private static final String SQL_UPDATE =
      "UPDATE " + TABLE_NAME
          + """
              SET name = :name,
                  description = :description,
                  date_of_birth = :date_of_birth,
                  sex = :sex,
                  owner_id = :owner_id,
                  image_id = :image_id,
                  mother_id = :mother_id,
                  father_id = :father_id
              WHERE id = :id
          """;

  private static final String SQL_CREATE =
      "INSERT INTO " + TABLE_NAME
          + " (name, description, date_of_birth, sex, owner_id, image_id, mother_id, father_id)"
          + " VALUES (:name, :description, :date_of_birth, :sex, :owner_id, :image_id, :mother_id, :father_id)";

  private static final String SQL_DELETE =
      "DELETE FROM " + TABLE_NAME + " WHERE id = :id";

  private static final String SQL_FAMILY =
      """
      
          WITH RECURSIVE family_tree (id, name, date_of_birth, mother_id, father_id, level) AS (
          -- Base: Start with the root horse
          SELECT
              h.id,
              h.name,
              h.date_of_birth,
              h.mother_id,
              h.father_id,
              0 AS level
          FROM
              horse h
          WHERE
              h.id = :id
          UNION ALL
          SELECT
              h.id,
              h.name,
              h.date_of_birth,
              h.mother_id,
              h.father_id,
              ft.level + 1
          FROM
              horse h
          JOIN
              family_tree ft ON h.id IN (ft.mother_id, ft.father_id)
          WHERE
              ft.level < :gen
      )
      SELECT
          id,
          name,
          date_of_birth,
          mother_id,
          father_id
      FROM
          family_tree
      ORDER BY
          level, id;
      """;

  private final JdbcClient jdbcClient;

  @Autowired
  public HorseJdbcDao(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  /**
   * Retrieves all horses from the database.
   *
   * @return A list of all horses stored in the database
   */
  @Override
  public List<Horse> getAll() {
    LOG.trace("getAll()");
    return jdbcClient
        .sql(SQL_SELECT_ALL)
        .query(this::mapRow)
        .list();
  }

  /**
   * Retrieves a specific horse by its ID.
   *
   * @param id The ID of the horse to retrieve
   * @return The horse with the specified ID
   * @throws NotFoundException If no horse with the given ID exists
   */
  @Override
  public Horse getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Horse> horses = jdbcClient
        .sql(SQL_SELECT_BY_ID)
        .param("id", id)
        .query(this::mapRow)
        .list();

    if (horses.isEmpty()) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
    if (horses.size() > 1) {
      // This should never happen!!
      throw new FatalException("Too many horses with ID %d found".formatted(id));
    }

    return horses.getFirst();
  }

  /**
   * Creates a new horse record in the database.
   *
   * @param horse The data transfer object containing the horse information to store
   * @return A new Horse entity containing the created horse's data, including its generated ID
   */
  @Override
  public Horse create(HorseCreateDto horse) {
    LOG.trace("create({})", horse);
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcClient.sql(SQL_CREATE)
            .param("name", horse.name())
            .param("description", horse.description())
            .param("date_of_birth", horse.dateOfBirth())
            .param("sex", horse.sex().toString())
            .param("owner_id", horse.ownerId())
            .param("image_id", horse.imageId())
            .param("mother_id", horse.motherId())
            .param("father_id", horse.fatherId())
            .update(keyHolder);

    // Create a new Horse record instance directly
    return new Horse(
            keyHolder.getKey().longValue(),
            horse.name(),
            horse.description(),
            horse.dateOfBirth(),
            horse.sex(),
            horse.ownerId(),
            horse.imageId(),
            horse.motherId(),
            horse.fatherId());
  }

  /**
   * Updates an existing horse record in the database.
   *
   * @param horse The data transfer object containing the updated horse information
   * @return The updated Horse entity
   * @throws NotFoundException If no horse with the given ID exists
   */
  @Override
  public Horse update(HorseUpdateDto horse) throws NotFoundException {
    LOG.trace("update({})", horse);
    int updated = jdbcClient
        .sql(SQL_UPDATE)
        .param("id", horse.id())
        .param("name", horse.name())
        .param("description", horse.description())
        .param("date_of_birth", horse.dateOfBirth())
        .param("sex", horse.sex().toString())
        .param("owner_id", horse.ownerId())
        .param("image_id", horse.imageId())
        .param("mother_id", horse.motherId())
        .param("father_id", horse.fatherId())
        .update();

    if (updated == 0) {
      throw new NotFoundException(
          "Could not update horse with ID " + horse.id() + ", because it does not exist"
      );
    }

    return new Horse(
        horse.id(),
        horse.name(),
        horse.description(),
        horse.dateOfBirth(),
        horse.sex(),
        horse.ownerId(),
        horse.imageId(),
        horse.motherId(),
        horse.fatherId());
  }

  /**
   * Deletes a horse from the database by its ID.
   *
   * @param id The ID of the horse to delete
   * @throws NotFoundException If no horse with the given ID exists
   */
  @Override
  public void delete(long id) throws NotFoundException {
    LOG.trace("delete({})", id);
    int deleted = jdbcClient
        .sql(SQL_DELETE)
        .param("id", id)
        .update();

    if (deleted == 0) {
      throw new NotFoundException("Could not delete horse with ID " + id + ", because it does not exist");
    }
  }

  /**
   * Retrieves horses in the family tree of a specific horse.
   *
   * @param id The ID of the root horse for which to retrieve the family
   * @param gen The number of generations to include in the family tree
   * @return A list of horses that are part of the family tree
   */
  @Override
  public List<Horse> getHorseFamilyById(long id, int gen) {
    LOG.trace("getHorseFamilyById({})", id);
    return jdbcClient
        .sql(SQL_FAMILY)
        .param("id", id)
        .param("gen", gen)
        .query(this::mapFamilyRow)
        .list();
  }

  /**
   * Searches for horses based on various filter criteria.
   *
   * @param horse The search parameters to filter horses
   * @return A list of horses matching the search criteria
   */
  @Override
  public List<Horse> searchHorses(HorseSearchDto horse) {
    LOG.trace("searchHorses({})", horse);

    // Build the base query
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT h.* FROM ").append(TABLE_NAME).append(" h");

    // Add JOIN for owner filter if needed
    boolean needsOwnerJoin = horse.ownerName() != null && !horse.ownerName().isBlank();
    if (needsOwnerJoin) {
      sql.append(" LEFT JOIN owner o ON o.id = h.owner_id");
    }

    // Start WHERE clause
    sql.append(" WHERE 1=1"); // Always true to simplify adding conditions

    // Prepare for named parameters
    if (horse.description() != null && !horse.description().isBlank()) {
      sql.append(" AND LOWER(COALESCE(h.description, '')) LIKE LOWER(CONCAT('%', :description, '%'))");
    }

    if (horse.name() != null && !horse.name().isBlank()) {
      sql.append(" AND LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%'))");
    }

    if (horse.sex() != null) {
      sql.append(" AND h.sex = :sex");
    }

    if (horse.bornBefore() != null) {
      sql.append(" AND h.date_of_birth < :bornBefore");
    }

    if (needsOwnerJoin) {
      sql.append(" AND UPPER(o.first_name || ' ' || o.last_name) LIKE UPPER(CONCAT('%', :ownerName, '%'))");
    }

    if (horse.limit() != null && horse.limit() > 0) {
      sql.append(" LIMIT :limit");
    }

    // Build the query with parameters
    var query = jdbcClient.sql(sql.toString());

    // Add parameters only once
    if (horse.description() != null && !horse.description().isBlank()) {
      query = query.param("description", horse.description());
    }

    if (horse.name() != null && !horse.name().isBlank()) {
      query = query.param("name", horse.name());
    }

    if (horse.sex() != null) {
      query = query.param("sex", horse.sex().toString());
    }

    if (horse.bornBefore() != null) {
      query = query.param("bornBefore", horse.bornBefore());
    }

    if (needsOwnerJoin) {
      query = query.param("ownerName", horse.ownerName());
    }

    if (horse.limit() != null && horse.limit() > 0) {
      query = query.param("limit", horse.limit());
    }

    // Execute query and return results
    return query.query(this::mapRow).list();
  }

  /**
   * Maps a database result row to a Horse entity with all properties.
   *
   * @param result The database result set containing the horse data
   * @param rownum The row number in the result set
   * @return A Horse entity populated with data from the result set
   * @throws SQLException If an error occurs while accessing the result set
   */
  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    return new Horse(
        result.getLong("id"),
        result.getString("name"),
        result.getString("description"),
        result.getDate("date_of_birth").toLocalDate(),
        Sex.valueOf(result.getString("sex")),
        result.getObject("owner_id", Long.class),
        result.getLong("image_id"),
        result.getLong("mother_id"),
        result.getLong("father_id"));
  }

  /**
   * Maps a database result row to a Horse entity with minimal properties for family tree.
   *
   * @param result The database result set containing the horse data
   * @param rownum The row number in the result set
   * @return A Horse entity populated with essential family tree data
   * @throws SQLException If an error occurs while accessing the result set
   */
  private Horse mapFamilyRow(ResultSet result, int rownum) throws SQLException {
    return new Horse(
        result.getLong("id"),
        result.getString("name"),
        null,
        result.getDate("date_of_birth").toLocalDate(),
        null,
        null,
        null,
        result.getLong("mother_id"),
        result.getLong("father_id")
    );
  }
}
