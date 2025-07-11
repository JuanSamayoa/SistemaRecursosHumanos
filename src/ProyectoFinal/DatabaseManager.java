/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ProyectoFinal;

import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Database Manager for Human Resources System
 * Uses secure configuration from .env file
 *
 * @author Juan Samayoa
 */
public class DatabaseManager {

    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    private static Connection connection;

    /**
     * Gets a database connection using .env configuration
     */
    public static Connection getConnection() {
        try {
            // Use secure configuration from .env file
            String url = ConfigurationSystem.getConnectionString();
            String driver = ConfigurationSystem.getDatabaseDriver();
            String username = ConfigurationSystem.getDatabaseUsername();
            String password = PasswordSecurity.getDatabasePassword(); // Use secure password handling

            // Load the driver
            Class.forName(driver);

            // Set connection timeout
            DriverManager.setLoginTimeout(ConfigurationSystem.getConnectionTimeout());

            // Create connection based on authentication type
            if (username.trim().isEmpty()) {
                // Use Windows Integrated Authentication
                logger.info("Attempting Windows Integrated Authentication");
                connection = DriverManager.getConnection(url);
            } else {
                // Use SQL Server Authentication
                logger.info("Attempting SQL Server Authentication for user: " + username);
                connection = DriverManager.getConnection(url, username, password);
            }

            return connection;

        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Database driver not found: " + e.getMessage());
            throw new RuntimeException("Driver configuration error", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error connecting to database: " + e.getMessage());

            // Provide useful debugging information
            if (e.getSQLState() != null) {
                switch (e.getSQLState()) {
                    case "08001":
                        throw new RuntimeException("Cannot connect to database server. Check host and port in .env", e);
                    case "28000":
                        throw new RuntimeException("Invalid database credentials. Check username and password in .env", e);
                    case "42000":
                        throw new RuntimeException("Database not found. Check database name in .env", e);
                    default:
                        throw new RuntimeException("Database error: " + e.getMessage(), e);
                }
            }
            throw new RuntimeException("Database connection error", e);
        }
    }

    /**
     * Closes current connection safely
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    logger.info("Database connection closed successfully");
                }
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    /**
     * Checks if connection is active
     */
    public static boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error validating connection: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets a new connection if current one is not valid
     */
    public static Connection getValidConnection() {
        if (!isConnectionValid()) {
            closeConnection();
            return getConnection();
        }
        return connection;
    }
}
