package nl._42.boot.csv.file;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "csv.file")
public class CsvFileProperties {

    private File directory;
    private List<String> order = Collections.emptyList();
    private boolean runOnStartup;

    File getDirectory(String type, String directory) {
        String path = format("%s/%s", type, directory);

        File file = new File(this.directory, path);
        file.mkdirs();
        return file;
    }

}
