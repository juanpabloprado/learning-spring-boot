package com.juanpabloprado.learningspringboot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.repository.InMemoryMetricRepository;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

  private static final String FILES_UPLOADED = "files.uploaded";
  private static final String FILES_UPLOADED_LAST_BYTES = "files.uploaded.lastBytes";
  private static final String FILES_UPLOADED_TOTAL_BYTES = "files.uploaded.totalBytes";
  private static String UPLOAD_ROOT = "upload-dir";

  private final ImageRepository repository;
  private final ResourceLoader resourceLoader;
  private final CounterService counterService;
  private final GaugeService gaugeService;
  private final InMemoryMetricRepository inMemoryMetricRepository;

  @Autowired
  public ImageService(ImageRepository repository, ResourceLoader resourceLoader,
      CounterService counterService, GaugeService gaugeService, InMemoryMetricRepository inMemoryMetricRepository) {
    this.repository = repository;
    this.resourceLoader = resourceLoader;
    this.counterService = counterService;
    this.gaugeService = gaugeService;
    this.inMemoryMetricRepository = inMemoryMetricRepository;

    this.counterService.reset(FILES_UPLOADED);
    this.gaugeService.submit(FILES_UPLOADED_LAST_BYTES, 0);
    this.inMemoryMetricRepository.set(new Metric<Number>(FILES_UPLOADED_TOTAL_BYTES, 0));
  }

  public Page<Image> findPage(Pageable pageable) {
    return repository.findAll(pageable);
  }

  public Resource findOneImage(String filename) {
    return resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + filename);
  }

  public void createImage(MultipartFile file) throws IOException {
    if (!file.isEmpty()) {
      Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));
      repository.save(new Image(file.getOriginalFilename()));
      counterService.increment(FILES_UPLOADED);
      gaugeService.submit(FILES_UPLOADED_LAST_BYTES, file.getSize());
      inMemoryMetricRepository.increment(new Delta<Number>(FILES_UPLOADED_TOTAL_BYTES, file.getSize()));
    }
  }

  public void deleteImage(String filename) throws IOException {
    final Image byName = repository.findByName(filename);
    repository.delete(byName);
    Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
  }

  @Bean
    //@Profile("dev")
  CommandLineRunner setUp(ImageRepository repository) throws IOException {
    return (args) -> {
      FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));
      Files.createDirectory(Paths.get(UPLOAD_ROOT));

      FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT + "/test"));
      repository.save(new Image("test"));

      FileCopyUtils.copy("Test file2", new FileWriter(UPLOAD_ROOT + "/test2"));
      repository.save(new Image("test2"));

      FileCopyUtils.copy("Test file3", new FileWriter(UPLOAD_ROOT + "/test3"));
      repository.save(new Image("test3"));
    };
  }
}
