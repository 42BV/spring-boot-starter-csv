package nl._42.boot.csv.document;

import lombok.AllArgsConstructor;
import nl._42.boot.csv.CsvProperties;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicBoolean;

@AllArgsConstructor
class CsvBuilder {

    private final StringBuilder content = new StringBuilder();
    private final CsvProperties properties;
    private final AtomicBoolean quote = new AtomicBoolean(false);

    CsvBuilder append(String value) {
        if (quote.get()) {
            content.append(properties.getSeparator());
        } else {
            quote.set(true);
        }

        String text = StringUtils.defaultString(value, "");
        content.append(properties.getQuote()).append(text).append(properties.getQuote());

        return this;
    }

    CsvBuilder next() {
        quote.set(false);
        content.append('\n');
        return this;
    }

    String build() {
        return content.toString();
    }

}
