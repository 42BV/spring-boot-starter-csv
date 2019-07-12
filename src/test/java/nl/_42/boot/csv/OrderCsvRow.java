package nl._42.boot.csv;

import lombok.Getter;
import lombok.Setter;
import nl._42.boot.csv.converter.BigDecimalConverter;
import nl._42.boot.csv.converter.LocalDateConverter;
import org.csveed.annotations.CsvConverter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class OrderCsvRow {

  private String person;

  @CsvConverter(converter = LocalDateConverter.class)
  private LocalDate date;

  @CsvConverter(converter = BigDecimalConverter.class)
  private BigDecimal cost;

}
