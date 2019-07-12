package nl._42.boot.csv;

import lombok.AllArgsConstructor;

import java.util.Collection;

@AllArgsConstructor
public class CsvParameters {

    public final Collection<String> types;
    public final char separator;
    public final char quote;

}
