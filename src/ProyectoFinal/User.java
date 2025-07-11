package ProyectoFinal;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * User class for the Human Resources System
 * Handles user management with enhanced validations and utilities
 *
 * @author Juan Samayoa
 */
public class User extends Person {

    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String userType;

    public static final ArrayList<User> users = new ArrayList<>();

    // Database connection variables
    private static PreparedStatement ps;
    private static ResultSet rs;

    // ================================
    // 1. CONSTRUCTOR
    // ================================

    /**
     * Constructor with parameters
     */
    public User(int id, String name, String fullName, String email, String phone, String password, String userType) {
        super(id, name);
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.userType = userType;
    }

    // ================================
    // 2. CRUD METHODS
    // ================================

    /**
     * Adds a new user to the database
     *
     * @param username username for the new user
     * @param fullName full name of the user
     * @param email email address
     * @param phone phone number
     * @param password password in plain text (will be hashed)
     * @param userType type of user (Administrator, Recruiter, Operator)
     * @return true if user was added successfully, false otherwise
     */
    public static boolean add(String username, String fullName, String email, String phone, String password, String userType) {
        // Validate input data using Utils
        List<String> errors = Utils.validateUserData(email, phone, username, fullName, password);
        if (!errors.isEmpty()) {
            System.out.println("Validation errors:");
            errors.forEach(System.out::println);
            return false;
        }

        // Validate user type
        if (!validateUserTypeExists(userType)) {
            System.out.println("Invalid user type: " + userType);
            return false;
        }

        // Check if username or email is already taken
        if (isUsernameOrEmailTaken(username, email)) {
            System.out.println("Username or email is already taken");
            return false;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            // Get user type ID
            int userTypeId = Utils.getUserTypeId(userType);
            if (userTypeId == 0) {
                System.out.println("User type not found: " + userType);
                return false;
            }

            // Hash password
            String hashedPassword = PasswordSecurity.hashPassword(password);

            // Insert user
            String sql = "INSERT INTO RecursosHumanos.Usuario (nombre, nombreCompleto, correo, telefono, contraseña, tipoUsuario) VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, Utils.cleanText(username));
            ps.setString(2, Utils.formatName(fullName));
            ps.setString(3, Utils.cleanText(email).toLowerCase());
            ps.setString(4, Utils.cleanText(phone));
            ps.setString(5, hashedPassword);
            ps.setInt(6, userTypeId);

            int result = ps.executeUpdate();

            if (result > 0) {
                System.out.println("User added successfully");
                return true;
            }

        } catch (Exception e) {
            System.out.println("Error adding user: " + e.getMessage());
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
     * Updates an existing user in the database
     *
     * @param id user ID to update
     * @param username new username
     * @param fullName new full name
     * @param email new email address
     * @param phone new phone number
     * @param password new password in plain text (will be hashed) - can be null to keep existing
     * @param userType new user type
     * @return true if user was updated successfully, false otherwise
     */
    public static boolean update(int id, String username, String fullName, String email, String phone, String password, String userType) {
        // Validate ID
        if (!Utils.validateId(id)) {
            System.out.println("Invalid user ID");
            return false;
        }

        // Check if user exists
        if (!userExists(id)) {
            System.out.println("User with ID " + id + " does not exist");
            return false;
        }

        // Validate input data (without password if null)
        List<String> errors = Utils.validateUserData(email, phone, username, fullName, password);
        if (!errors.isEmpty()) {
            System.out.println("Validation errors:");
            errors.forEach(System.out::println);
            return false;
        }

        // Validate user type
        if (!validateUserTypeExists(userType)) {
            System.out.println("Invalid user type: " + userType);
            return false;
        }

        // Check if username or email is taken by another user
        if (isUsernameOrEmailTakenForUpdate(id, username, email)) {
            System.out.println("Username or email is already taken by another user");
            return false;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            // Get user type ID
            int userTypeId = Utils.getUserTypeId(userType);
            if (userTypeId == 0) {
                System.out.println("User type not found: " + userType);
                return false;
            }

            String sql;
            if (password != null && !password.trim().isEmpty()) {
                // Update with new password
                String hashedPassword = PasswordSecurity.hashPassword(password);
                sql = "UPDATE RecursosHumanos.Usuario SET nombre = ?, nombreCompleto = ?, correo = ?, telefono = ?, contraseña = ?, tipoUsuario = ? WHERE id = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, Utils.cleanText(username));
                ps.setString(2, Utils.formatName(fullName));
                ps.setString(3, Utils.cleanText(email).toLowerCase());
                ps.setString(4, Utils.cleanText(phone));
                ps.setString(5, hashedPassword);
                ps.setInt(6, userTypeId);
                ps.setInt(7, id);
            } else {
                // Update without changing password
                sql = "UPDATE RecursosHumanos.Usuario SET nombre = ?, nombreCompleto = ?, correo = ?, telefono = ?, tipoUsuario = ? WHERE id = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, Utils.cleanText(username));
                ps.setString(2, Utils.formatName(fullName));
                ps.setString(3, Utils.cleanText(email).toLowerCase());
                ps.setString(4, Utils.cleanText(phone));
                ps.setInt(5, userTypeId);
                ps.setInt(6, id);
            }

            int result = ps.executeUpdate();

            if (result > 0) {
                System.out.println("User updated successfully");
                return true;
            }

        } catch (Exception e) {
            System.out.println("Error updating user: " + e.getMessage());
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
     * Deletes a user from the database
     *
     * @param id user ID to delete
     * @return true if user was deleted successfully, false otherwise
     */
    public static boolean delete(int id) {
        // Validate input
        if (!Utils.validateId(id)) {
            System.out.println("Invalid user ID");
            return false;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            // Check if user exists
            if (!userExists(id)) {
                System.out.println("User with ID " + id + " does not exist");
                return false;
            }

            // Delete user
            String sql = "DELETE FROM RecursosHumanos.Usuario WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int result = ps.executeUpdate();

            if (result > 0) {
                System.out.println("User deleted successfully");
                return true;
            }

        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
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
     * Gets all users from database
     */
    public static void getAll() {
        users.clear();

        try (Connection conn = DatabaseManager.getConnection()) {
            ps = conn.prepareStatement(
                    "SELECT u.id, u.nombre, u.nombreCompleto, u.correo, u.telefono, u.contraseña, tu.tipo AS tipoUsuario " +
                            "FROM RecursosHumanos.Usuario u " +
                            "JOIN RecursosHumanos.Cat_Usuario tu ON u.tipoUsuario = tu.id"
            );
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("nombre");
                String fullName = rs.getString("nombreCompleto");
                String email = rs.getString("correo");
                String phone = rs.getString("telefono");
                String password = rs.getString("contraseña");
                String userType = rs.getString("tipoUsuario");

                User user = new User(id, username, fullName, email, phone, password, userType);
                users.add(user);
            }

        } catch (Exception e) {
            System.out.println("Database error while retrieving users: " + e.getMessage());
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
     * Gets user information by ID and returns User object
     */
    public static User getUser(int id) {
        getAll();
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    /**
     * Gets next available ID for new user
     */
    public static int getNextId() {
        getAll();

        if (users.isEmpty()) {
            return 1;
        }

        int maxId = users.stream()
                .mapToInt(User::getId)
                .max()
                .orElse(0);

        return Utils.getNextId(maxId);
    }

    // ================================
    // 3. UTILS METHODS
    // ================================

    /**
     * Authenticates user with username and password
     */
    public static User authenticate(String username, String password) {
        // Validate input parameters first
        if (!Utils.validateNonEmptyText(username) || !Utils.validateNonEmptyText(password)) {
            System.out.println("Username and password cannot be empty");
            return null;
        }

        getAll();
        String passwordHash = PasswordSecurity.hashPassword(password);

        for (User user : users) {
            if (user.name.equals(Utils.cleanText(username)) && user.getPassword().equals(passwordHash)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Validates if user type exists using Utils
     */
    protected static boolean validateUserTypeExists(String userType) {
        return Utils.getUserTypeId(userType) > 0;
    }

    /**
     * Checks if username or email is already taken
     */
    protected static boolean isUsernameOrEmailTaken(String username, String email) {
        getAll();
        return users.stream().anyMatch(user ->
                user.getName().equalsIgnoreCase(Utils.cleanText(username)) ||
                        user.getEmail().equalsIgnoreCase(Utils.cleanText(email))
        );
    }

    /**
     * Checks if username or email is taken by another user (for updates)
     */
    protected static boolean isUsernameOrEmailTakenForUpdate(int currentUserId, String username, String email) {
        getAll();
        return users.stream()
                .filter(user -> user.getId() != currentUserId)
                .anyMatch(user ->
                        user.getName().equalsIgnoreCase(Utils.cleanText(username)) ||
                                user.getEmail().equalsIgnoreCase(Utils.cleanText(email))
                );
    }

    /**
     * Checks if user exists by ID
     */
    protected static boolean userExists(int id) {
        getAll();
        return users.stream().anyMatch(user -> user.getId() == id);
    }

    /**
     * Gets user type name by ID using Utils
     */
    public static String getUserTypeName(int id) {
        return Utils.getUserTypeName(id);
    }

    /**
     * Gets user type ID by name using Utils
     */
    public static int getUserTypeId(String userType) {
        return Utils.getUserTypeId(userType);
    }

    /**
     * Gets all available user types using Utils
     */
    public static java.util.Map<Integer, String> getAllUserTypes() {
        return Utils.getAllUserTypes();
    }

    // ================================
    // 4. OVERRIDE METHODS
    // ================================

    @Override
    public String toString() {
        return String.format(
                "User{id=%d, username='%s', fullName='%s', email='%s', phone='%s', userType='%s'}",
                id, name, fullName, email, phone, userType
        );
    }

    // ================================
    // 5. OTHER METHODS
    // ================================

    // Note: If there were any other specific methods that don't fit in previous categories, they would go here

    // ================================
    // 6. GETTERS AND SETTERS
    // ================================

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = Utils.capitalizeText(Utils.cleanText(fullName));
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Utils.cleanText(email).toLowerCase();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = Utils.cleanText(phone);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = PasswordSecurity.hashPassword(password);
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
