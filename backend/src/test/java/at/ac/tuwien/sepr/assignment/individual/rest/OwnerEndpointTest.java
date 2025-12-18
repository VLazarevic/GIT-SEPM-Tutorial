package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.OwnerDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class OwnerEndpointTest {

  @Autowired
  private WebApplicationContext webAppContext;
  private MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
  }

  /**
   * Tests retrieving all owners from the endpoint.
   *
   * @throws Exception if the request fails
   */
  @Test
  public void gettingAllOwners() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/owners")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    List<OwnerDto> horseResult = objectMapper.readerFor(OwnerDto.class).<OwnerDto>readValues(body).readAll();

    assertThat(horseResult).isNotNull();
    assertThat(horseResult.size()).isEqualTo(10);
    assertThat(horseResult)
        .extracting(OwnerDto::id, OwnerDto::firstName, OwnerDto::lastName)
        .containsAll(List.of(
            tuple(-10L, "Max", "Mustermann"),
            tuple(-9L, "Predrag", "Lazarevic"),
            tuple(-8L, "Pamela", "Lazarevic"),
            tuple(-7L, "Lukas", "Reif"),
            tuple(-6L, "Leonardo", "Lazarevic"),
            tuple(-5L, "Jan Guenther", "Giefing"),
            tuple(-4L, "Philipp", "Maurer"),
            tuple(-3L, "Karlo", "Peranovic"),
            tuple(-2L, "Alexandra", "Ladislai"),
            tuple(-1L, "Valentino", "Lazarevic")
        ));
  }

}
