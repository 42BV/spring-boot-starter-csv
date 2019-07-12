package nl._42.boot.csv;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CsvTest {

    @Autowired
    private CsvService csvService;

    @Before
    public void clear() {
        Results.clear();
    }

    @Test
    public void success_orders() throws IOException {
        try (InputStream is = new ClassPathResource("orders.csv").getInputStream()) {
            CsvResult result = csvService.load(is, OrderCsvHandler.TYPE);

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
    public void success_persons() throws IOException {
        try (InputStream is = new ClassPathResource("persons.csv").getInputStream()) {
            CsvResult result = csvService.load(is, PersonCsvHandler.TYPE);

            assertEquals(1, result.getSuccess());
            assertEquals(0, result.getErrors().size());
        }

        List<Object> values = Results.values();
        assertEquals(1, values.size());

        PersonCsvRow value = (PersonCsvRow) values.get(0);
        assertEquals("Jan", value.getFirstName());
        assertEquals("de Boer", value.getLastName());
        assertEquals("jan@test.nl", value.getEmail());
        assertEquals(28, value.getAge());
        assertEquals("1234AB", value.getPostalCode());
        assertEquals("Mijn omschrijving", value.getDescriptions().get("NL"));
        assertEquals("My description", value.getDescriptions().get("EN"));
        assertEquals("male", value.getTags().get("gender"));
    }

    @Test
    public void fail_converter() throws IOException {
        try (InputStream is = new ClassPathResource("persons-fail.csv").getInputStream()) {
            CsvResult result = csvService.load(is, PersonCsvHandler.TYPE);

            assertEquals(0, result.getSuccess());
            assertEquals(2, result.getErrors().size());

            assertEquals("Could not map column 'age' at index 4: For input string: \"invalid\"", result.getErrors().get(0).getMessage());
            assertEquals("Could not map column 'age' at index 4: For input string: \"other\"", result.getErrors().get(1).getMessage());
        }
    }

    @Test
    public void fail_separator() throws IOException {
        try (InputStream is = new ClassPathResource("persons-semicolon.csv").getInputStream()) {
            CsvResult result = csvService.load(is, PersonCsvHandler.TYPE);

            assertEquals(0, result.getSuccess());
            assertEquals(1, result.getErrors().size());

            assertEquals(
                "Expected header 'first_name' at index 1 but got 'first_name;last_name;email;age;postal_code'",
                result.getErrors().get(0).getMessage()
            );
        }
    }

}
