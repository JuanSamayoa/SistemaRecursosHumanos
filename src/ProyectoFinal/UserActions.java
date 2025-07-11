package ProyectoFinal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * User Actions Interface for Human Resources System
 * Defines CRUD operations for user management with enhanced validations
 *
 * @author Juan Samayoa
 */
public interface UserActions {

    /**
     * Adds a new user with enhanced modular validation
     */
    static boolean add(String username, String fullName, String email, String phone, String password, String userType) {
        // Use enhanced modular validation
        List<String> validationErrors = Utils.validateUserData(email, phone, username, fullName, password);

        if (!validationErrors.isEmpty()) {
            System.out.println("User validation failed:");
            for (int i = 0; i < validationErrors.size(); i++) {
                System.out.println((i + 1) + ". " + validationErrors.get(i));
            }
            return false;
        }

        // Business logic validations
        if (!User.validateUserTypeExists(userType)) {
            System.out.println("Invalid user type: " + userType + ". Valid types: Administrator, Recruiter, Operator");
            return false;
        }

        if (User.isUsernameOrEmailTaken(username, email)) {
            System.out.println("Username or email already exists in the system");
            return false;
        }

        // Prepare data for insertion
        int idUser = User.getNextId();
        String passwordHash = Utils.hashPassword(password);
        int userTypeId = Utils.getUserTypeId(userType);

        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO RecursosHumanos.Usuario (id, nombreUsuario, nombreCompleto, correoElectronico, telefono, contrasena, tipoUsuario) VALUES (?, ?, ?, ?, ?, ?, ?)"
            );

            // Clean and format data before insertion
            ps.setInt(1, idUser);
            ps.setString(2, Utils.cleanText(username));
            ps.setString(3, Utils.capitalizeText(Utils.cleanText(fullName)));
            ps.setString(4, Utils.cleanText(email).toLowerCase());
            ps.setString(5, Utils.cleanText(phone));
            ps.setString(6, passwordHash);
            ps.setInt(7, userTypeId);

            int result = ps.executeUpdate();

            if (result > 0) {
                User.users.clear(); // Refresh cache
                System.out.println("User created successfully: " + username);
                return true;
            }
        } catch (Exception e) {
            System.out.println("Database error while creating user: " + e.getMessage());
        }

        return false;
    }

    /**
     * Updates an existing user with enhanced validation
     */
    static boolean update(int id, String username, String fullName, String email, String phone, String password, String userType) {
        // Validate user exists first
        if (!User.userExists(id)) {
            System.out.println("User with ID " + id + " does not exist");
            return false;
        }

        // Use enhanced modular validation (password is optional for updates)
        List<String> validationErrors = Utils.validateUserData(email, phone, username, fullName, null);

        if (!validationErrors.isEmpty()) {
            System.out.println("User validation failed:");
            for (int i = 0; i < validationErrors.size(); i++) {
                System.out.println((i + 1) + ". " + validationErrors.get(i));
            }
            return false;
        }

        // Validate password separately if provided
        if (password != null && !password.trim().isEmpty()) {
            Utils.ValidationResult passwordResult = Utils.validatePasswordDetailed(password);
            if (!passwordResult.isValid()) {
                System.out.println("Password validation failed: " + passwordResult.getErrorMessage());
                return false;
            }
        }

        // Business logic validations
        if (!User.validateUserTypeExists(userType)) {
            System.out.println("Invalid user type: " + userType + ". Valid types: Administrator, Recruiter, Operator");
            return false;
        }

        if (User.isUsernameOrEmailTakenForUpdate(id, username, email)) {
            System.out.println("Username or email already exists for another user");
            return false;
        }

        // Prepare data for update
        int userTypeId = Utils.getUserTypeId(userType);
        String passwordHash = (password != null && !password.trim().isEmpty()) ?
                             Utils.hashPassword(password) : null;

        try (Connection conn = DatabaseManager.getConnection()) {
            StringBuilder queryBuilder = new StringBuilder(
                "UPDATE RecursosHumanos.Usuario SET nombreUsuario = ?, nombreCompleto = ?, " +
                "correoElectronico = ?, telefono = ?, tipoUsuario = ?"
            );

            if (passwordHash != null) {
                queryBuilder.append(", contrasena = ?");
            }

            queryBuilder.append(" WHERE id = ?");

            PreparedStatement ps = conn.prepareStatement(queryBuilder.toString());

            // Clean and format data before update
            ps.setString(1, Utils.cleanText(username));
            ps.setString(2, Utils.capitalizeText(Utils.cleanText(fullName)));
            ps.setString(3, Utils.cleanText(email).toLowerCase());
            ps.setString(4, Utils.cleanText(phone));
            ps.setInt(5, userTypeId);

            int paramIndex = 6;
            if (passwordHash != null) {
                ps.setString(paramIndex, passwordHash);
                paramIndex++;
            }
            ps.setInt(paramIndex, id);

            int result = ps.executeUpdate();

            if (result > 0) {
                User.users.clear(); // Refresh cache
                System.out.println("User updated successfully: " + username);
                return true;
            }
        } catch (Exception e) {
            System.out.println("Database error while updating user: " + e.getMessage());
        }

        return false;
    }

    /**
     * Deletes a user by ID
     */
    static boolean delete(int id) {
        if (!User.userExists(id)) {
            System.out.println("User with ID " + id + " does not exist");
            return false;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM RecursosHumanos.Usuario WHERE id = ?");
            ps.setInt(1, id);

            int result = ps.executeUpdate();

            if (result > 0) {
                User.users.clear(); // Refresh cache
                System.out.println("User deleted successfully");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Database error while deleting user: " + e.getMessage());
        }

        return false;
    }
}
