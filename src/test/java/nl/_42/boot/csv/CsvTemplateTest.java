package nl._42.boot.csv;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.CsvParsingException;

import nl._42.boot.csv.CsvResult.CsvError;

class CsvTemplateTest {

    private CsvTemplate csvTemplate = new CsvTemplate();
    private List<String> rows = new ArrayList<>();
    private List<String> processed = new ArrayList<>();

    private Supplier<String> supplier;
    private Consumer<String> consumer;

    @BeforeEach
    public void setUp() {
        supplier = () -> {
            if (!rows.isEmpty()) {
                return rows.remove(0);
            }
            return null;
        };

        consumer = (value) -> {
            processed.add(value);
        };
    }

    @Test
    void read_shouldSucceed() {
        rows.add("test");

        CsvResult result = csvTemplate.read(supplier, consumer);
        assertThat(result.getSuccess(), is(1));
        assertThat(result.getErrors(), is(empty()));
        assertThat(result.getRows(), is(1));

        assertThat(processed, hasSize(1));
        assertThat(processed.get(0), is("test"));
    }

    @Test
    void read_shouldSucceed_whenRowsEmpty() {
        CsvResult result = csvTemplate.read(supplier, consumer);

        assertThat(result.getSuccess(), is(0));
        assertThat(result.getErrors(), is(empty()));
        assertThat(result.getRows(), is(0));

        assertThat(processed, hasSize(0));
    }

    @Test
    void read_shouldFail_whenRowSupplierFails() {
        supplier = () -> {
            throw new CsvParsingException("Invalid csv");
        };

        CsvResult result = csvTemplate.read(supplier, consumer);
        assertThat(result.getSuccess(), is(0));
        assertThat(result.getErrors(), hasSize(1));
        assertThat(result.getRows(), is(1));

        CsvError error = result.getErrors().get(0);
        assertThat(error.getMessage(), is("Invalid csv"));
        assertThat(error.getRowNumber(), is(1));

        assertThat(processed, is(empty()));
    }

    @Test
    void read_shouldFail_whenConsumerFails() {
        rows.add("test");

        consumer = (value) -> {
            throw new RuntimeException("Processing failed");
        };

        CsvResult result = csvTemplate.read(supplier, consumer);
        assertThat(result.getSuccess(), is(0));
        assertThat(result.getErrors(), hasSize(1));
        assertThat(result.getRows(), is(1));

        CsvError error = result.getErrors().get(0);
        assertThat(error.getMessage(), is("Processing failed"));
        assertThat(error.getRowNumber(), is(1));
    }

}
