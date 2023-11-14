package nl._42.boot.csv.file;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@ConditionalOnProperty("csv.file.directory")
class CsvFileStarter implements ApplicationListener<ApplicationReadyEvent> {

    private final CsvFileProperties properties;
    private final CsvFileService service;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (properties.isRunOnStartup()) {
            service.run();
        }
    }

}
