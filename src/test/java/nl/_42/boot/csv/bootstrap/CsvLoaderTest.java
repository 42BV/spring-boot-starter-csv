package nl._42.boot.csv.bootstrap;

import nl._42.boot.csv.OrderCsvRow;
import nl._42.boot.csv.PersonCsvRow;
import nl._42.boot.csv.Results;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CsvLoaderTest {

  @Autowired
  private CsvLoader loader;

  @Before
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
