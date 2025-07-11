package ProyectoFinal;

import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Recent Hirings Report for the Human Resources System
 * Generates reports for recently hired employees
 *
 * @author Juan Samayoa
 */
public class RecentHiringsReport extends Report {

    private int contractId;
    private int employeeId;
    private String firstName;
    private String lastName;
    private String hiringDate;
    private String contractStartDate;
    private String contractType;
    private int duration;
    private double salary;

    // Constructor
    public RecentHiringsReport(int contractId, int employeeId, String firstName, String lastName,
                              String hiringDate, String contractStartDate, String contractType,
                              int duration, double salary) {
        super("Recent Hirings Report", "HIRING");
        this.contractId = contractId;
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hiringDate = hiringDate;
        this.contractStartDate = contractStartDate;
        this.contractType = contractType;
        this.duration = duration;
        this.salary = salary;
    }

    // Static method to generate recent hirings report
    // Supports flexible date filtering:
    // - startDate null, endDate provided: from beginning of time to endDate
    // - startDate provided, endDate null: from startDate to end of time
    // - both dates provided: within the date range
    // - both dates null: all records (no date filter)
    public static ArrayList<RecentHiringsReport> generateRecentHiringsReport(String startDate, String endDate) throws ParseException {
        ArrayList<RecentHiringsReport> report = new ArrayList<>();
        Connection connection = null;
        
        // Build dynamic query based on date parameters
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT c.id, e.id AS idEmpleado, e.nombre, e.apellido, e.fechaContratacion, c.fechaInicio, tc.tipo AS tipoContrato, c.duracion, c.salario ")
                   .append("FROM RecursosHumanos.Contratacion c ")
                   .append("JOIN RecursosHumanos.Empleado e ON c.idEmpleado = e.id ")
                   .append("JOIN RecursosHumanos.Cat_Contrato tc ON c.tipoContrato = tc.id ");
        
        // Determine date filtering logic
        boolean hasStartDate = startDate != null && !startDate.trim().isEmpty();
        boolean hasEndDate = endDate != null && !endDate.trim().isEmpty();
        
        if (hasStartDate || hasEndDate) {
            queryBuilder.append("WHERE ");
            
            if (hasStartDate && hasEndDate) {
                // Case 3: Both dates provided - search within range
                queryBuilder.append("c.fechaInicio BETWEEN ? AND ? ");
            } else if (hasStartDate) {
                // Case 2: Only start date - from start date to end of time
                queryBuilder.append("c.fechaInicio >= ? ");
            } else {
                // Case 1: Only end date - from beginning of time to end date
                queryBuilder.append("c.fechaInicio <= ? ");
            }
        }
        // Case 4: No dates provided - no WHERE clause, returns all records
        
        queryBuilder.append("ORDER BY c.fechaInicio DESC");

        try {
            connection = DatabaseManager.getConnection();
            ps = connection.prepareStatement(queryBuilder.toString());
            
            // Set parameters based on available dates
            int paramIndex = 1;
            if (hasStartDate && hasEndDate) {
                // Case 3: Both dates
                ps.setString(paramIndex++, Utils.convertToISODate(startDate));
                ps.setString(paramIndex, Utils.convertToISODate(endDate));
            } else if (hasStartDate) {
                // Case 2: Only start date
                ps.setString(paramIndex, Utils.convertToISODate(startDate));
            } else if (hasEndDate) {
                // Case 1: Only end date
                ps.setString(paramIndex, Utils.convertToISODate(endDate));
            }
            // Case 4: No parameters needed

            rs = ps.executeQuery();

            while(rs.next()){
                int contractId = rs.getInt("id");
                int employeeId = rs.getInt("idEmpleado");
                String firstName = rs.getString("nombre");
                String lastName = rs.getString("apellido");
                String hiringDate = rs.getString("fechaContratacion");
                String contractStart = rs.getString("fechaInicio");
                String contractType = rs.getString("tipoContrato");
                int duration = rs.getInt("duracion");
                double salary = rs.getDouble("salario");

                String hiringDateFormatted = Utils.convertToStandardDate(hiringDate);
                String contractStartFormatted = Utils.convertToStandardDate(contractStart);

                RecentHiringsReport reportItem = new RecentHiringsReport(
                    contractId, employeeId, firstName, lastName,
                    hiringDateFormatted, contractStartFormatted, contractType,
                    duration, salary
                );
                report.add(reportItem);
            }

        } catch (Exception e) {
            System.out.println("Error generating recent hirings report: " + e.getMessage());
        } finally {
            closeResources(connection);
        }

        return report;
    }

    // Overloaded method for backward compatibility - single start date
    public static ArrayList<RecentHiringsReport> generateRecentHiringsReport(String startDate) throws ParseException {
        return generateRecentHiringsReport(startDate, null);
    }

    @Override
    public ArrayList<RecentHiringsReport> generateReport() {
        // This method would require a date parameter, so we return empty list
        // Use the static method generateRecentHiringsReport(String startDate) instead
        return new ArrayList<>();
    }

    @Override
    public String getReportSummary() {
        return String.format("Recent Hirings Report - Employee: %s %s, Contract Type: %s, Salary: Q%.2f",
                           firstName, lastName, contractType, salary);
    }

    // Getters and Setters
    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getHiringDate() {
        return hiringDate;
    }

    public void setHiringDate(String hiringDate) {
        this.hiringDate = hiringDate;
    }

    public String getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(String contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return String.format("RecentHiringsReport{contractId=%d, employee='%s %s', contractType='%s', salary=%.2f}",
                           contractId, firstName, lastName, contractType, salary);
    }
}
