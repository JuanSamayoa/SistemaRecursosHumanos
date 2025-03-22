package ProyectoFinal;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Juan Samayoa
 * Carne: 9390-23-2010
 * Programación II
 */
public class AccionPersonal {
    
    public static final String user = "admindb";
    public static final String password = "root";
    public static final String url = "jdbc:sqlserver://localhost:1433;databaseName=SistemaRRHH;encrypt=true;trustServerCertificate=true";
    static PreparedStatement ps;
    static ResultSet rs;
    
    private int idAccion;
    private int idEmpleado;
    private String tipoAccion;
    private String fechaAccion;
    private String detalle;
    
    private static ArrayList<AccionPersonal> acciones = new ArrayList<>();
    
    //Constructor con parametros
    public AccionPersonal(int idAccion, int idEmpleado, String tipoAccion, String fechaAccion, String detalle) {
        this.idAccion = idAccion;
        this.idEmpleado = idEmpleado;
        this.tipoAccion = tipoAccion;
        this.fechaAccion = fechaAccion;
        this.detalle = detalle;
    }

    //Constructor sin parametros
    public AccionPersonal() {}
    
    //Metodo para registrar una acción en base al ID del empleado que desea registrar la misma acción.
    public static boolean registrarAccion(int idEmpleado, String tipo, String fecha, String detalle) throws ParseException{
        Connection conexion = null;
        int idTipo = 0;
        String fechaBD = convertirAFechaISO(fecha);
        
        switch(tipo.toLowerCase()){
            case "permiso" -> idTipo = 1;
            case "licencia" -> idTipo = 2;
            case "vacaciones" -> idTipo = 3;
            case "otros" -> idTipo = 4;
        }
        
        try {
            conexion = DriverManager.getConnection(url, user, password);
            ps = conexion.prepareStatement("INSERT INTO RecursosHumanos.Accion_Personal(idEmpleado, tipoAccion, fecha, detalle) VALUES (?, ?, ?, ?)");
            ps.setInt(1, idEmpleado);
            ps.setInt(2, idTipo);
            ps.setString(3, fechaBD);
            ps.setString(4, detalle);
            
            int resp = ps.executeUpdate();
            
            if(resp > 0){
                conexion.close();
                System.out.println("Se ha agregado una acción.");
                return true;
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return false;
        }
        return false;
    }
    
    //Metodo que retorna un arreglo de acciones en base a un ID de empleado
    public static ArrayList<AccionPersonal> consultarAccionesEmpleado(int idEmpleado){
        ArrayList<AccionPersonal> resultado = new ArrayList<>();
        Connection conexion = null;
        if(!resultado.isEmpty()){
            resultado.clear();
        }
        try {
            conexion = DriverManager.getConnection(url, user, password);
            ps = conexion.prepareStatement(
                "SELECT ap.id, ap.idEmpleado, ca.tipo AS tipoAccion, ap.fecha, ap.detalle " +
                "FROM RecursosHumanos.Accion_Personal ap " +
                "JOIN RecursosHumanos.Cat_Acciones ca ON ap.tipoAccion = ca.id " +
                "WHERE ap.idEmpleado = ?");
            ps.setInt(1, idEmpleado);
            
            rs = ps.executeQuery();
            
            while(rs.next()){
                int id = rs.getInt("id");
                int idEmp = rs.getInt("idEmpleado");
                String tipo = rs.getString("tipoAccion");
                String fechaBD = rs.getString("fecha");
                String detalle = rs.getString("detalle");
                
                String fecha = convertirAFechaEstandar(fechaBD);
                
                AccionPersonal accion = new AccionPersonal(id, idEmp, tipo, fecha, detalle);
                resultado.add(accion);
                
            }
            conexion.close();
            return resultado;
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return null;
    }

    // Método para generar el reporte de acciones en base a dos fechas
    public static ArrayList<AccionPersonal> generarReporteAcciones(String fechaI, String fechaF) throws ParseException {
        ArrayList<AccionPersonal> reporte = new ArrayList<>();
        Connection conexion = null;
        String fechaIBD  = convertirAFechaISO(fechaI);
        String fechaFBD = convertirAFechaISO(fechaF);
        
        try {
            conexion = DriverManager.getConnection(url, user, password);
            ps = conexion.prepareStatement("SELECT ap.id, ap.idEmpleado, ca.tipo AS tipoAccion, ap.fecha, ap.detalle" +
                "FROM RecursosHumanos.Accion_Personal ap" +
                "JOIN RecursosHumanos.Cat_Acciones ca ON ap.tipoAccion = ca.id" +
                "WHERE ap.fecha BETWEEN ? AND ?");
            ps.setString(1, fechaIBD);
            ps.setString(2, fechaFBD);
            
            rs = ps.executeQuery();
            
            while(rs.next()){
                int id = rs.getInt("id");
                int idEmp = rs.getInt("idEmpleado");
                String tipo = rs.getString("tipoAccion");
                String fechaBD = rs.getString("fecha");
                String detalle = rs.getString("detalle");
                
                String fecha = convertirAFechaEstandar(fechaBD);
                
                AccionPersonal accion = new AccionPersonal(id, idEmp, tipo, fecha, detalle);
                reporte.add(accion);
            }
            conexion.close();
            return reporte;
        } catch (Exception e) {
            System.out.println("Error: " + e);   
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "AccionPersonal{" +
                "idAccion=" + idAccion +
                ", idEmpleado=" + idEmpleado +
                ", tipoAccion='" + tipoAccion + '\'' +
                ", fechaAccion='" + fechaAccion + '\'' +
                ", detalle='" + detalle + '\'' +
                '}';
    }

    //Metodo que convierte fecha en formato dd/mm/aaaa a formato aaaa-mm-dd
    public static String convertirAFechaISO(String fecha) throws ParseException {
        SimpleDateFormat formatoOriginal = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatoObjetivo = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaObj = formatoOriginal.parse(fecha);
        return formatoObjetivo.format(fechaObj);
    }

    //Metodo que convierte fecha en formato aaaa-mm-dd a formato dd/mm/aaaa 
    public static String convertirAFechaEstandar(String fecha) throws ParseException {
        SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoObjetivo = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaObj = formatoOriginal.parse(fecha);
        return formatoObjetivo.format(fechaObj);
    }
    
    //getters y setters
    public int getIdAccion() {
        return idAccion;
    }

    public void setIdAccion(int idAccion) {
        this.idAccion = idAccion;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getTipoAccion() {
        return tipoAccion;
    }

    public void setTipoAccion(String tipoAccion) {
        this.tipoAccion = tipoAccion;
    }

    public String getFechaAccion() {
        return fechaAccion;
    }

    public void setFechaAccion(String fechaAccion) {
        this.fechaAccion = fechaAccion;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public static ArrayList<AccionPersonal> getAcciones() {
        return acciones;
    }
}
