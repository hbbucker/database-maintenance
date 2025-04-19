# `ConfigDataSourceFileReader`

## Overview

The `ConfigDataSourceFileReader` class is responsible for reading and parsing database configuration properties from a file. It implements the `ConfigDataSource` interface and is designed to load and process properties into a structured format for use in the application.

## Key Features

- Reads a properties file specified by the `database.config.file` configuration property.
- Parses database-related properties into a `DataSourcePropertiesList` object.
- Supports multiple database configurations in a single file.
- Validates and processes specific database property keys.

## How It Works

1. **File Reading**:  
   The class reads the properties file using a `BufferedReader` and loads it into a `Properties` object.

2. **Property Parsing**:  
   It filters and processes properties that start with the prefix `database.` and have a specific structure (`database.<sourceName>.<propertyKey>`).

3. **Property Mapping**:  
   Each property is mapped to a `DataSourceProperties` object, which contains details like database type, host, port, username, password, and database name.

4. **Error Handling**:
    - Throws a `RuntimeException` if the properties file cannot be loaded.
    - Logs an error for unknown property keys.
    - Validates and throws an exception for invalid port values.

## Property File Format

The properties file should follow this structure:

```
database.<sourceName>.type=POSTGRESQL
database.<sourceName>.host=localhost
database.<sourceName>.port=5432
database.<sourceName>.username=postgres
database.<sourceName>.password=secret
database.<sourceName>.database=mydb
```

- `<sourceName>`: A unique identifier for each database configuration.
- `<propertyKey>`: The specific property for the database (e.g., `type`, `host`, `port`, etc.).

## Methods

### `loadDatabaseConfigurations()`
- Loads the properties file and parses the database configurations into a `DataSourcePropertiesList`.

### `loadPropertiesFromFile(String filePath)`
- Reads the properties file and returns a `Properties` object.

### `parseProperties(Properties properties)`
- Filters and processes database-related properties.

### `isDatabaseProperty(String propertyName)`
- Checks if a property is related to a database configuration.

### `processProperty(String propertyName, Properties properties, DataSourcePropertiesList databaseConfigurations)`
- Extracts and maps property values to the corresponding `DataSourceProperties` object.

### `applyProperty(DataSourceProperties sourceProperties, String propertyKey, String value)`
- Maps individual property keys to their respective fields in the `DataSourceProperties` object.

### `parsePort(String value)`
- Parses and validates the port value.

### `logUnknownProperty(String propertyKey)`
- Logs an error for unknown property keys.

## Dependencies

- **Jakarta Inject**: For dependency injection.
- **Lombok**: For reducing boilerplate code (e.g., `@RequiredArgsConstructor`).
- **Quarkus Logging**: For logging errors and messages.
- **MicroProfile Config**: For injecting configuration properties.

## Example Usage

```java
@Inject
ConfigDataSourceFileReader configReader;

DataSourcePropertiesList configurations = configReader.loadDatabaseConfigurations();
```

## Error Handling

- **File Not Found**: Throws a `RuntimeException` if the file cannot be read.
- **Invalid Port**: Throws an `IllegalArgumentException` for invalid port values.
- **Unknown Properties**: Logs an error for unrecognized property keys.

This class is designed to simplify the management of database configurations in a Spring Boot application, ensuring a structured and error-free approach to handling multiple data sources.