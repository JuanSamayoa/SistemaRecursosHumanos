package ProyectoFinal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Juan Samayoa
 * Carne: 9390-23-2010
 * Programación II
 */
public class MovimientoPersonal {
    
    public static final String user = "admindb";
    public static final String password = "root";
    public static final String url = "jdbc:sqlserver://localhost:1433;databaseName=SistemaRRHH;encrypt=true;trustServerCertificate=true";
    static PreparedStatement ps;
    static ResultSet rs;
    
    private int idMovimiento;
    private int idEmpleado;
    private String tipoMovimiento;
    private String fechaMovimiento;
    private String detalle;
    
    private static ArrayList<MovimientoPersonal> movimientos = new ArrayList<>();
    
    //Constructor con parametros
    public MovimientoPersonal(int idMovimiento, int idEmpleado, String tipoMovimiento, String fechaMovimiento, String detalle) {
        this.idMovimiento = idMovimiento;
        this.idEmpleado = idEmpleado;
        this.tipoMovimiento = tipoMovimiento;
        this.fechaMovimiento = fechaMovimiento;
        this.detalle = detalle;
    }

    //Constructor sin parametros
    public MovimientoPersonal() {}

    //Metodo que registra un movimiento segun el ID de empleado y sus datos para agregarlo a la base de datos
    public static boolean registrarMovimiento(int idEmpleado, String tipo, String fecha, String detalle) throws ParseException {
        Connection conexion = null;
        int idTipo = 0;
        String fechaBD = convertirAFechaISO(fecha);
        switch(tipo.toLowerCase()){
            case "promoción" -> idTipo = 1;
            case "transferencia" -> idTipo = 2;
            case "cambio de puesto" -> idTipo = 3;
            case "ascenso" -> idTipo = 4;
            case "reasignación" -> idTipo = 5;
        }
        
        try {
            conexion = DriverManager.getConnection(url, user, password);
            ps = conexion.prepareStatement("INSERT INTO RecursosHumanos.Movimiento_Personal (idEmpleado, tipoMovimiento, fecha, detalle) VALUES (?, ?, ?, ?)");
            ps.setInt(1, idEmpleado);
            ps.setInt(2, idTipo);
            ps.setString(3, fechaBD);
            ps.setString(4, detalle);
            
            int resp = ps.executeUpdate();
            
            if(resp > 0){
                conexion.close();
                System.out.println("Se ha agregado un movimiento");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return false;
        }
        return false;
    }
    
    //Metodo que valida si existe una consulta anterior, de lo contrario busca en base al ID de empleado y devuelve unicamente las acciones del empleado buscado
    public static ArrayList<MovimientoPersonal> consultarMovimientosEmpleado(int idEmpleado){
        ArrayList<MovimientoPersonal> resultado = new ArrayList<>();
        Connection conexion = null;
        if(!resultado.isEmpty()){
            resultado.clear();
        }
        try {
            conexion = DriverManager.getConnection(url, user, password);
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
                
                String fecha = convertirAFechaEstandar(fechaBD);
                
                MovimientoPersonal movimiento = new MovimientoPersonal(id, idEmp, tipo, fecha, detalle);
                resultado.add(movimiento);
            }
            conexion.close();
            return resultado;
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return null;
    }
    
    //Metodo que busca los movimientos entre dos fechas correspondientes y devuelve un arreglo de movimientos con todos los movimientos en esa fecha
    public static ArrayList<MovimientoPersonal> generarReporteMovimientos(String fechaI, String fechaF) throws ParseException {
        ArrayList<MovimientoPersonal> reporte = new ArrayList<>();
        Connection conexion = null;
        String fechaIBD  = convertirAFechaISO(fechaI);
        String fechaFBD = convertirAFechaISO(fechaF);
        
        try {
            conexion = DriverManager.getConnection(url, user, password);
            ps = conexion.prepareStatement(
                    "SELECT mp.id, mp.idEmpleado, cm.tipo AS tipoMovimiento, mp.fecha, mp.detalle " +
                    "FROM RecursosHumanos.Movimiento_Personal mp " +
                    "JOIN RecursosHumanos.Cat_Movimientos cm ON mp.tipoMovimiento = cm.id " +
                    "WHERE mp.fecha BETWEEN ? AND ?" +
                    "ORDER BY mp.fecha DESC");
            ps.setString(1, fechaIBD);
            ps.setString(2, fechaFBD);
            
            rs = ps.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                int idEmp = rs.getInt("idEmpleado");
                String tipo = rs.getString("tipoMovimiento");
                String fechaBD = rs.getString("fecha");
                String detalle = rs.getString("detalle");
                
                String fecha = convertirAFechaEstandar(fechaBD);
                
                MovimientoPersonal movimiento = new MovimientoPersonal(id, idEmp, tipo, fecha, detalle);
                reporte.add(movimiento);
            }
            conexion.close();
            return reporte;
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return null;
    }

    //Metodo que devuelve la fecha actual con formato dd/mm/aaaa
    public static String obtenerFechaActual() {
        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fechaActual.format(formatter);
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
}
