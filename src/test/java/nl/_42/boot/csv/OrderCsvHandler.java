package nl._42.boot.csv;

import lombok.extern.slf4j.Slf4j;
import org.csveed.api.CsvClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCsvHandler implements CsvHandler<OrderCsvRow> {

  public static final String TYPE = "orders";

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public Class<OrderCsvRow> getBeanClass() {
    return OrderCsvRow.class;
  }

  @Override
  public CsvResult handle(CsvClient<OrderCsvRow> client) {
    return new CsvTemplate().read(client::readBean, Results::add);
  }

}
