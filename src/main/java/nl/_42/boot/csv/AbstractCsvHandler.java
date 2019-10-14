package nl._42.boot.csv;

import lombok.Getter;
import org.csveed.api.CsvClient;

import java.util.function.Supplier;

@Getter
public abstract class AbstractCsvHandler<T> implements CsvHandler<T> {

    private final String type;
    private final Class<T> beanClass;

    public AbstractCsvHandler(String type, Class<T> beanClass) {
        this.type = type;
        this.beanClass = beanClass;
    }

    @Override
    public CsvResult handle(CsvClient<T> client) {
        Supplier<T> reader = reader(client);
        return new CsvTemplate().read(reader, this::write);
    }

    protected Supplier<T> reader(CsvClient<T> client) {
        return client::readBean;
    }

    protected abstract void write(T row);

}
