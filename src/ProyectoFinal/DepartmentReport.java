package ProyectoFinal;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * Department Report for the Human Resources System
 * Generates reports showing employee count by department
 *
 * @author Juan Samayoa
 */
public class DepartmentReport extends Report{

    private String departmentName;
    private int employeeCount;

    // Constructor
    public DepartmentReport(String departmentName, int employeeCount) {
        super("Employees by Department Report", "DEPARTMENT");
        this.departmentName = departmentName;
        this.employeeCount = employeeCount;
    }

    // Static method to generate department report
    public static ArrayList<DepartmentReport> generateDepartmentReport() {
        ArrayList<DepartmentReport> report = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DatabaseManager.getConnection();
            ps = connection.prepareStatement(
                "SELECT d.nombre AS departamento, COUNT(e.id) AS cantidadEmpleados " +
                "FROM RecursosHumanos.Cat_Departamentos d " +
                "LEFT JOIN RecursosHumanos.Empleado e ON d.id = e.idDepartamento " +
                "GROUP BY d.nombre " +
                "ORDER BY cantidadEmpleados DESC"
            );

            rs = ps.executeQuery();

            while(rs.next()){
                String departmentName = rs.getString("departamento");
                int employeeCount = rs.getInt("cantidadEmpleados");

                DepartmentReport reportItem = new DepartmentReport(departmentName, employeeCount);
                report.add(reportItem);
            }

        } catch (Exception e) {
            System.out.println("Error generating department report: " + e.getMessage());
        } finally {
            closeResources(connection);
        }

        return report;
    }

    @Override
    public ArrayList<DepartmentReport> generateReport() {
        return generateDepartmentReport();
    }

    @Override
    public String getReportSummary() {
        return String.format("Department Report - %s: %d employees", departmentName, employeeCount);
    }

    // Getters and Setters
    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(int employeeCount) {
        this.employeeCount = employeeCount;
    }

    @Override
    public String toString() {
        return String.format("DepartmentReport{department='%s', employeeCount=%d}",
                           departmentName, employeeCount);
    }
}
