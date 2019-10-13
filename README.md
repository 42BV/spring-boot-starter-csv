# Spring Boot Starter CSV

Provides support for importing data by CSV files. This Spring Boot starter
registers a `CsvService` and `/csv` endpoint, capable of handling CSV files.

It's also possible to import CSV files using a file directory.

## Handler

Each type of CSV file demands a handler implementation. This handler describes
how the file should be parsed and handled. Register a component that implements
the `CsvHandler` interface:

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

Handlers are detected during application startup and provided to end users. When handlers should 
be protected, please add your own security logic.

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

## File

Files can also be uploaded via the file system. Enabling file mode requires some
configuration:

```yaml
csv:
  file:
    cron: '*/5 * * * *' (cron when to scan)
    directory: /opt/app/csv (base directory)
    run_on_startup: true (scan on startup, default false)
```

In file mode the application will automatically scan the file system for new `*.csv`
files in the `upload` directory of every registered handler and attempt to process them. 

Files are moved to the `work` directory and processed. After processing it will either 
move to the `success` or `fail` directory. Error messages are moved in the `logs` directory.

The directory structure looks as follows:
```
 /opt/app/csv (base dir)
  /persons (csv type)
   /uploads (inbox)
   /work
   /success
   /fail
   /logs
  /orders (other type)
```

Make sure the user running the application has write access to the base directory. Sub directories
will be created on demand.
