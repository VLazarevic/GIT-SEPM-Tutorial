package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.ImageDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

/**
 * Mapper class responsible for converting {@link Image} entities into {@link ImageDto}.
 */
@Component
public class ImageMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Converts a {@link Image} entity into a {@link ImageDto}.
   * The given maps must contain the owners and parents referenced by the horse.
   *
   * @param image the horse entity to convert
   * @return the converted {@link ImageDto}
   */
  public ImageDto entityToFileDto(Image image) {
    LOG.trace("entityToDto({})", image);
    return image == null ? null : new ImageDto(image.id(), image.fileType(), image.image());
  }

}
