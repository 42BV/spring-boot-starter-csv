package nl._42.boot.csv;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class PersonCsvRow {

    private String firstName;
    private String lastName;
    private String email;
    private int age;
    private String postalCode;
    private boolean active;
    private Map<String, String> descriptions = new HashMap<>();
    private Map<String, String> tags = new HashMap<>();

}
