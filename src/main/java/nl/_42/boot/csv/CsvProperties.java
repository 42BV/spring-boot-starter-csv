package nl._42.boot.csv;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "csv")
public class CsvProperties {

    private List<String> types = Collections.emptyList();

    private char separator = ',';
    private char quote = '"';

}
