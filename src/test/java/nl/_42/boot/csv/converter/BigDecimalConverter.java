package nl._42.boot.csv.converter;

import org.csveed.bean.conversion.AbstractConverter;

import java.math.BigDecimal;

public class BigDecimalConverter extends AbstractConverter<BigDecimal> {

    public BigDecimalConverter() {
        super(BigDecimal.class);
    }

    @Override
    public BigDecimal fromString(String text) {
        return new BigDecimal(text);
    }

    @Override
    public String toString(BigDecimal value) {
        return value.toString();
    }

}
