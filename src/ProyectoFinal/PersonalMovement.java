package ProyectoFinal;

import java.text.ParseException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

    private int idMovimiento;
    private int idEmpleado;
    private String tipoMovimiento;
    private String fechaMovimiento;
    private String detalle;
    
    private static ArrayList<PersonalMovement> movimientos = new ArrayList<>();
    
    // Constructor with parameters
    public PersonalMovement(int idMovimiento, int idEmpleado, String tipoMovimiento, String fechaMovimiento, String detalle) {
        this.idMovimiento = idMovimiento;
        this.idEmpleado = idEmpleado;
        this.tipoMovimiento = tipoMovimiento;
        this.fechaMovimiento = fechaMovimiento;
        this.detalle = detalle;
    }

    // Constructor without parameters
    public PersonalMovement() {}

    // Method to register a movement based on employee ID and data
    public static boolean registrarMovimiento(int idEmpleado, String tipo, String fecha, String detalle) throws ParseException {
        Connection conexion = null;
        int idTipo = 0;
        String fechaBD = Utils.convertToISODate(fecha);

        switch(tipo.toLowerCase()){
            case "promoción" -> idTipo = 1;
            case "transferencia" -> idTipo = 2;
            case "cambio de puesto" -> idTipo = 3;
            case "ascenso" -> idTipo = 4;
            case "reasignación" -> idTipo = 5;
        }
        
        try {
            conexion = DatabaseManager.getConnection();
            ps = conexion.prepareStatement("INSERT INTO RecursosHumanos.Movimiento_Personal(idEmpleado, tipoMovimiento, fecha, detalle) VALUES (?, ?, ?, ?)");
            ps.setInt(1, idEmpleado);
            ps.setInt(2, idTipo);
            ps.setString(3, fechaBD);
            ps.setString(4, detalle);
            
            int resp = ps.executeUpdate();
            
            if(resp > 0){
                System.out.println("Movement registered successfully");
                return true;
            }

        } catch (Exception e) {
            System.out.println("Error registering movement: " + e.getMessage());
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
    
    // Method that returns an array of movements based on employee ID
    public static ArrayList<PersonalMovement> consultarMovimientosEmpleado(int idEmpleado){
        ArrayList<PersonalMovement> resultado = new ArrayList<>();
        Connection conexion = null;

        try {
            conexion = DatabaseManager.getConnection();
            ps = conexion.prepareStatement(
                "SELECT mp.id, mp.idEmpleado, cm.tipo AS tipoMovimiento, mp.fecha, mp.detalle " +
                "FROM RecursosHumanos.Movimiento_Personal mp " +
                "JOIN RecursosHumanos.Cat_Movimientos cm ON mp.tipoMovimiento = cm.id " +
                "WHERE mp.idEmpleado = ?");
            ps.setInt(1, idEmpleado);
            
            rs = ps.executeQuery();
            
            while(rs.next()){
                int id = rs.getInt("id");
                int idEmp = rs.getInt("idEmpleado");
                String tipo = rs.getString("tipoMovimiento");
                String fechaBD = rs.getString("fecha");
                String detalle = rs.getString("detalle");
                
                String fechaFormatted = Utils.convertToStandardDate(fechaBD);

                PersonalMovement movimiento = new PersonalMovement(id, idEmp, tipo, fechaFormatted, detalle);
                resultado.add(movimiento);
            }

            return resultado;
        } catch (Exception e) {
            System.out.println("Error consulting employee movements: " + e.getMessage());
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

    @Override
    public String toString() {
        return "MovimientoPersonal{" +
                "idMovimiento=" + idMovimiento +
                ", idEmpleado=" + idEmpleado +
                ", tipoMovimiento='" + tipoMovimiento + '\'' +
                ", fechaMovimiento='" + fechaMovimiento + '\'' +
                ", detalle='" + detalle + '\'' +
                '}';
    }

    // Getters and Setters
    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public String getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(String fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public static ArrayList<PersonalMovement> getMovimientos() {
        return movimientos;
    }
}
