package nl._42.boot.csv;

import lombok.extern.slf4j.Slf4j;
import org.csveed.api.CsvClient;
import org.csveed.api.Header;
import org.csveed.api.Row;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PersonCsvHandler implements CsvHandler<Row> {

  public static final String TYPE = "persons";

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public Class<Row> getBeanClass() {
    return Row.class;
  }

  @Override
  public CsvResult handle(CsvClient<Row> client) {
    CsvMapper<PersonCsvRow> mapper = mapperOf(client.readHeader());
    return new CsvTemplate().read(client::readRow, mapper::map, Results::add);
  }

  private CsvMapper<PersonCsvRow> mapperOf(Header header) {
    return CsvMapper.builder(PersonCsvRow::new, header)
            .add("first_name", (firstName, person) -> person.setFirstName(firstName))
            .add("last_name", (lastName, person) -> person.setLastName(lastName))
            .add("email", (email, person) -> person.setEmail(email))
            .add("age", (age, person) -> person.setAge(Integer.parseInt(age)))
            .add("postal_code", (postalCode, person) -> person.setPostalCode(postalCode))
            .addStartsWith("description-", (language) -> (value, person) -> person.getDescriptions().put(language, value))
            .addRemainder((name) -> (value, person) -> person.getTags().put(name, value))
            .build();
  }

}
