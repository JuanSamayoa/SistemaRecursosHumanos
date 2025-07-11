package ProyectoFinal;

import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Personal Movements Report for the Human Resources System
 * Generates reports for employee promotions, transfers and position changes
 *
 * @author Juan Samayoa
 */
public class PersonalMovementsReport extends Report {

    private int movementId;
    private int employeeId;
    private String firstName;
    private String lastName;
    private String movementType;
    private String movementDate;
    private String movementDetail;

    // Constructor
    public PersonalMovementsReport(int movementId, int employeeId, String firstName, String lastName,
                                  String movementType, String movementDate, String movementDetail) {
        super("Personal Movements Report", "MOVEMENTS");
        this.movementId = movementId;
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.movementType = movementType;
        this.movementDate = movementDate;
        this.movementDetail = movementDetail;
    }

    // Static method to generate personal movements report by date range
    // Supports flexible date filtering:
    // - startDate null, endDate provided: from beginning of time to endDate
    // - startDate provided, endDate null: from startDate to end of time (2040-12-31)
    // - both dates provided: within the date range
    // - both dates null: all records (no date filter)
    public static ArrayList<PersonalMovementsReport> generatePersonalMovementsReport(String startDate, String endDate) throws ParseException {
        ArrayList<PersonalMovementsReport> report = new ArrayList<>();
        Connection connection = null;
        
        // Build dynamic query based on date parameters
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT mp.id, mp.idEmpleado, e.nombre, e.apellido, cm.tipo AS tipoMovimiento, mp.fecha, mp.detalle ")
                   .append("FROM RecursosHumanos.Movimiento_Personal mp ")
                   .append("JOIN RecursosHumanos.Empleado e ON mp.idEmpleado = e.id ")
                   .append("JOIN RecursosHumanos.Cat_Movimientos cm ON mp.tipoMovimiento = cm.id ");
        
        // Determine date filtering logic
        boolean hasStartDate = startDate != null && !startDate.trim().isEmpty();
        boolean hasEndDate = endDate != null && !endDate.trim().isEmpty();
        
        if (hasStartDate || hasEndDate) {
            queryBuilder.append("WHERE ");
            
            if (hasStartDate && hasEndDate) {
                // Case 3: Both dates provided - search within range
                queryBuilder.append("mp.fecha BETWEEN ? AND ? ");
            } else if (hasStartDate) {
                // Case 2: Only start date - from start date to end of time
                queryBuilder.append("mp.fecha >= ? ");
            } else {
                // Case 1: Only end date - from beginning of time to end date
                queryBuilder.append("mp.fecha <= ? ");
            }
        }
        // Case 4: No dates provided - no WHERE clause, returns all records
        
        queryBuilder.append("ORDER BY mp.fecha DESC");
        
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
                int movementId = rs.getInt("id");
                int employeeId = rs.getInt("idEmpleado");
                String firstName = rs.getString("nombre");
                String lastName = rs.getString("apellido");
                String movementType = rs.getString("tipoMovimiento");
                String movementDate = rs.getString("fecha");
                String movementDetail = rs.getString("detalle");

                String movementDateFormatted = Utils.convertToStandardDate(movementDate);

                PersonalMovementsReport reportItem = new PersonalMovementsReport(
                    movementId, employeeId, firstName, lastName,
                    movementType, movementDateFormatted, movementDetail
                );
                report.add(reportItem);
            }

        } catch (Exception e) {
            System.out.println("Error generating personal movements report: " + e.getMessage());
        } finally {
            closeResources(connection);
        }

        return report;
    }

    // Static method to generate movements report by employee ID
    public static ArrayList<PersonalMovementsReport> generateMovementsByEmployee(int employeeId) {
        ArrayList<PersonalMovementsReport> report = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DatabaseManager.getConnection();
            ps = connection.prepareStatement(
                "SELECT mp.id, mp.idEmpleado, e.nombre, e.apellido, cm.tipo AS tipoMovimiento, mp.fecha, mp.detalle " +
                "FROM RecursosHumanos.Movimiento_Personal mp " +
                "JOIN RecursosHumanos.Empleado e ON mp.idEmpleado = e.id " +
                "JOIN RecursosHumanos.Cat_Movimientos cm ON mp.tipoMovimiento = cm.id " +
                "WHERE mp.idEmpleado = ? " +
                "ORDER BY mp.fecha DESC"
            );
            ps.setInt(1, employeeId);

            rs = ps.executeQuery();

            while(rs.next()){
                int movementId = rs.getInt("id");
                int empId = rs.getInt("idEmpleado");
                String firstName = rs.getString("nombre");
                String lastName = rs.getString("apellido");
                String movementType = rs.getString("tipoMovimiento");
                String movementDate = rs.getString("fecha");
                String movementDetail = rs.getString("detalle");

                String movementDateFormatted = Utils.convertToStandardDate(movementDate);

                PersonalMovementsReport reportItem = new PersonalMovementsReport(
                    movementId, empId, firstName, lastName,
                    movementType, movementDateFormatted, movementDetail
                );
                report.add(reportItem);
            }

        } catch (Exception e) {
            System.out.println("Error generating movements by employee report: " + e.getMessage());
        } finally {
            closeResources(connection);
        }

        return report;
    }

    @Override
    public ArrayList<PersonalMovementsReport> generateReport() {
        // This method would require parameters, so we return empty list
        // Use the static methods generatePersonalMovementsReport() or generateMovementsByEmployee() instead
        return new ArrayList<>();
    }

    @Override
    public String getReportSummary() {
        return String.format("Personal Movements Report - Employee: %s %s, Movement: %s, Date: %s",
                           firstName, lastName, movementType, movementDate);
    }

    // Getters and Setters
    public int getMovementId() {
        return movementId;
    }

    public void setMovementId(int movementId) {
        this.movementId = movementId;
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

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public String getMovementDate() {
        return movementDate;
    }

    public void setMovementDate(String movementDate) {
        this.movementDate = movementDate;
    }

    public String getMovementDetail() {
        return movementDetail;
    }

    public void setMovementDetail(String movementDetail) {
        this.movementDetail = movementDetail;
    }

    @Override
    public String toString() {
        return String.format("PersonalMovementsReport{movementId=%d, employee='%s %s', movementType='%s', date='%s'}",
                           movementId, firstName, lastName, movementType, movementDate);
    }
}
