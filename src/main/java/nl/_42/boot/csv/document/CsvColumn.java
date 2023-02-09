package nl._42.boot.csv.document;

import lombok.Getter;

@Getter
public class CsvColumn {

    private static final String TEXT = "text";

    private final String name;
    private final String pattern;
    private String type = TEXT;
    private String description;
    private String example;
    private boolean required;

    public CsvColumn(String name) {
        this(name, name);
    }

    public CsvColumn(String name, String pattern) {
        this.name = name;
        this.pattern = pattern;
    }

    public CsvColumn type(String type) {
        this.type = type;
        return this;
    }

    public CsvColumn description(String description) {
        this.description = description;
        return this;
    }

    public CsvColumn example(String example) {
        this.example = example;
        return this;
    }

    public CsvColumn required(boolean required) {
        this.required = required;
        return this;
    }

    public CsvColumn required() {
        return this.required(true);
    }

}
