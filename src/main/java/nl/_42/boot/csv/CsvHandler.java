package nl._42.boot.csv;

import nl._42.boot.csv.document.CsvDocument;
import org.csveed.api.CsvClient;

/**
 * Handler of a specific CSV type.
 *
 * @param <T> the bean class
 */
public interface CsvHandler<T> {

    /**
     * Retrieve the CSV type name.
     *
     * @return the type name
     */
    String getType();

    /**
     * Retrieve the target bean class.
     *
     * @return the target bean class
     */
    Class<T> getBeanClass();

    /**
     * Process a CSV client, containing the data.
     *
     * @param client the CSV client
     * @return the CSV result
     */
    CsvResult handle(CsvClient<T> client);

    /**
     * Describe this CSV type, can be shown for usage.
     *
     * @param document the CSV description for this type
     */
    default void describe(CsvDocument document) {
    }

}
