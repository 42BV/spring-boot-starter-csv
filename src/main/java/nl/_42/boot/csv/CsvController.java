package nl._42.boot.csv;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.csv.document.CsvDocument;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Provide a '/csv' endpoint, capable of processing CSV files.
 */
@Slf4j
@Schema
@RestController
@Tag(name = "CSV")
@RequestMapping("/csv")
@AllArgsConstructor
public class CsvController {

    private final CsvService csvService;

    @GetMapping
    @Operation(operationId = "getParameters", description = "Get CSV configuration")
    public CsvParameters getParameters() {
        return csvService.getParameters();
    }

    @GetMapping("/document")
    @Operation(operationId = "getDocument", description = "Get CSV endpoint documentation")
    public CsvDocument getDocument(
        @Parameter(name = "type", description = "CSV type", example = "user")
        @RequestParam("type") String type
    ) {
        return csvService.getDocument(type);
    }

    @PostMapping
    @Operation(operationId = "uploadFile", description = "Upload a CSV file")
    public CsvResult uploadFile(
        @Parameter(name = "file", description = "CSV file to upload")
        @RequestParam("file") MultipartFile file,
        @Parameter(name = "type", description = "CSV type", example = "user")
        @RequestParam("type") String type,
        CsvProperties properties
    ) {
        return upload(file::getInputStream, type, properties);
    }

    @PostMapping("/raw")
    @Operation(operationId = "uploadRaw", description = "Upload a CSV in raw text")
    public CsvResult uploadRaw(
        @Parameter(name = "content", description = "CSV content to upload", example = "name,email\nuser,user@organisation.com")
        @RequestBody String content,
        @Parameter(name = "type", description = "CSV type", example = "user")
        @RequestParam("type") String type,
        CsvProperties properties
    ) {
        return upload(() -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), type, properties);
    }

    private CsvResult upload(InputProvider provider, String type, CsvProperties properties) {
        try (InputStream is = provider.getInputStream()) {
            return csvService.load(is, type, properties);
        } catch (RuntimeException | IOException e) {
            log.error("Could not load CSV file", e);
            return CsvResult.error(e);
        }
    }

    @FunctionalInterface
    private interface InputProvider {

        InputStream getInputStream() throws IOException;

    }

}
