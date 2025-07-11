package ProyectoFinal;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Hiring class for the Human Resources System
 * Handles employee hiring and contract management independently
 *
 * @author Juan Samayoa
 * Carne: 9390-23-2010
 * Programación II
 */
public class Hiring {

    // Static variables for database operations
    static PreparedStatement ps;
    static ResultSet rs;

    private int id;
    private int employeeId;
    private String startDate;
    private String contractType;
    private int contractDuration;
    private double salary;
    public static ArrayList<Hiring> activeContracts = new ArrayList<>();
    
    // Constructor with parameters
    public Hiring(int id, int employeeId, String startDate, String contractType, int contractDuration, double salary) {
        this.id = id;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.contractType = contractType;
        this.contractDuration = contractDuration;
        this.salary = salary;
    }

    // Constructor without parameters
    public Hiring() {}

    // Method to create contracts based on employee ID and form data
    public boolean add(int employeeId, String startDate, String contractType, int contractDuration, double salary){
        int contractId = getNextIdContratacion();

        int contractTypeId = getContractIdByString(contractType);
        if (contractTypeId == -1) {
            System.out.println("Error getting next contract ID");
            return false;
        }

        try(Connection conn = DatabaseManager.getConnection()) {
            String dbDate = Utils.convertToISODate(startDate);

            ps = conn.prepareStatement("INSERT INTO RecursosHumanos.Contratacion (id, idEmpleado, fechaInicio, tipoContrato, duracionContrato, salario) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, contractId);
            ps.setInt(2, employeeId);
            ps.setString(3, dbDate);
            ps.setInt(4, contractTypeId);
            ps.setInt(5, contractDuration);
            ps.setDouble(6, salary);
            
            int resp = ps.executeUpdate();
            
            if(resp > 0){
                System.out.println("Contract created successfully");
                return true;
            }

        } catch (Exception e) {
            System.out.println("Error creating contract: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return false;
    }

    // Method to get next contract ID
    public static int getNextIdContratacion(){
        int maxId = 0;
        Connection conexion = null;
        
        try {
            conexion = DatabaseManager.getConnection();
            ps = conexion.prepareStatement("SELECT MAX(id) AS maxId FROM RecursosHumanos.Contratacion");
            rs = ps.executeQuery();
            
            if(rs.next()){
                maxId = rs.getInt("maxId");
            }

        } catch (Exception e) {
            System.out.println("Error getting next contract ID: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conexion != null) conexion.close();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return maxId + 1;
    }

    // Method to get active contracts
    public static ArrayList<Hiring> obtenerContratosActivos(){
        ArrayList<Hiring> contratos = new ArrayList<>();
        Connection conexion = null;

        try {
            conexion = DatabaseManager.getConnection();
            ps = conexion.prepareStatement(
                "SELECT c.id, c.idEmpleado, c.fechaInicio, tc.tipo AS tipoContrato, c.duracionContrato, c.salario " +
                "FROM RecursosHumanos.Contratacion c " +
                "JOIN RecursosHumanos.Cat_TipoContrato tc ON c.tipoContrato = tc.id " +
                "WHERE c.activo = 1"
            );

            rs = ps.executeQuery();

            while(rs.next()){
                int id = rs.getInt("id");
                int idEmp = rs.getInt("idEmpleado");
                String fechaBD = rs.getString("fechaInicio");
                String tipo = rs.getString("tipoContrato");
                int duracion = rs.getInt("duracionContrato");
                double salario = rs.getDouble("salario");

                String fechaFormatted = Utils.convertToStandardDate(fechaBD);

                Hiring contrato = new Hiring(id, idEmp, fechaFormatted, tipo, duracion, salario);
                contratos.add(contrato);
            }

        } catch (Exception e) {
            System.out.println("Error getting active contracts: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conexion != null) conexion.close();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }

        return contratos;
    }

    public static int getContractIdByString(String contractType) {
        return switch(contractType.toLowerCase()) {
            case "indefinido" -> 1;
            case "temporal" -> 2;
            case "prácticas" -> 3;
            case "proyecto" -> 4;
            default -> {
                System.out.println("Invalid contract type: " + contractType);
                yield -1; // Invalid contract type
            }
        };
    }

    // Additional methods for form compatibility
    public static boolean agregarContrato(int employeeId, String startDate, String contractType, int contractDuration, double salary) {
        Hiring hiring = new Hiring();
        return hiring.add(employeeId, startDate, contractType, contractDuration, salary);
    }

    public static boolean modificarContrato(int contractId, int employeeId, String startDate, String contractType, int contractDuration, double salary) {
        try (Connection conn = DatabaseManager.getConnection()) {
            int contractTypeId = getContractIdByString(contractType);
            if (contractTypeId == -1) {
                System.out.println("Invalid contract type: " + contractType);
                return false;
            }

            String dbDate = Utils.convertToISODate(startDate);

            ps = conn.prepareStatement("UPDATE RecursosHumanos.Contratacion SET idEmpleado = ?, fechaInicio = ?, tipoContrato = ?, duracionContrato = ?, salario = ? WHERE id = ?");
            ps.setInt(1, employeeId);
            ps.setString(2, dbDate);
            ps.setInt(3, contractTypeId);
            ps.setInt(4, contractDuration);
            ps.setDouble(5, salary);
            ps.setInt(6, contractId);

            int result = ps.executeUpdate();

            if (result > 0) {
                System.out.println("Contract updated successfully");
                return true;
            }

        } catch (Exception e) {
            System.out.println("Error updating contract: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return false;
    }

    public static boolean finalizarContrato(int contractId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            ps = conn.prepareStatement("UPDATE RecursosHumanos.Contratacion SET activo = 0 WHERE id = ?");
            ps.setInt(1, contractId);

            int result = ps.executeUpdate();

            if (result > 0) {
                System.out.println("Contract finalized successfully");
                return true;
            }

        } catch (Exception e) {
            System.out.println("Error finalizing contract: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Contratacion{" +
                "idContratacion=" + id +
                ", idEmpleado=" + employeeId +
                ", fechaInicio='" + startDate + '\'' +
                ", tipoContrato='" + contractType + '\'' +
                ", duracionContrato=" + contractDuration +
                ", salario=" + salary +
                '}';
    }

    // Getters and Setters
    public int getIdContratacion() {
        return id;
    }

    public void setIdContratacion(int idContratacion) {
        this.id = idContratacion;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public int getContractDuration() {
        return contractDuration;
    }

    public void setContractDuration(int contractDuration) {
        this.contractDuration = contractDuration;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public static ArrayList<Hiring> getActiveContracts() {
        return activeContracts;
    }
}
