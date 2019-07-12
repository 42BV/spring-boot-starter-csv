package nl._42.boot.csv;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CsvResult {

    @Getter
    private int success = 0;

    @Getter
    private List<CsvError> errors = new ArrayList<>();

    public CsvResult success() {
        success++;
        return this;
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
            return messages.stream().collect(Collectors.joining(" "));
        }

    }

}
