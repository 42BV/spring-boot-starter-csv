package nl._42.boot.csv.file;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.csv.CsvResult;
import nl._42.boot.csv.CsvService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

import static java.lang.String.format;

@Slf4j
@Service
@AllArgsConstructor
@ConditionalOnProperty("csv.file.directory")
public class CsvFileService {

  static final String UPLOAD  = "upload";
  static final String WORK    = "work";
  static final String SUCCESS = "success";
  static final String FAIL    = "fail";
  static final String LOGS    = "logs";

  private final CsvFileProperties properties;
  private final CsvService service;

  @PostConstruct
  void runOnStartup() {
    if (properties.isRunOnStartup()) {
      run();
    }
  }

  @Scheduled(cron = "${csv.file.cron}")
  public void run() {
    Collection<String> types = getTypes();
    types.forEach(this::run);
  }

  private Collection<String> getTypes() {
    Collection<String> types = properties.getOrder();
    if (types.isEmpty()) {
      types = service.getTypes();
    }
    return types;
  }

  public void run(String type) {
    File upload = properties.getDirectory(type, UPLOAD);
    File[] files = upload.listFiles();
    for (File file : files) {
      if (isSupported(file)) {
        moveToWork(type, file).ifPresent(moved -> run(type, moved));
      }
    }
  }

  private boolean isSupported(File file) {
    return file.getName().endsWith(".csv");
  }

  private Optional<File> moveToWork(String type, File file) {
    File work = properties.getDirectory(type, WORK);
    String timestamped = addTimestamp(file);

    return move(file, new File(work, timestamped));
  }

  private String addTimestamp(File file) {
    String name = file.getName().toLowerCase().replaceAll("\\.csv", "");
    long timestamp = Instant.now().getEpochSecond();
    return format("%s-%d.csv", name, timestamp);
  }

  private Optional<File> move(File file, File target) {
    boolean success = file.renameTo(target);
    if (success) {
      return Optional.of(target);
    } else {
      log.info("Couldn't move csv '{}' to: {}", file.getAbsolutePath(), target.getAbsolutePath());
      return Optional.empty();
    }
  }

  private void run(String type, File file) {
    CsvResult result = load(type, file);

    if (result.isSuccess()) {
      move(type, file, SUCCESS);
    } else {
      move(type, file, FAIL);
      addErrors(type, file, result);
    }
  }

  private CsvResult load(String type, File file) {
    try (InputStream is = new FileInputStream(file)) {
      return service.load(is, type);
    } catch (IOException ioe) {
      log.error("Could not process CSV file", ioe);
      return CsvResult.error(ioe);
    }
  }

  private void move(String type, File file, String target) {
    File renamed = new File(properties.getDirectory(type, target), file.getName());
    move(file, renamed);
  }

  private void addErrors(String type, File file, CsvResult result) {
    String name = file.getName() + ".log";
    File target = new File(properties.getDirectory(type, LOGS), name);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(target))) {
      for (CsvResult.CsvError error : result.getErrors()) {
        String message = format("%d: %s\n", error.getRowNumber(), error.getMessage());
        writer.write(message);
      }
    } catch (IOException ioe) {
      log.info("Could not write log", ioe);
    }
  }

}
