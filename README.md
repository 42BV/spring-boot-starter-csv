# Spring Boot Starter CSV

Registers a `CsvService` and `/csv` endpoint, capable of importing CSV files.

## Types

Consumers register implementations of the `CsvHandler` interface per type of file:

```java
@Slf4j
@Component
public class OrderCsvHandler implements CsvHandler<OrderCsvRow> {

  public static final String TYPE = "orders";

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public Class<OrderCsvRow> getBeanClass() {
    return OrderCsvRow.class;
  }

  @Override
  public CsvResult handle(CsvClient<OrderCsvRow> client) {
    return new CsvTemplate().read(client::readBean, (order) -> save(order));
  }

}
```

Handlers are detected automatically during application startup and offered to end users.
By default the handlers are insecure, add security rules to the handler implementation when needed.

## Endpoints

### GET /csv
Retrieve all registered CSV types and default separator/quote characters.
This endpoint is used to prefill the CSV upload form.

### POST /csv
Processes the CSV file based on the following *required* parameters:

 * file: CSV file (multipart)
 * type: CSV type name, as specified in handler
 * separator: Separator character
 * quote: Quote character
 
Content will be processed per line and results are returned as response body:

```json
{
 "success": 10,
 "errors": [
  {
   "rowNumber": 2,
   "message": "Could not map column 'age' at index 4: For input string: 'not a number'"
  }
 ]
}
```
