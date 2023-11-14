package nl._42.boot.csv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCsvHandler extends AbstractCsvHandler<OrderCsvRow> {

    public static final String TYPE = "ORDERS";

    public OrderCsvHandler() {
        super(TYPE, OrderCsvRow.class);
    }

    @Override
    protected void write(OrderCsvRow row) {
        Results.add(row);
    }

}
