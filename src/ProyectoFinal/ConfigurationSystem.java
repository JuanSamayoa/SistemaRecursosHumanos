package ProyectoFinal;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * System Configuration Manager
 * Handles loading environment variables from .env file
 *
 * @author Juan Samayoa
 */
public class ConfigurationSystem {

    private static final Logger logger = Logger.getLogger(ConfigurationSystem.class.getName());
    private static Properties properties = new Properties();
    private static boolean configurationLoaded = false;

    // Default values as fallback
    private static final String DB_HOST_DEFAULT = "localhost";
    private static final String DB_PORT_DEFAULT = "1433";
    private static final String DB_NAME_DEFAULT = "RecursosHumanos";
    private static final String DB_DRIVER_DEFAULT = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    static {
        loadConfiguration();
    }

    /**
     * Loads configuration from .env file
     */
    private static void loadConfiguration() {
        try (FileInputStream input = new FileInputStream(".env")) {
            properties.load(input);
            configurationLoaded = true;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not load .env file, using default values: " + e.getMessage());
            configurationLoaded = false;
        }
    }

    /**
     * Gets a property from .env file or default value
     */
    private static String getProperty(String key, String defaultValue) {
        return configurationLoaded ? properties.getProperty(key, defaultValue) : defaultValue;
    }

    /**
     * Gets the complete database connection string
     */
    public static String getConnectionString() {
        // Try to use complete connection string first
        String completeString = getProperty("DB_CONNECTION_STRING", null);

        if (completeString != null && !completeString.trim().isEmpty()) {
            // Remove credentials from connection string if present
            String cleanString = completeString.replaceAll(";user=[^;]*", "")
                                              .replaceAll(";password=[^;]*", "");
            
            // Add integrated security if no username is provided
            String username = getProperty("DB_USERNAME", "");
            if (username.trim().isEmpty()) {
                cleanString += ";integratedSecurity=true";
            }
            
            return cleanString;
        }

        // Build string using individual properties (without credentials)
        String host = getProperty("DB_HOST", DB_HOST_DEFAULT);
        String port = getProperty("DB_PORT", DB_PORT_DEFAULT);
        String database = getProperty("DB_NAME", DB_NAME_DEFAULT);
        String username = getProperty("DB_USERNAME", "");

        StringBuilder connectionString = new StringBuilder();
        connectionString.append("jdbc:sqlserver://")
                       .append(host)
                       .append(":")
                       .append(port)
                       .append(";databaseName=")
                       .append(database)
                       .append(";encrypt=true;trustServerCertificate=true");
        
        // Add integrated security if no username is provided
        if (username.trim().isEmpty()) {
            connectionString.append(";integratedSecurity=true");
        }

        return connectionString.toString();
    }

    /**
     * Gets the database driver
     */
    public static String getDatabaseDriver() {
        return getProperty("DB_DRIVER", DB_DRIVER_DEFAULT);
    }

    /**
     * Gets connection timeout
     */
    public static int getConnectionTimeout() {
        String timeout = getProperty("DB_TIMEOUT", "30");
        try {
            return Integer.parseInt(timeout);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid timeout in configuration, using 30 seconds default");
            return 30;
        }
    }

    /**
     * Gets database username
     */
    public static String getDatabaseUsername() {
        return getProperty("DB_USERNAME", "");
    }

    /**
     * Gets database password
     */
    public static String getDatabasePassword() {
        return getProperty("DB_PASSWORD", "");
    }

    /**
     * Checks if configuration was loaded correctly
     */
    public static boolean isConfigurationValid() {
        return configurationLoaded;
    }

    /**
     * Reloads configuration from .env file
     */
    public static void reloadConfiguration() {
        loadConfiguration();
    }
}
