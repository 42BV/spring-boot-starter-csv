package nl._42.boot.csv;

import lombok.extern.slf4j.Slf4j;
import nl._42.boot.csv.document.CsvColumn;
import nl._42.boot.csv.document.CsvDocument;
import org.csveed.api.CsvClient;
import org.csveed.api.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InvalidCsvHandler implements CsvHandler<InvalidCsvRow> {

    public static final String TYPE = "INVALID";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Class<InvalidCsvRow> getBeanClass() {
        return InvalidCsvRow.class;
    }

    @Override
    public CsvResult handle(CsvClient<InvalidCsvRow> client) {
        Header header = client.readHeader();
        CsvMapper<InvalidCsvRow> mapper = mapperOf(header);
        return new CsvTemplate().read(client::readRow, mapper::map, Results::add);
    }

    private CsvMapper<InvalidCsvRow> mapperOf(Header header) {
        return CsvMapper
            .builder(InvalidCsvRow::new, header)
            .add("text", (text, row) -> row.setText(text))
            .build();
    }

    @Override
    public void describe(CsvDocument document) {
        document.addColumn(new CsvColumn("unknown").example("value"));
    }

}
