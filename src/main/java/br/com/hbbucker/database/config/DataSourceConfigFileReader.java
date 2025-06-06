package br.com.hbbucker.database.config;

import br.com.hbbucker.database.DataSourceProperties;
import br.com.hbbucker.shared.database.DataBaseType;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public final class DataSourceConfigFileReader implements DataSourceConfig {

    public static final int SIZE_SPLITED_PROPERTY = 3;
    @ConfigProperty(name = "database.config.file")
    private String filePath;

    @Override
    public DataSourcePropertiesList loadDatabaseConfigurations() {
        Properties properties = loadPropertiesFromFile(filePath);
        return parseProperties(properties);
    }

    private Properties loadPropertiesFromFile(final String filePath) {
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            properties.load(reader);
        } catch (IOException e) {
            throw new RuntimeException("Error load properties from file: " + filePath, e);
        }
        return properties;
    }

    private DataSourcePropertiesList parseProperties(final Properties properties) {
        DataSourcePropertiesList databaseConfigurations = new DataSourcePropertiesList();

        properties.stringPropertyNames()
                .stream()
                .filter(this::isDatabaseProperty)
                .forEach(propertyName -> processProperty(propertyName, properties, databaseConfigurations));

        return databaseConfigurations;
    }

    private boolean isDatabaseProperty(final String propertyName) {
        return propertyName.startsWith("database.")
                && propertyName.split("\\.").length == SIZE_SPLITED_PROPERTY;
    }

    private void processProperty(final String propertyName, final Properties properties, final DataSourcePropertiesList databaseConfigurations) {
        String[] parts = propertyName.split("\\.");
        String sourceName = parts[1];
        String propertyKey = parts[2];
        String value = properties.getProperty(propertyName);

        DataSourceProperties sourceProperties = databaseConfigurations.get(sourceName);
        sourceProperties.setSourceName(sourceName);

        applyProperty(sourceProperties, propertyKey, value);
    }

    private void applyProperty(
            final DataSourceProperties sourceProperties,
            final String propertyKey,
            final String value) {
        switch (propertyKey) {
            case "type":
                sourceProperties.setDbType(DataBaseType.valueOf(value));
                break;
            case "host":
                sourceProperties.setHost(value);
                break;
            case "port":
                sourceProperties.setPort(parsePort(value));
                break;
            case "username":
                sourceProperties.setUser(value);
                break;
            case "password":
                sourceProperties.setPassword(value);
                break;
            case "database":
                sourceProperties.setDatabase(value);
                break;
            default:
                logUnknownProperty(propertyKey);
        }
    }

    private int parsePort(final String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port number: " + value, e);
        }
    }

    private void logUnknownProperty(final String propertyKey) {
        Log.error("Unknown property: " + propertyKey);
    }
}
