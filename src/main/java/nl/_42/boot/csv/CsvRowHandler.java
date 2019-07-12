package nl._42.boot.csv;

import org.csveed.api.Row;

public interface CsvRowHandler extends CsvHandler<Row> {

  @Override
  default Class<Row> getBeanClass() {
    return Row.class;
  }

}
