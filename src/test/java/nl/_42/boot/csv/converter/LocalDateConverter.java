package nl._42.boot.csv.converter;

import org.csveed.bean.conversion.AbstractConverter;

import java.time.LocalDate;

public class LocalDateConverter extends AbstractConverter<LocalDate> {

  public LocalDateConverter() {
    super(LocalDate.class);
  }

  @Override
  public LocalDate fromString(String text) {
    return LocalDate.parse(text);
  }

  @Override
  public String toString(LocalDate value) {
    return value.toString();
  }

}
