package ProyectoFinal;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    
    private static ArrayList<PersonalActions> acciones = new ArrayList<>();
    
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

    // Method to register an action based on employee ID
    public static boolean registrarAccion(int idEmpleado, String tipo, String fecha, String detalle) throws ParseException {
        Connection conexion = null;
        int idTipo = 0;
        String fechaBD = Utils.convertToISODate(fecha);

        switch(tipo.toLowerCase()){
            case "permiso" -> idTipo = 1;
            case "licencia" -> idTipo = 2;
            case "vacaciones" -> idTipo = 3;
            case "otros" -> idTipo = 4;
        }
        
        try {
            conexion = DatabaseManager.getConnection();
            ps = conexion.prepareStatement("INSERT INTO RecursosHumanos.Accion_Personal (idEmpleado, tipoAccion, fecha, detalle) VALUES (?, ?, ?, ?)");
            ps.setInt(1, idEmpleado);
            ps.setInt(2, idTipo);
            ps.setString(3, fechaBD);
            ps.setString(4, detalle);
            
            int resultado = ps.executeUpdate();

            if (resultado > 0) {
                System.out.println("Acción registrada exitosamente");
                return true;
            }
            
        } catch (Exception e) {
            System.out.println("Error al registrar acción: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (conexion != null) conexion.close();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return false;
    }

    // Method to generate report by date
    public static ArrayList<PersonalActions> generarReportePorFecha(String fecha) {
        ArrayList<PersonalActions> reporte = new ArrayList<>();
        Connection conexion = null;

        try {
            String fechaFBD = Utils.convertToISODate(fecha);
            conexion = DatabaseManager.getConnection();

            ps = conexion.prepareStatement(
                "SELECT ap.id, ap.idEmpleado, ta.tipo AS tipoAccion, ap.fecha, ap.detalle " +
                "FROM RecursosHumanos.Accion_Personal ap " +
                "JOIN RecursosHumanos.Cat_Acciones ta ON ap.tipoAccion = ta.id " +
                "WHERE ap.fecha = ?"
            );
            ps.setString(1, fechaFBD);

            rs = ps.executeQuery();
            
            while(rs.next()){
                int id = rs.getInt("id");
                int idEmp = rs.getInt("idEmpleado");
                String tipo = rs.getString("tipoAccion");
                String fechaBD = rs.getString("fecha");
                String detalle = rs.getString("detalle");
                
                String fechaFormatted = Utils.convertToStandardDate(fechaBD);

                PersonalActions accion = new PersonalActions(id, idEmp, tipo, fechaFormatted, detalle);
                reporte.add(accion);
            }

            return reporte;
        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conexion != null) conexion.close();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return new ArrayList<>();
    }
    
    // Method for getting actions by employee (for form compatibility)
    public static ArrayList<PersonalActions> consultarAccionesEmpleado(int employeeId) {
        ArrayList<PersonalActions> employeeActions = new ArrayList<>();
        Connection conexion = null;
        
        try {
            conexion = DatabaseManager.getConnection();
            ps = conexion.prepareStatement(
                "SELECT ap.id, ap.idEmpleado, tap.tipo AS tipoAccion, ap.fecha, ap.detalle " +
                "FROM RecursosHumanos.Accion_Personal ap " +
                "JOIN RecursosHumanos.Cat_Acciones tap ON ap.tipoAccion = tap.id " +
                "WHERE ap.idEmpleado = ?"
            );
            ps.setInt(1, employeeId);
            rs = ps.executeQuery();
            
            while(rs.next()){
                int id = rs.getInt("id");
                int idEmp = rs.getInt("idEmpleado");
                String tipo = rs.getString("tipoAccion");
                String fechaBD = rs.getString("fecha");
                String detalle = rs.getString("detalle");
                
                String fechaFormatted = Utils.convertToStandardDate(fechaBD);
                
                PersonalActions accion = new PersonalActions(id, idEmp, tipo, fechaFormatted, detalle);
                employeeActions.add(accion);
            }
            
        } catch (Exception e) {
            System.out.println("Error getting employee actions: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conexion != null) conexion.close();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return employeeActions;
    }

    @Override
    public String toString() {
        return "AccionPersonal{" +
                "idAccion=" + id +
                ", idEmpleado=" + employeeId +
                ", tipoAccion='" + actionType + '\'' +
                ", fechaAccion='" + actionDate + '\'' +
                ", detalle='" + detail + '\'' +
                '}';
    }

    // Getters and Setters
    public int getIdAccion() {
        return id;
    }

    public void setIdAccion(int idAccion) {
        this.id = idAccion;
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

    public static ArrayList<PersonalActions> getAcciones() {
        return acciones;
    }
}
