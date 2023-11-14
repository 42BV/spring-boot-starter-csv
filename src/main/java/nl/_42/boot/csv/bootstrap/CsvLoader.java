package nl._42.boot.csv.bootstrap;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.csv.CsvResult;
import nl._42.boot.csv.CsvService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Service for loading a bulk of CSV files from a directory.
 */
@Slf4j
@Component
@AllArgsConstructor
public class CsvLoader {

    private final CsvService csvService;

    /**
     * Load all CSV files inside a classpath location. During execution
     * the loader will scan for files matching the defined CSV types, e.g.
     * 'PERSON' expects a 'person.csv'.
     * Whenever a matching file is found it is loaded automatically.
     *
     * @param location the base directory on the classpath
     */
    public void loadAll(String location) {
        ClassPathResource resource = new ClassPathResource(location);
        if (resource.exists()) {
            csvService.getTypes().forEach(type -> load(type, resource));
        }
    }

    private void load(String type, ClassPathResource resource) {
        final String name = type.toLowerCase() + ".csv";

        try {
            File directory = resource.getFile();
            File file = new File(directory, name);
            if (file.exists()) {
                load(file, type);
            }
        } catch (IOException e) {
            log.error("Could not load CSV " + name, e);
            throw new IllegalStateException(e);
        }
    }

    private void load(File file, String type) throws IOException {
        log.info("Loading CSV from: {}", file.getAbsolutePath());

        try (InputStream is = new FileInputStream(file)) {
            CsvResult result = csvService.load(is, type);
            result.getErrors().forEach(error -> print(file.getAbsolutePath(), error));
        }
    }

    private void print(String location, CsvResult.CsvError error) {
        log.error("Could not load CSV '{}' line {}: {}", location, error.getRowNumber(), error.getMessage());
    }

}
