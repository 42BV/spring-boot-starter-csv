package nl._42.boot.csv;

import org.csveed.api.CsvClient;

public interface CsvHandler<T> {

    String getType();

    Class<T> getBeanClass();

    CsvResult handle(CsvClient<T> client);

}
