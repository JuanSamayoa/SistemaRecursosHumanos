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
public class Contratacion {
    
    public static final String user = "admindb";
    public static final String password = "root";
    public static final String url = "jdbc:sqlserver://localhost:1433;databaseName=SistemaRRHH;encrypt=true;trustServerCertificate=true";
    static PreparedStatement ps;
    static ResultSet rs;
    
    private int idContratacion;
    private int idEmpleado;
    private String fechaInicio;
    private String tipoContrato;
    private int duracionContrato;
    private double salario;
    public static ArrayList<Contratacion> contratosActivos = new ArrayList<>();
    
    //Constructor con parametros
    public Contratacion(int idContratacion, int idEmpleado, String fechaInicio, String tipoContrato, int duracionContrato, double salario) {
        this.idContratacion = idContratacion;
        this.idEmpleado = idEmpleado;
        this.fechaInicio = fechaInicio;
        this.tipoContrato = tipoContrato;
        this.duracionContrato = duracionContrato;
        this.salario = salario;
    }

    //Constructor sin parametros
    public Contratacion() {}

    //Metodo para crear contratos en base a ID del empleado y datos solicitados en el formulario
    public boolean crearContrato(int idE, String fechaIn, String tipoCon, int dur, double sal){
        int idContrato = getNextIdContratacion();
        
        Connection conexion = null;
        int idCont = 0;
        switch(tipoCon.toLowerCase()){
            case "indefinido" -> idCont = 1;
            case "definido" -> idCont = 2;
            case "por obra" -> idCont = 3;
            case "por servicios" -> idCont = 4;
        }
        
        try {
            conexion = DriverManager.getConnection(url, user, password);
            ps = conexion.prepareStatement("INSERT INTO RecursosHumanos.Empleado (id, idEmpleado, fechaInicio, tipoContrato, duracion, salario) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, idContrato);
            ps.setInt(2, idE);
            String fechaBD = convertirAFechaISO(fechaIn);
            ps.setString(3, fechaBD);
            ps.setInt(4, idCont);
            ps.setInt(5, dur);
            ps.setDouble(6, sal);
            
            int resp = ps.executeUpdate();
            
            if(resp > 0){
                contratosActivos.clear();
                conexion.close();
                System.out.println("Se ha generado un contrato");
                return true;
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return false;
        }
        return false;
    }
    
    //Metodo que busca un contrato en base a su ID y lo elimina en la base de datos
    public static boolean finalizarContrato(int idContrato){   
        Connection conexion = null;
                
        try {
            conexion = DriverManager.getConnection(url, user, password);
            ps =  conexion.prepareStatement("DELETE FROM RecursosHumanos.Contratacion WHERE id = ?");
            ps.setInt(1, idContrato);
            
            int resp = ps.executeUpdate();
            System.out.println("Eliminacion: "+resp);
            if(resp > 0){
                System.out.println("Se ha eliminado el registro del contrato");
                // Recorrer la lista de contratos activos y eliminar el contrato con el ID especificado
                for (Contratacion contrato : contratosActivos) {
                    if (contrato.getIdContratacion() == idContrato) {
                        contratosActivos.remove(contrato);
                        conexion.close();
                        System.out.println("Contrato finalizado exitosamente: ID " + idContrato);
                        return true;
                    }
                }
                conexion.close();
                return true;
            }            
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return false;
        }
        return false;
    }
    
    //Metodo que valida si existe un arreglo de contrato activos, si existe lo vacia y obtiene todos los contratos activos para agregarlos al arreglo de contratos
    public static ArrayList<Contratacion> consultarContratosVigentes(){
        Connection conexion = null;
        
        if(!contratosActivos.isEmpty()){
            contratosActivos.clear();
        }
        
        System.out.println("Obteniendo los contractos activos...");
        
        try {
            conexion = DriverManager.getConnection(url, user, password);
            ps = conexion.prepareStatement(
                "SELECT c.id, c.idEmpleado, c.fechaInicio, cc.tipo AS tipoContrato, c.duracion, c.salario " +
                "FROM RecursosHumanos.Contratacion c " +
                "JOIN RecursosHumanos.Cat_Contrato cc ON c.tipoContrato = cc.id");
            rs = ps.executeQuery();
            
            while(rs.next()){
                int id = rs.getInt("id");
                int idEmp = rs.getInt("idEmpleado");
                String fechaBD = rs.getString("fechaInicio");
                String tipoCont = rs.getString("tipoContrato");
                int duracion = rs.getInt("duracion");
                double salario = rs.getDouble("salario");
                
                String fechaIniC = convertirAFechaEstandar(fechaBD);
                
                Contratacion contracion = new Contratacion(id, idEmp, fechaIniC, tipoCont, duracion, salario);
                contratosActivos.add(contracion);
            }
            conexion.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conexion != null) conexion.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getContrataciones();
    }
    
    // Método para obtener el siguiente ID de contrato disponible segun el arreglo de Contratos
    public static int getNextIdContratacion() {
        if (contratosActivos.isEmpty()) {
            return 1;
        }
        int maxId = 0;
        for (Contratacion contrato : contratosActivos) {
            if (contrato.getIdContratacion() > maxId) {
                maxId = contrato.getIdContratacion();
            }
        }
        return maxId + 1;
    }

    // Método para agregar un nuevo contrato seegun el ID e información del empleado y lo agrega a la base de datos
    public static boolean agregarContrato(int idEmpleado, String fechaInicio, String tipoContrato, int duracionContrato, double salario) {
        int newId = getNextIdContratacion();
        Connection conexion = null;
        int idCont = 0;
        switch(tipoContrato.toLowerCase()){
            case "contrato por tiempo indefinido" -> idCont = 1;
            case "contrato por tiempo definido" -> idCont = 2;
            case "contrato por obra" -> idCont = 3;
            case "contrato por servicios" -> idCont = 4;
        }
        if(idCont < 1 && idCont > 4){
            System.out.println("No se ha podido obtener el tipo de contrato");
            return false;
        }
        
        try {
            conexion = DriverManager.getConnection(url, user, password);
            ps = conexion.prepareStatement("INSERT INTO RecursosHumanos.Contratacion (id, idEmpleado, fechaInicio, tipoContrato, duracion, salario) VALUES (?,?,?,?,?,?)");
            ps.setInt(1, newId);
            ps.setInt(2, idEmpleado);
            String fechaBD = convertirAFechaISO(fechaInicio);
            ps.setString(3, fechaBD);
            ps.setInt(4, idCont);
            ps.setInt(5, duracionContrato);
            ps.setDouble(6, salario);
            
            int resp = ps.executeUpdate();
            if(resp > 0){
                contratosActivos.clear();
                conexion.close();
                System.out.println("Se ha agregado un nuevo contrato");
                return true;
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return false;
        }
        return false;
    }
    
    //Metodo que busca en base al ID del contrato para actualizar los datos en la base de datos
    public static boolean modificarContrato(int id, int idEmpleado, String fechaInicio, String tipoContrato, int duracionContrato, double salario) throws ParseException{
        Connection conexion = null;
        int idCont = 0;
        String fechaBD = convertirAFechaISO(fechaInicio);
         switch(tipoContrato.toLowerCase()){
            case "contrato por tiempo indefinido" -> idCont = 1;
            case "contrato por tiempo definido" -> idCont = 2;
            case "contrato por obra" -> idCont = 3;
            case "contrato por servicios" -> idCont = 4;
        }
         
         try {
            conexion = DriverManager.getConnection(url, user, password);
            ps = conexion.prepareStatement("UPDATE RecursosHumanos.Contratacion SET idEmpleado = ?, fechaInicio = ?, tipoContrato = ?, duracion = ?, salario = ? WHERE id = ?");
            ps.setInt(1, idEmpleado);
            ps.setString(2, fechaBD);
            ps.setInt(3, idCont);
            ps.setInt(4, duracionContrato);
            ps.setDouble(5, salario);
            ps.setInt(6, id);
            
            int resp = ps.executeUpdate();
            
            if(resp > 0){
                contratosActivos.clear();
                conexion.close();
                System.out.println("BD: Contrato modificado.");
                return true;
            }
        } catch (Exception e) {
             System.out.println("Error: " + e);
             return false;
        }
        return false;
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
    public int getIdContratacion() {
        return this.idContratacion;
    }

    public void setIdContratacion(int idContratacion) {
        this.idContratacion = idContratacion;
    }

    public int getIdEmpleado() {
        return this.idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getFechaInicio() {
        return this.fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getTipoContrato() {
        return this.tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public int getDuracionContrato() {
        return this.duracionContrato;
    }

    public void setDuracionContrato(int duracionContrato) {
        this.duracionContrato = duracionContrato;
    }

    public double getSalario() {
        return this.salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }
    
    public static ArrayList<Contratacion> getContrataciones(){
        return contratosActivos;
    }

}
