package nl._42.boot.csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Results {

  private static final List<Object> VALUES = new ArrayList<>();

  public static void clear() {
    VALUES.clear();
  }

  public static void add(Object value) {
    VALUES.add(value);
  }

  public static List<Object> values() {
    return Collections.unmodifiableList(VALUES);
  }

}
