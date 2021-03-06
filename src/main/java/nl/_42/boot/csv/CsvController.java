package nl._42.boot.csv;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Provide a '/csv' endpoint, capable of processing CSV files.
 */
@Slf4j
@RestController
@RequestMapping("/csv")
@AllArgsConstructor
public class CsvController {

    private final CsvService csvService;

    @GetMapping
    public CsvParameters getParameters() {
        return csvService.getParameters();
    }

    @PostMapping
    public CsvResult load(@RequestParam("file") MultipartFile file, @RequestParam("type") String type, CsvProperties properties) {
        try (InputStream is = file.getInputStream()) {
            return csvService.load(is, type, properties);
        } catch (RuntimeException | IOException e) {
            log.error("Could not load CSV file", e);
            return CsvResult.error(e);
        }
    }

}
