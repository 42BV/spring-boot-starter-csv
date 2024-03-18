package nl._42.boot.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import nl._42.boot.csv.document.CsvDocument;

@SpringBootTest
class CsvServiceTest {

    @Autowired
    private CsvService csvService;

    @BeforeEach
    public void clear() {
        Results.clear();
    }

    @Test
    void success_orders() throws IOException {
        try (InputStream is = new ClassPathResource("csv/orders.csv").getInputStream()) {
            CsvResult result = csvService.load(is, OrderCsvHandler.TYPE);

            assertEquals("", getErrors(result));
            assertEquals(1, result.getSuccess());
            assertEquals(0, result.getErrors().size());
        }

        List<Object> values = Results.values();
        assertEquals(1, values.size());

        OrderCsvRow value = (OrderCsvRow) values.get(0);
        assertEquals("jan", value.getPerson());
        assertEquals(LocalDate.of(2019, 12, 15), value.getDate());
        assertEquals(new BigDecimal("42"), value.getCost());
    }

    @Test
    void success_persons() throws IOException {
        try (InputStream is = new ClassPathResource("csv/persons.csv").getInputStream()) {
            CsvResult result = csvService.load(is, PersonCsvHandler.TYPE);

            assertEquals("", getErrors(result));
            assertEquals(1, result.getSuccess());
            assertEquals(0, result.getErrors().size());
        }

        List<Object> values = Results.values();
        assertEquals(1, values.size());

        PersonCsvRow value = (PersonCsvRow) values.get(0);
        assertEquals("Irénëe", value.getFirstName());
        assertEquals("de Tester", value.getLastName());
        assertEquals("irenee@test.nl", value.getEmail());
        assertEquals(true, value.isActive());
        assertEquals(28, value.getAge());
        assertEquals("1234AB", value.getPostalCode());
        assertEquals("Mijn omschrijving", value.getDescriptions().get("NL"));
        assertEquals("My description", value.getDescriptions().get("EN"));
        assertEquals("female", value.getTags().get("gender"));
    }

    @Test
    void success_encoding_persons() throws IOException {
        try (InputStream is = new ClassPathResource("csv/person-iso.csv").getInputStream()) {
            CsvResult result = csvService.load(is, PersonCsvHandler.TYPE);

            assertEquals("", getErrors(result));
            assertEquals(1, result.getSuccess());
            assertEquals(0, result.getErrors().size());
        }

        List<Object> values = Results.values();
        assertEquals(1, values.size());

        PersonCsvRow value = (PersonCsvRow) values.get(0);
        assertEquals("Irénëe", value.getFirstName());
    }

    @Test
    @Disabled("Works in IDE but not on command line")
    void success_invalid_characters() throws IOException {
        try (InputStream is = new ClassPathResource("csv/persons.csv").getInputStream()) {
            try (Scanner scanner = new Scanner(is).useDelimiter("\\A")) {
                String content = '\uFEFF' + scanner.next();
                ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes());

                CsvResult result = csvService.load(bis, PersonCsvHandler.TYPE);

                assertEquals("", getErrors(result));
                assertEquals(1, result.getSuccess());
                assertEquals(0, result.getErrors().size());
            }
        }

        List<Object> values = Results.values();
        assertEquals(1, values.size());
    }

    @Test
    void fail_converter() throws IOException {
        try (InputStream is = new ClassPathResource("csv/persons-fail.csv").getInputStream()) {
            CsvResult result = csvService.load(is, PersonCsvHandler.TYPE);

            assertEquals(0, result.getSuccess());
            assertEquals(2, result.getErrors().size());

            assertEquals("Could not map column 'age' at index 5: For input string: \"invalid\"", result.getErrors().get(0).getMessage());
            assertEquals("Could not map column 'age' at index 5: For input string: \"other\"", result.getErrors().get(1).getMessage());
        }
    }

    @Test
    void fail_separator() throws IOException {
        try (InputStream is = new ClassPathResource("csv/persons-semicolon.csv").getInputStream()) {
            CsvResult result = csvService.load(is, PersonCsvHandler.TYPE);

            assertEquals(0, result.getSuccess());
            assertEquals(1, result.getErrors().size());

            assertEquals(
                    "Expected header 'first_name' at index 1 but got 'first_name;last_name;email;active;age;postal_code'",
                    getErrors(result)
            );
        }
    }

    @Test
    void fail_invalid_columns() throws IOException {
        try (InputStream is = new ClassPathResource("csv/persons-invalid-columns.csv").getInputStream()) {
            CsvResult result = csvService.load(is, PersonCsvHandler.TYPE);

            assertEquals(0, result.getSuccess());
            assertEquals(1, result.getErrors().size());

            CsvResult.CsvError error = result.getErrors().get(0);
            assertEquals("The expected number of columns is 6, whereas it was 5", error.getMessage());
            assertEquals(1, error.getRowNumber());
        }
    }

    private String getErrors(CsvResult result) {
        return result.getErrors().stream()
                .map(CsvResult.CsvError::getMessage)
                .collect(Collectors.joining(", "));
    }

    @Test
    void describe_shouldSucceed() {
        CsvDocument document = csvService.getDocument(PersonCsvHandler.TYPE);
        Assertions.assertNotNull(document);
    }

    @Test
    void validate_shouldSucceed_whenDefined() {
        CsvResult result = csvService.validate(PersonCsvHandler.TYPE);
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals(1, result.getSuccess());
    }

    @Test
    void validate_shouldSucceed_whenUndefined() {
        CsvResult result = csvService.validate(OrderCsvHandler.TYPE);
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals(0, result.getSuccess());
    }

    @Test
    void validate_shouldFail_whenInvalid() {
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () ->
                csvService.validate(InvalidCsvHandler.TYPE)
        );

        Assertions.assertEquals("CSV example failed: (0) Expected header 'text' at index 1 but got 'unknown'", exception.getMessage());
    }

}
