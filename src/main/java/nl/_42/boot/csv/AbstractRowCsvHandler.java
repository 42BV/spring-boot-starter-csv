package nl._42.boot.csv;

import lombok.Getter;
import org.csveed.api.CsvClient;
import org.csveed.api.Header;
import org.csveed.api.Row;

import java.util.function.Function;

@Getter
public abstract class AbstractRowCsvHandler<T> implements CsvHandler<Row> {

    private final String type;

    public AbstractRowCsvHandler(String type) {
        this.type = type;
    }

    @Override
    public final Class<Row> getBeanClass() {
        return Row.class;
    }

    @Override
    public CsvResult handle(CsvClient<Row> client) {
        Header header = client.readHeader();
        Function<Row, T> mapper = mapperOf(header);

        return new CsvTemplate().read(client::readRow, mapper::apply, this::write);
    }

    protected abstract Function<Row, T> mapperOf(Header header);

    protected abstract void write(T row);

}
