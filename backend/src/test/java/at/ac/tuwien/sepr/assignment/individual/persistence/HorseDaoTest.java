package at.ac.tuwien.sepr.assignment.individual.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;

import java.time.LocalDate;
import java.util.List;

import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for {@link HorseDao}, ensuring database operations function correctly.
 */
@ActiveProfiles({"test", "datagen"}) // Enables "test" Spring profile to load test data
@SpringBootTest
public class HorseDaoTest {

  @Autowired
  HorseDao horseDao;

  /**
   * Tests that retrieving all stored horses returns at least one entry
   * and verifies that a specific horse exists in the test dataset.
   */
  @Test
  public void getAllReturnsAllStoredHorses() {
    List<Horse> horses = horseDao.getAll();
    assertThat(horses.size()).isEqualTo(22);
    assertThat(horses)
        .extracting(Horse::id, Horse::name)
        .contains(tuple(-1L, "Wendy")).doesNotContain(
            tuple(-1L, "Not Wendy")
        );
  }

  /**
   * Tests that retrieving recursive with parents returns only root with 1 generation
   */
  @Test
  public void getWithParentsRecursiveWith1GenerationReturnsExpectedList() {
    List<Horse> horses = horseDao.getHorseFamilyById(-10L, 1);
    assertThat(horses.size()).isEqualTo(3);
    assertThat(horses)
        .extracting(Horse::id, Horse::name, Horse::motherId, Horse::fatherId)
        .containsAll(List.of(tuple(-10L, "Larry", -6L, -7L),
            tuple(-7L, "Steve", -1L, -2L),
            tuple(-6L, "Linda", -1L, -2L)));
  }

  /**
   * Tests that retrieving recursive with parents returns expected data with 3 generations
   */
  @Test
  public void getWithParentsRecursiveWith3GenerationsReturnsExpectedList() {
    List<Horse> horses = horseDao.getHorseFamilyById(-10L, 3);
    assertThat(horses.size()).isEqualTo(7);
    assertThat(horses)
        .extracting(Horse::id, Horse::name, Horse::motherId, Horse::fatherId)
        .containsAll(List.of(tuple(-10L, "Larry", -6L, -7L),
            tuple(-7L, "Steve", -1L, -2L),
            tuple(-6L, "Linda", -1L, -2L),
            tuple(-2L, "Lucky", 0L, 0L),
            tuple(-2L, "Lucky", 0L, 0L),
            tuple(-1L, "Wendy", 0L, 0L),
            tuple(-1L, "Wendy", 0L, 0L)
        ));
  }

  /**
   * Tests that searching horses works
   */
  @Test
  public void searchReturnsExpectedList() {
    List<Horse> horses = horseDao.searchHorses(new HorseSearchDto("L", null, null, Sex.FEMALE, null, 3));
    assertThat(horses.size()).isEqualTo(3);
    assertThat(horses)
        .extracting(Horse::id, Horse::name, Horse::motherId, Horse::fatherId)
        .containsAll(List.of(
            tuple(-22L, "Willow Jr", -20L, -17L),
            tuple(-16L, "Bella", -12L, -11L),
            tuple(-14L, "Willow", 0L, 0L)
        ));
  }
}
