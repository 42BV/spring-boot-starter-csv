package nl._42.boot.csv.document;

import lombok.Getter;
import lombok.Setter;
import nl._42.boot.csv.CsvProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CsvDocument {

    private final CsvProperties properties;

    @Getter
    private final String type;

    private List<CsvColumn> columns = new ArrayList<>();

    @Getter
    @Setter
    private String description;

    public CsvDocument(CsvProperties properties, String type) {
        this.properties = properties;
        this.type = type;
    }

    public List<CsvColumn> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public void addColumn(CsvColumn column) {
        Objects.requireNonNull(column, "Column cannot be null");
        columns.add(column);
    }

    public String getContent() {
        CsvBuilder builder = new CsvBuilder(properties);
        columns.forEach(column -> builder.append(column.getName()));
        builder.next();
        columns.forEach(column -> builder.append(column.getExample()));
        return builder.build();
    }

}
