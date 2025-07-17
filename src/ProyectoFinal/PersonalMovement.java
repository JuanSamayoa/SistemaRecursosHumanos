package ProyectoFinal;

import java.text.ParseException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Personal Movement class for the Human Resources System
 * Handles employee movement management independently
 *
 * @author Juan Samayoa
 */
public class PersonalMovement {

    // Static variables for database operations
    static PreparedStatement ps;
    static ResultSet rs;

    private int id;
    private int employeeId;
    private String movementType;
    private String movementDate;
    private String detail;
    
    private static ArrayList<PersonalMovement> movements = new ArrayList<>();
    
    // Constructor with parameters
    public PersonalMovement(int id, int employeeId, String movementType, String movementDate, String detail) {
        this.id = id;
        this.employeeId = employeeId;
        this.movementType = movementType;
        this.movementDate = movementDate;
        this.detail = detail;
    }

    // Constructor without parameters
    public PersonalMovement() {}

    // Method to register a movement based on employee ID and data
    /**
     * Adds a new personal movement for an employee
     * 
     * @param employeeId     employee ID
     * @param movementType   type of movement
     * @param movementDate   date of movement (dd/mm/yyyy format)
     * @param detail         movement details
     * @return true if movement was added successfully, false otherwise
     */
    public static boolean add(int employeeId, String movementType, String movementDate, String detail) throws ParseException {
        Connection connection = null;
        int typeId = 0;
        String dbDate = Utils.convertToISODate(movementDate);

        switch(movementType.toLowerCase()){
            case "promoción" -> typeId = 1;
            case "transferencia" -> typeId = 2;
            case "cambio de puesto" -> typeId = 3;
            case "ascenso" -> typeId = 4;
            case "reasignación" -> typeId = 5;
        }
        
        try {
            connection = DatabaseManager.getConnection();
            ps = connection.prepareStatement("INSERT INTO RecursosHumanos.Movimiento_Personal(idEmpleado, tipoMovimiento, fecha, detalle) VALUES (?, ?, ?, ?)");
            ps.setInt(1, employeeId);
            ps.setInt(2, typeId);
            ps.setString(3, dbDate);
            ps.setString(4, detail);
            
            int result = ps.executeUpdate();
            
            if(result > 0){
                System.out.println("Personal movement added successfully");
                return true;
            }

        } catch (Exception e) {
            System.out.println("Error adding personal movement: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return false;
    }
    
    // Method that returns an array of movements based on employee ID
    /**
     * Gets movements for a specific employee
     * 
     * @param employeeId employee ID to get movements for
     * @return ArrayList of personal movements for the employee
     */
    public static ArrayList<PersonalMovement> getEmployeeMovements(int employeeId){
        ArrayList<PersonalMovement> employeeMovements = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DatabaseManager.getConnection();
            ps = connection.prepareStatement(
                "SELECT mp.id, mp.idEmpleado, cm.tipo AS tipoMovimiento, mp.fecha, mp.detalle " +
                "FROM RecursosHumanos.Movimiento_Personal mp " +
                "JOIN RecursosHumanos.Cat_Movimientos cm ON mp.tipoMovimiento = cm.id " +
                "WHERE mp.idEmpleado = ?");
            ps.setInt(1, employeeId);
            
            rs = ps.executeQuery();
            
            while(rs.next()){
                int id = rs.getInt("id");
                int empId = rs.getInt("idEmpleado");
                String type = rs.getString("tipoMovimiento");
                String dbDate = rs.getString("fecha");
                String detail = rs.getString("detalle");
                
                String formattedDate = Utils.convertToStandardDate(dbDate);

                PersonalMovement movement = new PersonalMovement(id, empId, type, formattedDate, detail);
                employeeMovements.add(movement);
            }

            return employeeMovements;
        } catch (SQLException | ParseException e) {
            System.out.println("Error consulting employee movements: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return employeeMovements;
    }

    /**
     * Gets all personal movements from database
     * 
     * @return ArrayList of all personal movements
     */
    public static ArrayList<PersonalMovement> getAll() {
        ArrayList<PersonalMovement> allMovements = new ArrayList<>();
        Connection connection = null;
        
        try {
            connection = DatabaseManager.getConnection();
            ps = connection.prepareStatement(
                "SELECT mp.id, mp.idEmpleado, cm.tipo AS tipoMovimiento, mp.fecha, mp.detalle " +
                "FROM RecursosHumanos.Movimiento_Personal mp " +
                "JOIN RecursosHumanos.Cat_Movimientos cm ON mp.tipoMovimiento = cm.id " +
                "ORDER BY mp.fecha DESC"
            );
            rs = ps.executeQuery();
            
            while(rs.next()){
                int id = rs.getInt("id");
                int empId = rs.getInt("idEmpleado");
                String type = rs.getString("tipoMovimiento");
                String dbDate = rs.getString("fecha");
                String detail = rs.getString("detalle");
                
                String formattedDate = Utils.convertToStandardDate(dbDate);
                
                PersonalMovement movement = new PersonalMovement(id, empId, type, formattedDate, detail);
                allMovements.add(movement);
            }
            
        } catch (Exception e) {
            System.out.println("Error getting all personal movements: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return allMovements;
    }

    /**
     * Updates an existing personal movement
     * 
     * @param movementId     ID of the movement to update
     * @param movementType   new movement type
     * @param movementDate   new movement date
     * @param detail         new movement details
     * @return true if movement was updated successfully, false otherwise
     */
    public static boolean update(int movementId, String movementType, String movementDate, String detail) {
        Connection connection = null;
        int typeId = 0;

        switch(movementType.toLowerCase()){
            case "promoción" -> typeId = 1;
            case "transferencia" -> typeId = 2;
            case "cambio de puesto" -> typeId = 3;
            case "ascenso" -> typeId = 4;
            case "reasignación" -> typeId = 5;
        }

        try {
            String dbDate = Utils.convertToISODate(movementDate);
            connection = DatabaseManager.getConnection();
            ps = connection.prepareStatement(
                "UPDATE RecursosHumanos.Movimiento_Personal " +
                "SET tipoMovimiento = ?, fecha = ?, detalle = ? " +
                "WHERE id = ?"
            );
            ps.setInt(1, typeId);
            ps.setString(2, dbDate);
            ps.setString(3, detail);
            ps.setInt(4, movementId);

            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            System.out.println("Error updating personal movement: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    /**
     * Deletes a personal movement
     * 
     * @param movementId ID of the movement to delete
     * @return true if movement was deleted successfully, false otherwise
     */
    public static boolean delete(int movementId) {
        Connection connection = null;

        try {
            connection = DatabaseManager.getConnection();
            ps = connection.prepareStatement("DELETE FROM RecursosHumanos.Movimiento_Personal WHERE id = ?");
            ps.setInt(1, movementId);

            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            System.out.println("Error deleting personal movement: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    /**
     * Gets the next available ID for personal movements
     * 
     * @return next available ID
     */
    public static int getNextId() {
        Connection connection = null;
        int nextId = 1;
        
        try {
            connection = DatabaseManager.getConnection();
            ps = connection.prepareStatement("SELECT MAX(id) + 1 AS nextId FROM RecursosHumanos.Movimiento_Personal");
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Integer result = rs.getInt("nextId");
                if (result != null && result > 0) {
                    nextId = result;
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error getting next personal movement ID: " + e.getMessage());
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
     * Gets all movements (static list)
     * 
     * @return static ArrayList of movements
     */
    public static ArrayList<PersonalMovement> getMovements() {
        return movements;
    }

    @Override
    public String toString() {
        return "MovimientoPersonal{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", movementType='" + movementType + '\'' +
                ", movementDate='" + movementDate + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }

    // Getters and Setters
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
