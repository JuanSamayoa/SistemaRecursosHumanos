package ProyectoFinal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class Empleado {
    
    public static final String user = "admindb";
    public static final String password = "root";
    public static final String url = "jdbc:sqlserver://localhost:1433;databaseName=SistemaRRHH;encrypt=true;trustServerCertificate=true";
    static PreparedStatement ps;
    static ResultSet rs;
    
    private int idEmpleado;
    private String nombre;
    private String apellido;
    private String fechaContratacion;
    private String cargo;
    private double salario;
    private String departamento;
    private static ArrayList<AccionPersonal> accionPersonal;
    private static ArrayList<MovimientoPersonal> movimientoPersonal;
    private static ArrayList<Contratacion> contrataciones;
    
    public static ArrayList<Empleado> empleados = new ArrayList<>();
    
    //Constructor con parametros
    public Empleado(int idEmpleado, String nombre, String apellido, String fechaContratacion, String cargo, double salario, String departamento) {
        this.idEmpleado = idEmpleado;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaContratacion = fechaContratacion;
        this.cargo = cargo;
        this.salario = salario;
        this.departamento = departamento;
        Empleado.accionPersonal = new ArrayList<>();
        Empleado.movimientoPersonal = new ArrayList<>();
        Empleado.contrataciones = new ArrayList<>();
    }
    
    //Constructor sin parametros
    public Empleado(){}

    //Obtener la informacion del empleado y devolver la clase Empleado para utilizar su información
    public static Empleado getInformacionEmpleado(int id) {
        for (Empleado empleado : empleados) {
            if (empleado.getIdEmpleado() == id) {
                return empleado;
            }
        }
        return null;
    }
    
    //RMétodo que realiza la modificación del usuario con los datos recibidos como parametros y los actualiza segun el usuario seleccionado.
    public static boolean modificarInformacionEmpleado(int id, String nom, String ape, String fecha, String car, double sal, String dep){
        Connection conexion = null;
        int idDep = 0;
        switch(dep.toLowerCase()){
                case "dirección ejecutiva" -> idDep = 1;
                case "secretaría" -> idDep = 2;
                case "unidad de auditoría interna" -> idDep = 3;
                case "recursos humanos" -> idDep = 4;
                case "tecnología de la información" -> idDep = 5;
                case "finanzas" -> idDep = 6;
                case "marketing" -> idDep = 7;
                case "operaciones" -> idDep = 8;
                case "ventas" -> idDep = 9;
                case "logística" -> idDep = 10;
            }
        
        System.out.println("Modificando informacion de empleado...");
        try {
            conexion = DriverManager.getConnection(url, user, password);
            ps = conexion.prepareStatement("UPDATE RecursosHumanos.Empleado SET nombre = ?, apellido = ?, fechaContratacion = ?, cargo = ?, salario = ?, idDepartamento = ? WHERE id = ?");
            ps.setString(1, nom);
            ps.setString(2, ape);
            String fechaCon = convertirAFechaISO(fecha);
            ps.setString(3, fechaCon);
            ps.setString(4, car);
            ps.setDouble(5, sal);
            ps.setInt(6, idDep);
            ps.setInt(7, id);
            
            int resp = ps.executeUpdate();
            
            if (resp > 0){
                empleados.clear();
                conexion.close();
                System.out.println("BD: Empleado modificado.");
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return false;
    }
    
    //Método que calcula las vacaciones del empleado, valida primero si hay empleados, si no lo obtiene
    public static int calcularVacaciones(int id) {
        System.out.println("Calculando vacaciones del empleado...");
        
        if(empleados == null || empleados.isEmpty()){
            obtenerTodosLosEmpleados();
        }
        
        //Buscar el empleado por el id
        for (Empleado empleado : empleados) {
            if (empleado.getIdEmpleado() == id) {
                String fechaContratacion = empleado.getFechaContratacion();
                
                //Obtener la fecha actual
                Date fechaHoy = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String fechaActual = formatter.format(fechaHoy);
                
                //Calcular la diferencia de años entre la fecha de contratacion y la fecha actual
                int anios = Integer.parseInt(fechaActual.substring(6)) - Integer.parseInt(fechaContratacion.substring(6));

                int vacaciones = 0;
                if (anios >= 1 && anios < 5) {
                    vacaciones = 15;
                } else if (anios >= 5 && anios < 10) {
                    vacaciones = 20;
                } else if (anios >= 10) {
                    vacaciones = 25;
                }
                return vacaciones;
            }
        }
        return 0;
    }
    
    //Método para obtener el siguiente ID de empleado disponible segun el arreglo de empleados
    public static int getNextIdEmpleado() {
        if (empleados.isEmpty()) {
            return 1;
        }
        int maxId = 0;
        for (Empleado empleado : empleados) {
            if (empleado.getIdEmpleado() > maxId) {
                maxId = empleado.getIdEmpleado();
            }
        }
        return maxId + 1;
    }

    // Método para agregar un nuevo empleado con su información
    public static boolean agregarEmpleado(String nombre, String apellido, String fechaContratacion, String cargo, double salario, String departamento) {
        int idEmpleado = getNextIdEmpleado();
        Connection conexion = null;
        int idDep = 0;
        switch(departamento.toLowerCase()){
                case "dirección ejecutiva" -> idDep = 1;
                case "secretaría" -> idDep = 2;
                case "unidad de auditoría interna" -> idDep = 3;
                case "recursos humanos" -> idDep = 4;
                case "tecnología de la información" -> idDep = 5;
                case "finanzas" -> idDep = 6;
                case "marketing" -> idDep = 7;
                case "operaciones" -> idDep = 8;
                case "ventas" -> idDep = 9;
                case "logística" -> idDep = 10;
            }
        try {
            conexion = DriverManager.getConnection(url, user, password);
            ps = conexion.prepareStatement("INSERT INTO RecursosHumanos.Empleado (id, nombre, apellido, fechaContratacion, cargo, salario, idDepartamento) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, idEmpleado);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            String fechaCon = convertirAFechaISO(fechaContratacion);
            ps.setString(4, fechaCon);
            ps.setString(5, cargo);
            ps.setDouble(6, salario);
            ps.setInt(7, idDep);
            
            int resp = ps.executeUpdate();
            
            if (resp > 0){
                empleados.clear();
                conexion.close();
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return false;
        }
        return false;
    }

    //Metodo para eliminar un empleado en base a su ID en la base de datos
    public static boolean eliminarEmpleado(int id){
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(url, user, password);
            ps = conexion.prepareStatement("DELETE FROM RecursosHumanos.Empleado WHERE id = ?");
            ps.setInt(1, id);
            
            int resp = ps.executeUpdate();
            
            if (resp > 0){
                empleados.clear();
                conexion.close();
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return false;
        }
        return false;
    }
    
    //Método que valida si existe informacion de empleados, si existe la borra, de lo contrario busca todos los empleados en la base de datos y lo agrega al arreglo de empleados
    public static void obtenerTodosLosEmpleados(){
        Connection conexion = null;
        
        if (!empleados.isEmpty()){
            empleados.clear();
        }
        
        System.out.println("Obteniendo todos los empleados.");
        
        try {
            conexion = DriverManager.getConnection(url, user, password);
            ps = conexion.prepareStatement(
                "SELECT e.id, e.nombre, e.apellido, e.fechaContratacion, e.cargo, e.salario, d.nombre AS departamento " +
                "FROM RecursosHumanos.Empleado e " +
                "JOIN RecursosHumanos.Cat_Departamentos d ON e.idDepartamento = d.id"
            );
            rs = ps.executeQuery();
            
            while(rs.next()){
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String fechac = rs.getString("fechaContratacion");
                String cargo = rs.getString("cargo");
                Double salario = rs.getDouble("salario");
                String departamento = rs.getString("departamento");
                String fechaConC = convertirAFechaEstandar(fechac);
                
                Empleado empleado = new Empleado(id, nombre, apellido, fechaConC, cargo, salario, departamento);
                empleados.add(empleado);
            }
            conexion.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conexion != null) conexion.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    public String getCargo() {
        return cargo;
    }

    public double getSalario() {
        return salario;
    }

    public String getDepartamento() {
        return departamento;
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

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    
    public static ArrayList<AccionPersonal> getAccionesPersonal(){
        return accionPersonal;
    }
    
    public void setAccionesPersonales(ArrayList<AccionPersonal> accionesPersonales) {
        this.accionPersonal = accionesPersonales;
    }
    
    public static ArrayList<MovimientoPersonal> getMovimientoPersonal(){
        return movimientoPersonal;
    }
    
    public void setMovimientosPersonales(ArrayList<MovimientoPersonal> movimientosPersonales) {
        this.movimientoPersonal = movimientosPersonales;
    }
    
    public static ArrayList<Contratacion> getContratacion(){
        return contrataciones;
    }
    
    public void setContrataciones(ArrayList<Contratacion> contrataciones) {
        this.contrataciones = contrataciones;
    }

    public static ArrayList<Empleado> getEmpleados() {
        return empleados;
    }
    
    public void setAccionesPersonal(ArrayList<AccionPersonal> accionesPersonal) {
        this.accionPersonal = accionesPersonal;
    }
}
