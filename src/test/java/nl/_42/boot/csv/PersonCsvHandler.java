package nl._42.boot.csv;

import lombok.extern.slf4j.Slf4j;
import nl._42.boot.csv.document.CsvColumn;
import nl._42.boot.csv.document.CsvDocument;
import org.csveed.api.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PersonCsvHandler extends AbstractRowCsvHandler<PersonCsvRow> {

    public static final String TYPE = "PERSONS";

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
                        .add("active", (active, person) -> person.setActive(Boolean.parseBoolean(active)))
                        .addIfPresent("unknown", (value, person) -> {
                        })
                        .addIfPresent("age", (age, person) -> person.setAge(Integer.parseInt(age)))
                        .add("postal_code", (postalCode, person) -> person.setPostalCode(postalCode))
                        .addStartsWith("description-", language -> (value, person) -> person.getDescriptions().put(language, value))
                        .addRemainder(name -> (value, person) -> person.getTags().put(name, value))
                        .build();
    }

    @Override
    protected void write(PersonCsvRow row) {
        Results.add(row);
    }

    @Override
    public void describe(CsvDocument document) {
        document.setDescription("Describe the persons known in this system");
        document.addColumn(new CsvColumn("first_name").description("The first name").example("Piet").required());
        document.addColumn(new CsvColumn("last_name").description("The last name").example("de Boer").required());
        document.addColumn(new CsvColumn("email").description("The email address").example("piet@42.nl").required());
        document.addColumn(new CsvColumn("active").description("If person is currently active").type("boolean").example("true").required());
        document.addColumn(new CsvColumn("unknown").description("An additional property").example("Test"));
        document.addColumn(new CsvColumn("age").description("The age").type("number").example("42"));
        document.addColumn(new CsvColumn("postal_code").description("The postal code").example("1234AB").required());
        document.addColumn(new CsvColumn("description-NL", "description-{language}").description("The i18n description").example("Piet is docent."));
        document.addColumn(new CsvColumn("gender", "{property}").description("The first name").example("male"));
    }

}
