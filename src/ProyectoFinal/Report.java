package ProyectoFinal;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Abstract Report class for the Human Resources System
 * Base class for all specific report types
 *
 * @author Juan Samayoa
 */
public abstract class Report {

    // Static variables for database operations
    protected static PreparedStatement ps;
    protected static ResultSet rs;

    // Common report properties
    protected String reportTitle;
    protected String generationDate;
    protected String reportType;

    // Constructor
    public Report(String reportTitle, String reportType) {
        this.reportTitle = reportTitle;
        this.reportType = reportType;
        this.generationDate = Utils.getCurrentDate();
    }

    // Abstract methods that must be implemented by specific report classes
    public abstract ArrayList<?> generateReport();
    public abstract String getReportSummary();

    // Common utility methods for all reports
    protected Connection getConnection() {
        return DatabaseManager.getConnection();
    }

    protected static void closeResources(Connection conn) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            System.out.println("Error closing database resources: " + e.getMessage());
        }
    }

    // Common getters
    public String getReportTitle() {
        return reportTitle;
    }

    public String getGenerationDate() {
        return generationDate;
    }

    public String getReportType() {
        return reportType;
    }

    @Override
    public String toString() {
        return "Report{" +
                "title='" + reportTitle + '\'' +
                ", type='" + reportType + '\'' +
                ", generationDate='" + generationDate + '\'' +
                '}';
    }
}
