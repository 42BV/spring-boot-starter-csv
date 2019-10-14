package nl._42.boot.csv;

import lombok.extern.slf4j.Slf4j;
import org.csveed.api.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PersonCsvHandler extends AbstractRowCsvHandler<PersonCsvRow> {

  public static final String TYPE = "persons";

  public PersonCsvHandler() {
    super(TYPE);
  }

  @Override
  protected CsvMapper<PersonCsvRow> mapperOf(Header header) {
    return
      CsvMapper
        .builder(PersonCsvRow::new, header)
        .add("first_name", (firstName, person) -> person.setFirstName(firstName))
        .add("last_name", (lastName, person) -> person.setLastName(lastName))
        .add("email", (email, person) -> person.setEmail(email))
        .add("age", (age, person) -> person.setAge(Integer.parseInt(age)))
        .add("postal_code", (postalCode, person) -> person.setPostalCode(postalCode))
        .addStartsWith("description-", (language) -> (value, person) -> person.getDescriptions().put(language, value))
        .addRemainder((name) -> (value, person) -> person.getTags().put(name, value))
        .build();
  }

  @Override
  protected void write(PersonCsvRow row) {
    Results.add(row);
  }

}
