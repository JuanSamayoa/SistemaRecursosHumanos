package ProyectoFinal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
public class Reportes {
    
    public static final String user = "admindb";
    public static final String password = "root";
    public static final String url = "jdbc:sqlserver://localhost:1433;databaseName=SistemaRRHH;encrypt=true;trustServerCertificate=true";
    static PreparedStatement ps;
    static ResultSet rs;
    
    
    //Parametros generarReporteContratacionesRecientes()
    private int idCont;
    private int idEmpleado;
    private String nombre;
    private String apellido;
    private String fechaContratacion;
    private String fechaInicio;
    private String tipoContrato;
    private int duracion;
    private double salario;
    //Parametros de generarReporteEmpleadosPorDepartamento()
    private String departamento;
    private int cantidadEmpleados;
    //Parametros generarReporteAusenciasVacaciones()
    private int idAcc;
    private String tipoAccion;
    private String fechaAccion;
    private String detalleAccion;
    //Parametros generarReporteMovimientosPersonal()
    private int idMov;
    private String tipoMovimiento;
    private String fechaMovimiento;
    private String detalleMovimiento;

    public static ArrayList<Reportes> listaReportes = new ArrayList<>();
    
    //Constructor para generarReporteContratacionesRecientes()
    public Reportes( int idEmp, String nom, String ape, String fechaC, String fechaI, String tipoC, int dur, double sal){
        this.idEmpleado = idEmp;
        this.nombre = nom;
        this.apellido = ape;
        this.fechaContratacion = fechaC;
        this.fechaInicio = fechaI;
        this.tipoContrato = tipoC;
        this.duracion = dur;
        this.salario = sal;
    }

    //Constructor para generarReporteEmpleadosPorDepartamento()
    public Reportes(String dep, int cantEmp){
        this.departamento = dep;
        this.cantidadEmpleados = cantEmp;
    }

    //Constructor para generarReporteAusenciasVacaciones()
    public Reportes(int idEmp, String nom, String ape, String fechaA, String tipoA, String det){
        this.idEmpleado = idEmp;
        this.nombre = nom;
        this.apellido = ape;
        this.fechaAccion = fechaA;
        this.tipoAccion = tipoA;
        this.detalleAccion = det;
    }

    //Constructor para generarReporteMovimientosPersonal()
    public Reportes(int idMov, String nom, int idEmp, String ape, String fechaM, String tipoM, String det){
        this.idMov = idMov;
        this.idEmpleado = idEmp;
        this.nombre = nom;
        this.apellido = ape;
        this.fechaMovimiento = fechaM;
        this.tipoMovimiento = tipoM;
        this.detalleMovimiento = det;
    }
    
    private static Connection connectDatabase(){
        Connection conexion = null;
        
        try {
            String cadena = "jdbc:sqlserver://localhost:1433;databaseName=SistemaRRHH;encrypt=true;trustServerCertificate=true";
            conexion = DriverManager.getConnection(cadena, user, password);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al conectar a la base de datos: " + e.toString());
            System.out.println(e);
            System.exit(0);
        }
        return conexion;
    }
    
    public static ArrayList<Reportes> generarReporteEmpleadosPorDepartamento(){
        //Validar que no hayan datos en el arreglo
        if(listaReportes == null){
            listaReportes = new ArrayList<>();
        }
        if(!listaReportes.isEmpty()){
            listaReportes.clear();
        }
        try {
            Connection conexion = connectDatabase();
            String query = "SELECT d.nombre AS Departamento, COUNT(e.id) AS CantidadEmpleados " +
                                "FROM RecursosHumanos.Cat_Departamentos d " + 
                                "LEFT JOIN RecursosHumanos.Empleado e ON d.id = e.idDepartamento " + 
                                "GROUP BY d.nombre " + 
                                "ORDER BY d.nombre";
            ps = conexion.prepareStatement(query);
            rs = ps.executeQuery();
            while(rs.next()){
                String dep = rs.getString("Departamento");
                int cantEmp = rs.getInt("CantidadEmpleados");
                Reportes reporte = new Reportes(dep, cantEmp);
                listaReportes.add(reporte);
            }
            return listaReportes;
        } catch (Exception e) {
            System.out.println("Error al generar reporte de empleados por departamento: " + e.toString());
        }
        return null;
    }
    
    public static ArrayList<Reportes> generarReporteMovimientosPersonal(){
        //Validar que no hayan datos en el arreglo
        if(listaReportes == null){
            listaReportes = new ArrayList<>();
        }
        if(!listaReportes.isEmpty()){
            listaReportes.clear();
        }
        System.out.println("Consultado movimientos");
        try {
            Connection conexion = connectDatabase();
            String query = "SELECT mp.id AS IdMovimiento, e.id AS IdEmpleado, e.nombre AS Nombre, e.apellido AS Apellido, cm.tipo AS TipoMovimiento, mp.fecha AS Fecha, mp.detalle AS Detalle " +
                "FROM RecursosHumanos.Movimiento_Personal mp " + 
                "JOIN RecursosHumanos.Empleado e ON mp.idEmpleado = e.id " +
                "JOIN RecursosHumanos.Cat_Movimientos cm ON mp.tipoMovimiento = cm.id " +
                "ORDER BY mp.fecha DESC";
            ps = conexion.prepareStatement(query);
            rs = ps.executeQuery();
            while(rs.next()){
                int idMov = rs.getInt("IdMovimiento");
                int idEmp = rs.getInt("IdEmpleado");
                String nom = rs.getString("Nombre");
                String ape = rs.getString("Apellido");
                String tipoM = rs.getString("TipoMovimiento");
                String fechaM = rs.getString("Fecha");
                String det = rs.getString("Detalle");
                
                String fechaME = convertirAFechaEstandar(fechaM);

                Reportes reporte = new Reportes(idMov, nom, idEmp, ape, fechaME, tipoM, det);
                listaReportes.add(reporte);
            }
            conexion.close();
            return listaReportes;
            
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return null;
        
    }
    
    public static ArrayList<Reportes> generarReporteContratacionesRecientes(){
        //Validar que no hayan datos en el arreglo
        if(listaReportes == null){
            listaReportes = new ArrayList<>();
        }
        if(!listaReportes.isEmpty()){
            listaReportes.clear();
        }
        System.out.println("Consultado contrataciones recientes...");
        try {
            Connection conexion = connectDatabase();
            String query = "SELECT e.id AS IdEmpleado, e.nombre AS Nombre, e.apellido AS Apellido, e.fechaContratacion AS FechaContratacion, c.fechaInicio AS FechaInicio, ct.tipo AS TipoContrato, c.duracion AS Duracion, c.salario AS Salario " +
                    "FROM RecursosHumanos.Contratacion c " +
                    "JOIN RecursosHumanos.Empleado e ON c.idEmpleado = e.id " +
                    "JOIN RecursosHumanos.Cat_Contrato ct ON c.tipoContrato = ct.id " + 
                    "ORDER BY c.fechaInicio DESC";
            ps = conexion.prepareStatement(query);
            rs = ps.executeQuery();
            while(rs.next()){
                int idEmp = rs.getInt("IdEmpleado");
                String nom = rs.getString("Nombre");
                String ape = rs.getString("Apellido");
                String fechaC = rs.getString("FechaContratacion");
                String fechaI = rs.getString("FechaInicio");
                String tipoC = rs.getString("TipoContrato");
                int dur = rs.getInt("Duracion");
                double sal = rs.getDouble("Salario");
                
                String fechaCE = convertirAFechaEstandar(fechaC);
                String fechaIE = convertirAFechaEstandar(fechaI);
                
                Reportes reporte = new Reportes(idEmp, nom, ape, fechaCE, fechaIE, tipoC, dur, sal);
                listaReportes.add(reporte);
            }
            conexion.close();
            return listaReportes;
        } catch (Exception e) {
            System.out.println("Error al generar reporte de contrataciones recientes: " + e.toString());
        }
        return null;
    }
    
    public static ArrayList<Reportes> generarReporteAusenciasVacaciones(){
        //Validar que no hayan datos en el arreglo
        if(listaReportes == null){
            listaReportes = new ArrayList<>();
        }
        if(!listaReportes.isEmpty()){
            listaReportes.clear();
        }
        try {
            Connection conexion = connectDatabase();
            String query = "SELECT e.id AS IdEmpleado, e.nombre AS Nombre, e.apellido AS Apellido, a.fecha AS Fecha, ca.tipo AS TipoAccion, a.detalle AS Detalle " +
                    "FROM RecursosHumanos.Accion_Personal a " +
                    "JOIN RecursosHumanos.Empleado e ON a.idEmpleado = e.id " +
                    "JOIN RecursosHumanos.Cat_Acciones ca ON a.tipoAccion = ca.id " +
                    "ORDER BY a.fecha DESC;";
            ps = conexion.prepareStatement(query);
            rs = ps.executeQuery();
            while(rs.next()){
                int idEmp = rs.getInt("IdEmpleado");
                String nom = rs.getString("Nombre");
                String ape = rs.getString("Apellido");
                String fechaA = rs.getString("Fecha");
                String tipoA = rs.getString("TipoAccion");
                String det = rs.getString("Detalle");
                
                String fechaAE = convertirAFechaEstandar(fechaA);
                Reportes reporte = new Reportes(idEmp, nom, ape, fechaAE, tipoA, det);
                listaReportes.add(reporte);
            }
            conexion.close();
            return listaReportes;
        } catch (Exception e) {
            System.out.println("Error al generar reporte de ausencias y vacaciones: " + e.toString());
        }
        return null;
    }

    public static String convertirAFechaEstandar(String fecha) throws ParseException {
        SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoObjetivo = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaObj = formatoOriginal.parse(fecha);
        return formatoObjetivo.format(fechaObj);
    }
    //Getters y Setters

    public int getIdCont() {
        return idCont;
    }

    public int getIdAcc() {
        return idAcc;
    }

    public int getIdMov() {
        return idMov;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getFechaContratacion() {
        return fechaContratacion;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public int getDuracion() {
        return duracion;
    }

    public double getSalario() {
        return salario;
    }

    public String getDepartamento() {
        return departamento;
    }

    public int getCantidadEmpleados() {
        return cantidadEmpleados;
    }

    public String getTipoAccion() {
        return tipoAccion;
    }

    public String getFechaAccion() {
        return fechaAccion;
    }

    public String getDetalleAccion() {
        return detalleAccion;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public String getFechaMovimiento() {
        return fechaMovimiento;
    }

    public String getDetalleMovimiento() {
        return detalleMovimiento;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setFechaContratacion(String fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public void setCantidadEmpleados(int cantidadEmpleados) {
        this.cantidadEmpleados = cantidadEmpleados;
    }

    public void setTipoAccion(String tipoAccion) {
        this.tipoAccion = tipoAccion;
    }

    public void setFechaAccion(String fechaAccion) {
        this.fechaAccion = fechaAccion;
    }

    public void setDetalleAccion(String detalleAccion) {
        this.detalleAccion = detalleAccion;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public void setFechaMovimiento(String fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public void setDetalleMovimiento(String detalleMovimiento) {
        this.detalleMovimiento = detalleMovimiento;
    }

    public void setIdCont(int idCont) {
        this.idCont = idCont;
    }

    public void setIdAcc(int idAcc) {
        this.idAcc = idAcc;
    }

    public void setIdMov(int idMov) {
        this.idMov = idMov;
    }
}
