package at.ac.tuwien.sepr.assignment.individual.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for {@link HorseService}.
 */
@ActiveProfiles({"test", "datagen"}) // Enables "test" Spring profile during test execution
@SpringBootTest
public class HorseServiceTest {

  @Autowired
  HorseService horseService;

  /**
   * Tests whether retrieving all stored horses returns the expected number and specific entries.
   */
  @Test
  public void getAllReturnsAllStoredHorses() {
    List<HorseListDto> horses = horseService.allHorses()
        .toList();
    assertThat(horses.size()).isGreaterThanOrEqualTo(13);
    assertThat(horses)
        .map(HorseListDto::id, HorseListDto::sex)
        .contains(tuple(-1L, Sex.FEMALE));
  }

  /**
   * Tests that a horse can be retrieved by its ID and that the returned details are correct.
   */
  @Test
  public void getByIdReturnsHorseById() {
    HorseDetailDto horse = Assertions.assertDoesNotThrow(() -> horseService.getById(-1L));
    assertThat(horse.id()).isEqualTo(-1L);
    assertThat(horse.name().compareTo("Wendy") == 0).isTrue();
    assertThat(horse.description().compareTo("The famous one!") == 0).isTrue();
    assertThat(horse.sex() == Sex.FEMALE).isTrue();
    assertThat(horse.fatherId() == null).isFalse();
    assertThat(horse.motherId() == null).isFalse();
  }

  /**
   * Tests that searching for horses without any filters returns all horses in the database.
   */
  @Test
  public void searchHorsesReturnsHorseById() {
    List<HorseListDto> horses = Assertions.assertDoesNotThrow(() -> horseService.searchHorses(new HorseSearchDto(null, null, null, null, null, null))).toList();
    assertThat(horses.size()).isGreaterThanOrEqualTo(13);
    assertThat(horses)
        .map(HorseListDto::id, HorseListDto::sex)
        .contains(tuple(-1L, Sex.FEMALE));
  }

  /**
   * Tests that searching for horses with various filters returns the expected filtered results.
   * Includes tests for filtering by sex, name, date of birth, and limiting the result count.
   */
  @Test
  public void searchHorsesWithFilterReturnsFilteredResults() {
    HorseSearchDto femaleFilter = new HorseSearchDto(null, null, null, Sex.FEMALE, null, null);
    List<HorseListDto> femaleHorses = horseService.searchHorses(femaleFilter).toList();

    assertThat(femaleHorses).isNotEmpty();
    assertThat(femaleHorses)
        .extracting(HorseListDto::sex)
        .containsOnly(Sex.FEMALE);

    HorseSearchDto nameFilter = new HorseSearchDto("Wendy", null, null, null, null, null);
    List<HorseListDto> wendyHorses = horseService.searchHorses(nameFilter).toList();

    assertThat(wendyHorses).isNotEmpty();
    assertThat(wendyHorses)
        .extracting(HorseListDto::name)
        .anyMatch(name -> name.contains("Wendy"));

    LocalDate cutoffDate = LocalDate.of(2015, 1, 1);
    HorseSearchDto dateFilter = new HorseSearchDto(null, null, cutoffDate, null, null, null);
    List<HorseListDto> olderHorses = horseService.searchHorses(dateFilter).toList();

    assertThat(olderHorses).isNotEmpty();

    HorseSearchDto limitFilter = new HorseSearchDto(null, null, null, null, null, 2);
    List<HorseListDto> limitedHorses = horseService.searchHorses(limitFilter).toList();

    assertThat(limitedHorses).hasSizeLessThanOrEqualTo(2);
  }

  /**
   * Tests that attempting to retrieve a non-existent horse by ID throws a NotFoundException.
   */
  @Test
  public void negativeGetByIdReturnsCreatedHorse() {
    Assertions.assertThrows(NotFoundException.class, () -> horseService.getById(-99L));
  }
}
