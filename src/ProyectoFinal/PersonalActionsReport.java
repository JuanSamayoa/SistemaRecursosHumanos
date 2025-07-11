package ProyectoFinal;

import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Personal Actions Report for the Human Resources System
 * Generates reports for absences, vacations and other personal actions
 *
 * @author Juan Samayoa
 */
public class PersonalActionsReport extends Report {

    private int actionId;
    private int employeeId;
    private String firstName;
    private String lastName;
    private String actionType;
    private String actionDate;
    private String actionDetail;

    // Constructor
    public PersonalActionsReport(int actionId, int employeeId, String firstName, String lastName,
                                String actionType, String actionDate, String actionDetail) {
        super("Personal Actions Report", "ACTIONS");
        this.actionId = actionId;
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.actionType = actionType;
        this.actionDate = actionDate;
        this.actionDetail = actionDetail;
    }

    // Static method to generate personal actions report by date range
    // Supports flexible date filtering:
    // - startDate null, endDate provided: from beginning of time to endDate
    // - startDate provided, endDate null: from startDate to end of time
    // - both dates provided: within the date range
    // - both dates null: all records (no date filter)
    public static ArrayList<PersonalActionsReport> generatePersonalActionsReport(String startDate, String endDate) throws ParseException {
        ArrayList<PersonalActionsReport> report = new ArrayList<>();
        Connection connection = null;
        
        // Build dynamic query based on date parameters
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ap.id, ap.idEmpleado, e.nombre, e.apellido, ta.tipo AS tipoAccion, ap.fecha, ap.detalle ")
                   .append("FROM RecursosHumanos.Accion_Personal ap ")
                   .append("JOIN RecursosHumanos.Empleado e ON ap.idEmpleado = e.id ")
                   .append("JOIN RecursosHumanos.Cat_Acciones ta ON ap.tipoAccion = ta.id ")
                   .append("WHERE ta.tipo IN ('permiso', 'licencia', 'vacaciones') ");
        
        // Determine date filtering logic
        boolean hasStartDate = startDate != null && !startDate.trim().isEmpty();
        boolean hasEndDate = endDate != null && !endDate.trim().isEmpty();
        
        if (hasStartDate || hasEndDate) {
            queryBuilder.append("AND ");
            
            if (hasStartDate && hasEndDate) {
                // Case 3: Both dates provided - search within range
                queryBuilder.append("ap.fecha BETWEEN ? AND ? ");
            } else if (hasStartDate) {
                // Case 2: Only start date - from start date to end of time
                queryBuilder.append("ap.fecha >= ? ");
            } else {
                // Case 1: Only end date - from beginning of time to end date
                queryBuilder.append("ap.fecha <= ? ");
            }
        }
        // Case 4: No additional date filter beyond the type filter
        
        queryBuilder.append("ORDER BY ap.fecha DESC");
        
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
                int actionId = rs.getInt("id");
                int employeeId = rs.getInt("idEmpleado");
                String firstName = rs.getString("nombre");
                String lastName = rs.getString("apellido");
                String actionType = rs.getString("tipoAccion");
                String actionDate = rs.getString("fecha");
                String actionDetail = rs.getString("detalle");

                String actionDateFormatted = Utils.convertToStandardDate(actionDate);

                PersonalActionsReport reportItem = new PersonalActionsReport(
                    actionId, employeeId, firstName, lastName,
                    actionType, actionDateFormatted, actionDetail
                );
                report.add(reportItem);
            }

        } catch (Exception e) {
            System.out.println("Error generating personal actions report: " + e.getMessage());
        } finally {
            closeResources(connection);
        }

        return report;
    }

    @Override
    public ArrayList<PersonalActionsReport> generateReport() {
        // This method would require date parameters, so we return empty list
        // Use the static method generatePersonalActionsReport(String startDate, String endDate) instead
        return new ArrayList<>();
    }

    @Override
    public String getReportSummary() {
        return String.format("Personal Actions Report - Employee: %s %s, Action: %s, Date: %s",
                           firstName, lastName, actionType, actionDate);
    }

    // Getters and Setters
    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
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

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionDate() {
        return actionDate;
    }

    public void setActionDate(String actionDate) {
        this.actionDate = actionDate;
    }

    public String getActionDetail() {
        return actionDetail;
    }

    public void setActionDetail(String actionDetail) {
        this.actionDetail = actionDetail;
    }

    @Override
    public String toString() {
        return String.format("PersonalActionsReport{actionId=%d, employee='%s %s', actionType='%s', date='%s'}",
                           actionId, firstName, lastName, actionType, actionDate);
    }
}
