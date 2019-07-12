package nl._42.boot.csv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Register all CSV infrastructure.
 */
@Slf4j
@Configuration
@ComponentScan(basePackageClasses = CsvAutoConfiguration.class)
@ConditionalOnProperty(name = "csv.enabled", havingValue = "true", matchIfMissing = true)
public class CsvAutoConfiguration {

}
