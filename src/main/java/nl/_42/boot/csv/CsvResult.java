package nl._42.boot.csv;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CsvResult {

    @Getter
    private int success = 0;

    @Getter
    private List<CsvError> errors = new ArrayList<>();

    public CsvResult success() {
        success++;
        return this;
    }

    public static CsvResult error(Throwable throwable) {
        CsvResult result = new CsvResult();
        result.error(0, throwable.getMessage());
        return result;
    }

    public CsvResult error(int rowNumber, String message) {
        CsvError error = errors.stream().filter(e -> e.rowNumber == rowNumber)
                .findFirst().orElseGet(() -> addError(rowNumber));

        error.addMessage(message);
        return this;
    }

    private CsvError addError(int rowNumber) {
        CsvError error = new CsvError(rowNumber);
        errors.add(error);
        return error;
    }

    public boolean isSuccess() {
        return errors.isEmpty();
    }

    public int getRows() {
        return success + errors.size();
    }

    public class CsvError {

        public final Set<String> messages = new HashSet<>();

        @Getter
        public final int rowNumber;

        CsvError(int rowNumber) {
            this.rowNumber = rowNumber;
        }

        private void addMessage(String message) {
            this.messages.add(message);
        }

        public String getMessage() {
            return String.join(" ", messages);
        }

        @Override
        public String toString() {
            return String.format("(%d) %s", rowNumber, this.getMessage());
        }

    }

}
