package nl._42.boot.csv.file;

import com.google.common.io.Files;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CsvFileTest {

    @Autowired
    private CsvFileService service;

    @Autowired
    private CsvFileProperties properties;

    @Test
    @Disabled
    public void run_success() throws IOException {
        File file = new File("src/test/resources/csv/persons.csv");
        File upload = properties.getDirectory("persons", CsvFileService.UPLOAD);
        Files.copy(file, new File(upload, "persons.csv"));

        File success = properties.getDirectory("persons", CsvFileService.SUCCESS);
        File fail = properties.getDirectory("persons", CsvFileService.FAIL);

        int successes = success.listFiles().length;
        int fails = fail.listFiles().length;

        service.run();

        assertEquals(1, success.listFiles().length - successes);
        assertEquals(0, fail.listFiles().length - fails);
    }

    @Test
    @Disabled
    public void run_fail() throws IOException {
        File file = new File("src/test/resources/csv/persons-fail.csv");
        File upload = properties.getDirectory("persons", CsvFileService.UPLOAD);
        Files.copy(file, new File(upload, "persons-fail.csv"));

        File success = properties.getDirectory("persons", CsvFileService.SUCCESS);
        File fail = properties.getDirectory("persons", CsvFileService.FAIL);
        File log = properties.getDirectory("persons", CsvFileService.LOGS);

        int successes = success.listFiles().length;
        int fails = fail.listFiles().length;
        int logs = fail.listFiles().length;

        service.run();

        assertEquals(0, success.listFiles().length - successes);
        assertEquals(1, fail.listFiles().length - fails);
        assertEquals(1, log.listFiles().length - logs);
    }

}
