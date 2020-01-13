package nl._42.boot.csv;

import lombok.extern.slf4j.Slf4j;
import org.csveed.api.CsvClient;
import org.csveed.api.CsvClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Capable of processing CSV files.
 */
@Slf4j
@Service
public class CsvService {

    private final Map<String, CsvHandler<?>> handlers = new HashMap<>();
    private final CsvProperties properties;

    public CsvService(CsvProperties properties) {
        this.properties = properties;
    }

    /**
     * Retrieve all known CSV types and properties.
     * @return the parameters
     */
    public CsvParameters getParameters() {
        Collection<String> types = getTypes();
        return new CsvParameters(types, properties.getSeparator(), properties.getQuote());
    }

    /**
     * Retrieve all CSV types.
     * @return the types
     */
    public Collection<String> getTypes() {
        List<String> types = properties.getTypes();

        if (types.isEmpty()) {
            return new TreeSet<>(handlers.keySet()); // Return a sorted list of known types
        }

        return types.stream()
                    .filter(handlers::containsKey)
                    .collect(Collectors.toList());
    }

    /**
     * Process a CSV, based on default properties.
     * @param is the CSV content
     * @param type the CSV type
     * @return the result
     */
    public CsvResult load(InputStream is, String type) {
        return load(is, type, properties);
    }

    /**
     * Process a CSV.
     * @param is the CSV content
     * @param type the CSV type
     * @param properties the properties
     * @return the result
     */
    public CsvResult load(InputStream is, String type, CsvProperties properties) {
        CsvHandler handler = getHandler(type);
        return handle(is, handler, properties);
    }

    private CsvHandler<?> getHandler(String type) {
        CsvHandler<?> handler = handlers.get(type);
        Objects.requireNonNull(handler, "Unsupported CSV type: " + type);
        return handler;
    }

    private <T> CsvResult handle(InputStream is, CsvHandler<T> handler, CsvProperties properties) {
        CsvClient<T> client = buildCsvClient(is, handler.getBeanClass(), properties);

        try {
            return handler.handle(client);
        } catch (RuntimeException e) {
            log.error("Could not handle CSV file", e);
            return CsvResult.error(e);
        }
    }

    private <T> CsvClient<T> buildCsvClient(InputStream is, Class<T> beanClass, CsvProperties properties) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        CsvClient<T> csvReader = new CsvClientImpl<>(reader, beanClass);
        csvReader.setQuote(properties.getQuote());
        csvReader.setSeparator(properties.getSeparator());
        return csvReader;
    }

    /**
     * Register all known handlers.
     * @param handlers the handlers, if any
     */
    @Autowired(required = false)
    public void setHandlers(Collection<CsvHandler> handlers) {
        handlers.forEach(handler -> this.handlers.put(handler.getType(), handler));
    }

}
