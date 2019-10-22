package nl._42.boot.csv;

import org.apache.commons.lang3.StringUtils;
import org.csveed.api.Header;
import org.csveed.api.Row;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * Converts CSV rows with dynamic (header based) mappings into the target format.
 * Mappings can be partially static, expecting a certain header at a certain index,
 * or dynamic based on a prefix or variable tail.
 * <br>
 * Only use this mapper when static mapping is not possible.
 * @param <T> the target type
 */
public class CsvMapper<T> implements Function<Row, T> {

    private final Map<Integer, BiConsumer<String, T>> columns = new HashMap<>();
    private final Supplier<T> constructor;

    private CsvMapper(Supplier<T> constructor) {
        this.constructor = constructor;
    }

    public static <T> Builder<T> builder(Supplier<T> constructor, Header header) {
        return new Builder<>(constructor, header);
    }

    public T map(Row row) {
        T details = constructor.get();
        for (int index = 1; index <= row.size(); index++) {
            String value = row.get(index);
            BiConsumer<String, T> column = columns.get(index);
            if (column != null && StringUtils.isNotBlank(value)) {
                column.accept(value, details);
            }
        }
        return details;
    }

    @Override
    public T apply(Row row) {
        return map(row);
    }

    public static final class Builder<T> {

        private final Map<Integer, BiConsumer<String, T>> columns = new HashMap<>();
        private final Supplier<T> constructor;

        private final Header header;

        private Function<String, String> formatter = (value) -> value.replaceAll("\\P{Print}", "");
        private int index = 1;

        private Builder(Supplier<T> constructor, Header header) {
            this.constructor = constructor;
            this.header = header;
        }

        /**
         * Expected the next column to have a certain name.
         * @param expected the expected name
         * @param consumer the handler
         * @return this builder
         */
        public Builder<T> add(String expected, BiConsumer<String, T> consumer) {
            checkHeader(expected);
            return this.add(consumer);
        }

        private void checkHeader(String expected) {
            String name = getName(index);
            if (!Objects.equals(expected, name)) {
                throw new IllegalArgumentException(
                    format("Expected header '%s' at index %d but got '%s'", expected, index, name)
                );
            }
        }

        private String getName(int index) {
            if (index > header.size()) {
                return "";
            }

            String name = header.getName(index);
            if (name != null) {
                name = formatter.apply(name);
            }
            return name;
        }

        private Builder<T> add(BiConsumer<String, T> consumer) {
            BiConsumer<String, T> wrapped = wrap(consumer, index);
            columns.put(index++, wrapped);
            return this;
        }

        // Enhance with column information
        private BiConsumer<String, T> wrap(BiConsumer<String, T> consumer, int index) {
            String name = getName(index);
            return (key, value) -> {
                try {
                    consumer.accept(key, value);
                } catch (RuntimeException rte) {
                    throw new IllegalStateException(
                        format("Could not map column '%s' at index %d: %s", name, index, rte.getMessage()),
                        rte
                    );
                }
            };
        }

        /**
         * Register all next columns that start with the prefix, if any.
         * @param prefix the prefix
         * @param mapper the function producing a handler for each column
         * @return this builder
         */
        public Builder<T> addStartsWith(String prefix, Function<String, BiConsumer<String, T>> mapper) {
            String name = getName(index);
            while (StringUtils.startsWith(name, prefix)) {
                String suffix = StringUtils.substringAfter(name, prefix);
                add(mapper.apply(suffix));
                name = getName(index);
            }
            return this;
        }

        /**
         * Register all remaining columns, if any.
         * @param mapper the function producing a handler for each column
         * @return this builder
         */
        public Builder<T> addRemainder(Function<String, BiConsumer<String, T>> mapper) {
            while (index <= header.size()) {
                String name = getName(index);
                add(mapper.apply(name));
            }
            return this;
        }

        /**
         * Use a different formatter for parsing headers and values.
         * @param formatter the formatter
         * @return this builder
         */
        public Builder<T> formatter(Function<String, String> formatter) {
            Objects.requireNonNull(formatter, "Formatter cannot be null");
            this.formatter = formatter;
            return this;
        }

        public CsvMapper<T> build() {
            CsvMapper<T> mapper = new CsvMapper<>(constructor);
            mapper.columns.putAll(columns);
            return mapper;
        }

    }

}
