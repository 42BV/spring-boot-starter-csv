package nl._42.boot.csv;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class CsvTemplate {

    private final CsvResult result;

    public CsvTemplate() {
        this(new CsvResult());
    }

    public <T> CsvResult read(Supplier<T> supplier, Consumer<T> consumer) {
        return read(supplier, (value) -> value, consumer);
    }

    public <T, R> CsvResult read(Supplier<T> supplier, Function<T, R> transformer, Consumer<R> consumer) {
        int rowNumber = 1;
        while (next(supplier, transformer, consumer, rowNumber)) {
            rowNumber++;
        }
        return result;
    }

    private <T, R> boolean next(Supplier<T> supplier, Function<T, R> transformer, Consumer<R> consumer, int rowNumber) {
        boolean found = false;

        T row = get(supplier, rowNumber);
        if (row != null) {
            try {
                R value = transformer.apply(row);
                consumer.accept(value);
                result.success();
            } catch (RuntimeException rte) {
                handleException(rowNumber, rte);
            } finally {
                found = true;
            }
        }

        return found;
    }

    private <T> T get(Supplier<T> supplier, int rowNumber) {
        T row = null;
        try {
            row = supplier.get();
        } catch (RuntimeException rte) {
            handleException(rowNumber, rte);
        }
        return row;
    }

    private void handleException(int rowNumber, RuntimeException rte) {
        log.error("Could not handle CSV row {}", rowNumber, rte);
        result.error(rowNumber, rte.getMessage());
    }
    
}
