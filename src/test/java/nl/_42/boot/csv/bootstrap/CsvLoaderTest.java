package nl._42.boot.csv.bootstrap;

import nl._42.boot.csv.OrderCsvRow;
import nl._42.boot.csv.PersonCsvRow;
import nl._42.boot.csv.Results;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CsvLoaderTest {

  @Autowired
  private CsvLoader loader;

  @BeforeEach
  public void clear() {
    Results.clear();
  }

  @Test
  public void loadAll_shouldSucceed() {
    loader.loadAll("csv");

    List<Object> values = Results.values();
    assertEquals(2, values.size());
    assertEquals(PersonCsvRow.class, values.get(0).getClass());
    assertEquals(OrderCsvRow.class, values.get(1).getClass());
  }

  @Test
  public void loadAll_shouldSucceed_whenNotExisting() {
    loader.loadAll("unknown");

    List<Object> values = Results.values();
    assertEquals(0, values.size());
  }

}
