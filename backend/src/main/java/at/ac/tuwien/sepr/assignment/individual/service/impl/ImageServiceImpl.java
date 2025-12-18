package at.ac.tuwien.sepr.assignment.individual.service.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.ImageCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.ImageDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Image;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.mapper.ImageMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.ImageDao;
import at.ac.tuwien.sepr.assignment.individual.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@Service
public class ImageServiceImpl implements ImageService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final ImageDao dao;
  private final ImageMapper mapper;

  @Autowired
  public ImageServiceImpl(ImageDao dao, ImageMapper mapper) {
    this.dao = dao;
    this.mapper = mapper;
  }

  /**
   * Retrieves all image files stored in the system.
   *
   * @return a stream of all stored image files
   */
  @Override
  public Stream<Image> allFiles() {
    LOG.trace("allFiles()");
    var files = dao.getAll();
    return files.stream();
  }

  /**
   * Creates a new image with the information given in the parameter file.
   *
   * @param file the image data to create
   * @return the created image as a DTO
   */
  @Override
  public ImageDto create(ImageCreateDto file) {
    LOG.trace("create({})", file.fileType());
    return mapper.entityToFileDto(dao.create(file));
  }

  /**
   * Get the image with the given ID.
   *
   * @param id the ID of the image to get
   * @return the image with the specified ID as a DTO
   * @throws NotFoundException if the image with the given ID does not exist
   */
  @Override
  public ImageDto getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    return mapper.entityToFileDto(dao.getById(id));
  }

  /**
   * Deletes an image with the given ID from the database.
   *
   * @param id the ID of the image to delete
   * @throws NotFoundException if the image with the given ID does not exist
   */
  @Override
  public void delete(long id) throws NotFoundException {
    LOG.trace("delete({})", id);
    dao.delete(id);
  }
}
