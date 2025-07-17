package ProyectoFinal;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Personal Action class for the Human Resources System
 * Handles employee personal actions management independently
 *
 * @author Juan Samayoa
 * Carne: 9390-23-2010
 * Programación II
 */
public class PersonalActions {

    // Static variables for database operations
    static PreparedStatement ps;
    static ResultSet rs;

    private int id;
    private int employeeId;
    private String actionType;
    private String actionDate;
    private String detail;
    
    private static ArrayList<PersonalActions> actions = new ArrayList<>();
    
    // Constructor with parameters
    public PersonalActions(int id, int employeeId, String actionType, String actionDate, String detail) {
        this.id = id;
        this.employeeId = employeeId;
        this.actionType = actionType;
        this.actionDate = actionDate;
        this.detail = detail;
    }

    // Constructor without parameters
    public PersonalActions() {}

    // ================================
    // GETTERS AND SETTERS
    // ================================
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    // ================================
    // CRUD METHODS
    // ================================

    /**
     * Adds a new personal action for an employee
     * 
     * @param employeeId  employee ID
     * @param actionType  type of action (permiso, licencia, vacaciones, otros)
     * @param actionDate  date of action (dd/mm/yyyy format)
     * @param detail      action details
     * @return true if action was added successfully, false otherwise
     * @throws java.text.ParseException
     */
    public static boolean add(int employeeId, String actionType, String actionDate, String detail) throws ParseException {
        Connection connection = null;
        int typeId = 0;
        String dbDate = Utils.convertToISODate(actionDate);

        switch(actionType.toLowerCase()){
            case "permiso" -> typeId = 1;
            case "licencia" -> typeId = 2;
            case "vacaciones" -> typeId = 3;
            case "otros" -> typeId = 4;
        }
        
        try {
            connection = DatabaseManager.getConnection();
            ps = connection.prepareStatement("INSERT INTO RecursosHumanos.Accion_Personal (idEmpleado, tipoAccion, fecha, detalle) VALUES (?, ?, ?, ?)");
            ps.setInt(1, employeeId);
            ps.setInt(2, typeId);
            ps.setString(3, dbDate);
            ps.setString(4, detail);
            
            int result = ps.executeUpdate();

            if (result > 0) {
                System.out.println("Personal action added successfully");
                return true;
            }
            
        } catch (SQLException e) {
            System.out.println("Error adding personal action: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return false;
    }

    /**
     * Generates a report by date
     * @param date
     * @return 
     */
    public static ArrayList<PersonalActions> generateReportByDate(String date) {
        ArrayList<PersonalActions> report = new ArrayList<>();
        Connection connection = null;

        try {
            String dbDate = Utils.convertToISODate(date);
            connection = DatabaseManager.getConnection();

            ps = connection.prepareStatement(
                "SELECT ap.id, ap.idEmpleado, ta.tipo AS tipoAccion, ap.fecha, ap.detalle " +
                "FROM RecursosHumanos.Accion_Personal ap " +
                "JOIN RecursosHumanos.Cat_Acciones ta ON ap.tipoAccion = ta.id " +
                "WHERE ap.fecha = ?"
            );
            ps.setString(1, dbDate);

            rs = ps.executeQuery();
            
            while(rs.next()){
                int id = rs.getInt("id");
                int empId = rs.getInt("idEmpleado");
                String type = rs.getString("tipoAccion");
                String actionDate = rs.getString("fecha");
                String detail = rs.getString("detalle");
                
                String formattedDate = Utils.convertToStandardDate(actionDate);

                PersonalActions action = new PersonalActions(id, empId, type, formattedDate, detail);
                report.add(action);
            }

            return report;
        } catch (SQLException | ParseException e) {
            System.out.println("Error generating report: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return report;
    }
    
    /**
     * Gets actions for a specific employee
     * @param employeeId
     * @return 
     */
    public static ArrayList<PersonalActions> getEmployeeActions(int employeeId) {
        ArrayList<PersonalActions> employeeActions = new ArrayList<>();
        Connection connection = null;
        
        try {
            connection = DatabaseManager.getConnection();
            ps = connection.prepareStatement(
                "SELECT ap.id, ap.idEmpleado, tap.tipo AS tipoAccion, ap.fecha, ap.detalle " +
                "FROM RecursosHumanos.Accion_Personal ap " +
                "JOIN RecursosHumanos.Cat_Acciones tap ON ap.tipoAccion = tap.id " +
                "WHERE ap.idEmpleado = ?"
            );
            ps.setInt(1, employeeId);
            rs = ps.executeQuery();
            
            while(rs.next()){
                int id = rs.getInt("id");
                int empId = rs.getInt("idEmpleado");
                String type = rs.getString("tipoAccion");
                String actionDate = rs.getString("fecha");
                String detail = rs.getString("detalle");
                
                String formattedDate = Utils.convertToStandardDate(actionDate);
                
                PersonalActions action = new PersonalActions(id, empId, type, formattedDate, detail);
                employeeActions.add(action);
            }
            
        } catch (SQLException | ParseException e) {
            System.out.println("Error getting employee actions: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return employeeActions;
    }

    /**
     * Updates an existing personal action
     * 
     * @param actionId    ID of the action to update
     * @param actionType  new action type
     * @param actionDate  new action date
     * @param detail      new action details
     * @return true if action was updated successfully, false otherwise
     */
    public static boolean update(int actionId, String actionType, String actionDate, String detail) {
        Connection connection = null;
        int typeId = 0;

        switch(actionType.toLowerCase()){
            case "permiso" -> typeId = 1;
            case "licencia" -> typeId = 2;
            case "vacaciones" -> typeId = 3;
            case "otros" -> typeId = 4;
        }

        try {
            String dbDate = Utils.convertToISODate(actionDate);
            connection = DatabaseManager.getConnection();
            ps = connection.prepareStatement(
                "UPDATE RecursosHumanos.Accion_Personal " +
                "SET tipoAccion = ?, fecha = ?, detalle = ? " +
                "WHERE id = ?"
            );
            ps.setInt(1, typeId);
            ps.setString(2, dbDate);
            ps.setString(3, detail);
            ps.setInt(4, actionId);

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException | ParseException e) {
            System.out.println("Error updating personal action: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    /**
     * Deletes a personal action
     * 
     * @param actionId ID of the action to delete
     * @return true if action was deleted successfully, false otherwise
     */
    public static boolean delete(int actionId) {
        Connection connection = null;

        try {
            connection = DatabaseManager.getConnection();
            ps = connection.prepareStatement("DELETE FROM RecursosHumanos.Accion_Personal WHERE id = ?");
            ps.setInt(1, actionId);

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting personal action: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    /**
     * Gets the next available ID for personal actions
     * 
     * @return next available ID
     */
    public static int getNextId() {
        Connection connection = null;
        int nextId = 1;
        
        try {
            connection = DatabaseManager.getConnection();
            ps = connection.prepareStatement("SELECT MAX(id) + 1 AS nextId FROM RecursosHumanos.Accion_Personal");
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Integer result = rs.getInt("nextId");
                if (result != null && result > 0) {
                    nextId = result;
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error getting next personal action ID: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return nextId;
    }

    /**
     * Gets all personal actions from database
     * 
     * @return ArrayList of all personal actions
     */
    public static ArrayList<PersonalActions> getAll() {
        ArrayList<PersonalActions> allActions = new ArrayList<>();
        Connection connection = null;
        
        try {
            connection = DatabaseManager.getConnection();
            ps = connection.prepareStatement(
                "SELECT ap.id, ap.idEmpleado, tap.tipo AS tipoAccion, ap.fecha, ap.detalle " +
                "FROM RecursosHumanos.Accion_Personal ap " +
                "JOIN RecursosHumanos.Cat_Acciones tap ON ap.tipoAccion = tap.id " +
                "ORDER BY ap.fecha DESC"
            );
            rs = ps.executeQuery();
            
            while(rs.next()){
                int id = rs.getInt("id");
                int empId = rs.getInt("idEmpleado");
                String type = rs.getString("tipoAccion");
                String actionDate = rs.getString("fecha");
                String detail = rs.getString("detalle");
                
                String formattedDate = Utils.convertToStandardDate(actionDate);
                
                PersonalActions action = new PersonalActions(id, empId, type, formattedDate, detail);
                allActions.add(action);
            }
            
        } catch (Exception e) {
            System.out.println("Error getting all personal actions: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return allActions;
    }

    /**
     * Gets all actions (static list)
     * 
     * @return static ArrayList of actions
     */
    public static ArrayList<PersonalActions> getActions() {
        return actions;
    }

    @Override
    public String toString() {
        return "PersonalActions{" +
               "id=" + id +
               ", employeeId=" + employeeId +
               ", actionType='" + actionType + '\'' +
               ", actionDate='" + actionDate + '\'' +
               ", detail='" + detail + '\'' +
               '}';
    }
}
