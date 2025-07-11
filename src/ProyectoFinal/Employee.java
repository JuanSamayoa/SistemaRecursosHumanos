package ProyectoFinal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Employee class for the Human Resources System
 * Handles employee management with enhanced validations and utilities
 *
 * @author Juan Samayoa
 */
public class Employee extends Person {

    private String lastName;
    private String hiringDate;
    private String position;
    private double salary;
    private String department;

    // Static collections for related entities
    private static ArrayList<PersonalActions> personalActions = new ArrayList<>();
    private static ArrayList<PersonalMovement> personalMovements = new ArrayList<>();
    private static ArrayList<Hiring> hirings = new ArrayList<>();

    public static ArrayList<Employee> employees = new ArrayList<>();

    // Database connection variables
    private static PreparedStatement ps;
    private static ResultSet rs;

    // ================================
    // 1. CONSTRUCTOR
    // ================================

    /**
     * Constructor with parameters
     */
    public Employee(int id, String name, String lastName, String hiringDate, String position, double salary, String department) {
        super(id, name);
        this.lastName = lastName;
        this.hiringDate = hiringDate;
        this.position = position;
        this.salary = salary;
        this.department = department;
    }

    // ================================
    // 2. CRUD METHODS
    // ================================

    /**
     * Adds a new employee to the database
     *
     * @param name       employee's first name
     * @param lastName   employee's last name
     * @param hiringDate hiring date in dd/MM/yyyy format
     * @param position   employee's position
     * @param salary     employee's salary
     * @param department employee's department
     * @return true if employee was added successfully, false otherwise
     */
    public static boolean add(String name, String lastName, String hiringDate, String position, double salary, String department) {
        // Validate input data
        if (!validateEmployeeData(name, lastName, position, salary, department)) {
            return false;
        }

        if (!Utils.validateDate(hiringDate)) {
            System.out.println("Invalid hiring date format. Use dd/MM/yyyy");
            return false;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            // Get department ID
            int departmentId = getDepartmentId(department, conn);
            if (departmentId == -1) {
                System.out.println("Department not found: " + department);
                return false;
            }

            // Convert date format for database
            String isoDate = Utils.convertToISODate(hiringDate);

            // Insert employee
            String sql = "INSERT INTO RecursosHumanos.Empleado (nombre, apellido, fechaContratacion, cargo, salario, idDepartamento) VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, Utils.formatName(name));
            ps.setString(2, Utils.formatName(lastName));
            ps.setString(3, isoDate);
            ps.setString(4, Utils.formatName(position));
            ps.setDouble(5, salary);
            ps.setInt(6, departmentId);

            int result = ps.executeUpdate();

            if (result > 0) {
                System.out.println("Employee added successfully");
                return true;
            }

        } catch (Exception e) {
            System.out.println("Error adding employee: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (Exception e) {
                System.out.println("Error closing prepared statement: " + e.getMessage());
            }
        }

        return false;
    }

    /**
     * Updates an existing employee in the database
     *
     * @param id         employee ID to update
     * @param name       new first name
     * @param lastName   new last name
     * @param hiringDate new hiring date in dd/MM/yyyy format
     * @param position   new position
     * @param salary     new salary
     * @param department new department
     * @return true if employee was updated successfully, false otherwise
     */
    public static boolean update(int id, String name, String lastName, String hiringDate, String position, double salary, String department) {
        // Validate input data
        if (!Utils.validateId(id)) {
            System.out.println("Invalid employee ID");
            return false;
        }

        if (!validateEmployeeData(name, lastName, position, salary, department)) {
            return false;
        }

        if (!Utils.validateDate(hiringDate)) {
            System.out.println("Invalid hiring date format. Use dd/MM/yyyy");
            return false;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            // Check if employee exists
            if (!employeeExists(id)) {
                System.out.println("Employee with ID " + id + " does not exist");
                return false;
            }

            // Get department ID
            int departmentId = getDepartmentId(department, conn);
            if (departmentId == -1) {
                System.out.println("Department not found: " + department);
                return false;
            }

            // Convert date format for database
            String isoDate = Utils.convertToISODate(hiringDate);

            // Update employee
            String sql = "UPDATE RecursosHumanos.Empleado SET nombre = ?, apellido = ?, fechaContratacion = ?, cargo = ?, salario = ?, idDepartamento = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, Utils.formatName(name));
            ps.setString(2, Utils.formatName(lastName));
            ps.setString(3, isoDate);
            ps.setString(4, Utils.formatName(position));
            ps.setDouble(5, salary);
            ps.setInt(6, departmentId);
            ps.setInt(7, id);

            int result = ps.executeUpdate();

            if (result > 0) {
                System.out.println("Employee updated successfully");
                return true;
            }

        } catch (Exception e) {
            System.out.println("Error updating employee: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (Exception e) {
                System.out.println("Error closing prepared statement: " + e.getMessage());
            }
        }

        return false;
    }

    /**
     * Deletes an employee from the database
     *
     * @param id employee ID to delete
     * @return true if employee was deleted successfully, false otherwise
     */
    public static boolean delete(int id) {
        // Validate input
        if (!Utils.validateId(id)) {
            System.out.println("Invalid employee ID");
            return false;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            // Check if employee exists
            if (!employeeExists(id)) {
                System.out.println("Employee with ID " + id + " does not exist");
                return false;
            }

            // Delete employee
            String sql = "DELETE FROM RecursosHumanos.Empleado WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int result = ps.executeUpdate();

            if (result > 0) {
                System.out.println("Employee deleted successfully");
                return true;
            }

        } catch (Exception e) {
            System.out.println("Error deleting employee: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (Exception e) {
                System.out.println("Error closing prepared statement: " + e.getMessage());
            }
        }

        return false;
    }

    /**
     * Gets all employees from database
     */
    public static void getAll() {
        employees.clear();

        try (Connection conn = DatabaseManager.getConnection()) {
            ps = conn.prepareStatement(
                    "SELECT e.id, e.nombre, e.apellido, e.fechaContratacion, e.cargo, e.salario, d.nombre AS departamento " +
                            "FROM RecursosHumanos.Empleado e " +
                            "JOIN RecursosHumanos.Cat_Departamentos d ON e.idDepartamento = d.id"
            );
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("nombre");
                String lastName = rs.getString("apellido");
                String hiringDate = rs.getString("fechaContratacion");
                String position = rs.getString("cargo");
                double salary = rs.getDouble("salario");
                String department = rs.getString("departamento");

                Employee employee = new Employee(id, name, lastName, hiringDate, position, salary, department);
                employees.add(employee);
            }

        } catch (Exception e) {
            System.out.println("Database error while retrieving employees: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (Exception e) {
                System.out.println("Error closing database resources: " + e.getMessage());
            }
        }
    }

    /**
     * Gets employee information by ID and returns Employee object
     */
    public static Employee getEmployeeInformation(int id) {
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        return null;
    }

    /**
     * Gets next available ID for new employee
     */
    public static int getNextId() {
        getAll();

        if (employees.isEmpty()) {
            return 1;
        }

        int maxId = employees.stream()
                .mapToInt(Employee::getId)
                .max()
                .orElse(0);

        return Utils.getNextId(maxId);
    }

    /**
     * Helper method to get department ID from department name
     *
     * @param departmentName name of the department
     * @param conn           database connection
     * @return department ID or -1 if not found
     */
    private static int getDepartmentId(String departmentName, Connection conn) {
        try {
            String sql = "SELECT id FROM RecursosHumanos.Cat_Departamentos WHERE nombre = ?";
            PreparedStatement psDept = conn.prepareStatement(sql);
            psDept.setString(1, departmentName);
            ResultSet rsDept = psDept.executeQuery();

            if (rsDept.next()) {
                int id = rsDept.getInt("id");
                rsDept.close();
                psDept.close();
                return id;
            }

            rsDept.close();
            psDept.close();
        } catch (Exception e) {
            System.out.println("Error getting department ID: " + e.getMessage());
        }

        return -1;
    }

    // ================================
    // 3. UTILS METHODS
    // ================================

    /**
     * Gets full name of employee
     */
    public String getFullName() {
        return Utils.capitalizeText(this.name + " " + this.lastName);
    }

    /**
     * Checks if employee exists by ID
     */
    public static boolean employeeExists(int id) {
        getAll();
        return employees.stream().anyMatch(employee -> employee.getId() == id);
    }

    /**
     * Validates employee data
     */
    public static boolean validateEmployeeData(String name, String lastName, String position, double salary, String department) {
        if (!Utils.validateNonEmptyText(name) || !Utils.validateNonEmptyText(lastName)) {
            System.out.println("Name and last name cannot be empty");
            return false;
        }

        if (!Utils.validateNonEmptyText(position)) {
            System.out.println("Position cannot be empty");
            return false;
        }

        if (!Utils.validateSalary(salary)) {
            System.out.println("Salary must be greater than 0");
            return false;
        }

        if (!Utils.validateNonEmptyText(department)) {
            System.out.println("Department cannot be empty");
            return false;
        }

        return true;
    }

    /**
     * Calculates the vacation days for an employee based on years of service
     * <p>
     * Vacation policy:
     * - Less than 1 year: 0 days
     * - 1 year: 15 days
     * - 2-5 years: 20 days
     * - 6-10 years: 25 days
     * - More than 10 years: 30 days
     *
     * @return number of vacation days the employee is entitled to
     */
    public int calculateVacations() {
        try {
            // Calculate years of service using Utils
            int serviceYears = Utils.calculateServiceYears(this.hiringDate);

            // Vacation policy based on years of service
            if (serviceYears < 1) {
                return 0; // No vacation rights until completing 1 year
            } else if (serviceYears == 1) {
                return 15; // 15 days after completing 1 year
            } else if (serviceYears >= 2 && serviceYears <= 5) {
                return 20; // 20 days from 2 to 5 years
            } else if (serviceYears >= 6 && serviceYears <= 10) {
                return 25; // 25 days from 6 to 10 years
            } else {
                return 30; // 30 days for more than 10 years
            }

        } catch (Exception e) {
            System.out.println("Error calculating vacation for employee " + this.getId() + ": " + e.getMessage());
            return 0;
        }
    }

    /**
     * Static method to calculate vacation days for a specific employee by ID
     *
     * @param id employee ID
     * @return number of vacation days the employee is entitled to
     */
    public static int calculateVacations(int id) {
        Employee employee = getEmployeeInformation(id);
        if (employee != null) {
            return employee.calculateVacations();
        }
        return 0;
    }

    /**
     * Calculates pending vacation days considering already taken days
     *
     * @param daysTaken vacation days already used in the current year
     * @return remaining vacation days
     */
    public int calculatePendingVacations(int daysTaken) {
        int totalVacations = calculateVacations();
        int pending = totalVacations - daysTaken;
        return Math.max(0, pending); // Cannot be negative
    }

    /**
     * Gets detailed information about the employee's vacation status
     *
     * @param daysTaken days already used (optional, default 0)
     * @return String with complete vacation information
     */
    public String getVacationInformation(int daysTaken) {
        int serviceYears = Utils.calculateServiceYears(this.hiringDate);
        int totalVacations = calculateVacations();
        int pending = calculatePendingVacations(daysTaken);

        StringBuilder info = new StringBuilder();
        info.append("=== VACATION INFORMATION ===\n");
        info.append("Employee: ").append(this.getName()).append(" ").append(this.lastName).append("\n");
        info.append("Hiring Date: ").append(this.hiringDate).append("\n");
        info.append("Years of Service: ").append(serviceYears).append(" year(s)\n");
        info.append("Total Vacation Days: ").append(totalVacations).append(" days\n");
        info.append("Days Taken: ").append(daysTaken).append(" days\n");
        info.append("Pending Days: ").append(pending).append(" days\n");

        if (serviceYears < 1) {
            info.append("NOTE: Employee has not yet completed one year of service.\n");
        }

        return info.toString();
    }

    /**
     * Overloaded method with daysTaken = 0
     */
    public String getVacationInformation() {
        return getVacationInformation(0);
    }

    // Additional methods for form compatibility
    public static void obtenerTodosLosEmpleados() {
        getAll();
    }

    public static Employee getInformacionEmpleado(int id) {
        return getEmployeeInformation(id);
    }

    // Alias methods for compatibility with forms
    public String getNombre() {
        return getName();
    }

    public String getApellido() {
        return getLastName();
    }

    public String getCargo() {
        return getPosition();
    }

    public String getDepartamento() {
        return getDepartment();
    }

    public String getFechaContratacion() {
        return getHiringDate();
    }

    public int getIdEmpleado() {
        return getId();
    }

    // Method for modifying employee information (for form compatibility)
    public boolean modificarInformacionEmpleado(int id, String name, String lastName, String hiringDate, String position, double salary, String department) {
        return Employee.update(id, name, lastName, hiringDate, position, salary, department);
    }

    // ================================
    // 4. OVERRIDE METHODS
    // ================================

    @Override
    public String toString() {
        return String.format(
                "Employee{id=%d, name='%s', lastName='%s', hiringDate='%s', position='%s', salary=%.2f, department='%s'}",
                id, name, lastName, hiringDate, position, salary, department
        );
    }

    // ================================
    // 5. OTHER METHODS
    // ================================

    // Note: If there were any other specific methods that don't fit in previous categories, they would go here

    // ================================
    // 6. GETTERS AND SETTERS
    // ================================

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = Utils.capitalizeText(Utils.cleanText(lastName));
    }

    public String getHiringDate() {
        return hiringDate;
    }

    public void setHiringDate(String hiringDate) {
        this.hiringDate = hiringDate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = Utils.capitalizeText(Utils.cleanText(position));
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public static ArrayList<PersonalActions> getPersonalActions() {
        return personalActions;
    }

    public static ArrayList<PersonalMovement> getPersonalMovements() {
        return personalMovements;
    }

    public static ArrayList<Hiring> getHirings() {
        return hirings;
    }

    public static ArrayList<Employee> getEmployees() {
        return employees;
    }
}
