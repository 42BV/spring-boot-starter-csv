package nl._42.boot.csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Results {

  private static final List<Object> VALUES = new ArrayList<>();

  static void clear() {
    VALUES.clear();
  }

  static void add(Object value) {
    VALUES.add(value);
  }

  static List<Object> values() {
    return Collections.unmodifiableList(VALUES);
  }

}
