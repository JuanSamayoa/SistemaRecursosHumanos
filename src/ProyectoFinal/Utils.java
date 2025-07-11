package ProyectoFinal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utilities class for the Human Resources System
 * Organized into General Utilities and Class-Specific Utilities
 *
 * @author Juan Samayoa
 */
public class Utils {

    // ================================
    // CONSTANTS AND STATIC VARIABLES
    // ================================

    // Standard date formats
    private static final SimpleDateFormat STANDARD_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateTimeFormatter LOCAL_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Regular expressions for enhanced validations
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_GT_REGEX = "^[0-9]{8}$";
    private static final String NAMES_REGEX = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$";
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,20}$";
    private static final String SECURE_PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    // Compiled patterns for better performance
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_GT_REGEX);
    private static final Pattern NAMES_PATTERN = Pattern.compile(NAMES_REGEX);
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(SECURE_PASSWORD_REGEX);

    // Error message constants
    public static final String INVALID_EMAIL_ERROR = "Invalid email address. Must have format user@domain.com";
    public static final String INVALID_PHONE_ERROR = "Invalid phone number. Must have exactly 8 digits";
    public static final String INVALID_USERNAME_ERROR = "Invalid username. Must have 3-20 alphanumeric characters";
    public static final String INVALID_NAME_ERROR = "Invalid name. Only letters, spaces and accents allowed (2-50 characters)";
    public static final String INSECURE_PASSWORD_ERROR = "Insecure password. Must have at least 8 characters, 1 uppercase, 1 lowercase, 1 number and 1 special character";
    public static final String EMPTY_FIELD_ERROR = "Field cannot be empty";

    // Cache for user types
    private static final Map<Integer, String> USER_TYPES = new ConcurrentHashMap<>();
    private static final Map<String, Integer> USER_TYPE_IDS = new ConcurrentHashMap<>();

    static {
        // Initialize user types cache
        USER_TYPES.put(1, "Administrator");
        USER_TYPES.put(2, "Recruiter");
        USER_TYPES.put(3, "Operator");

        USER_TYPE_IDS.put("administrator", 1);
        USER_TYPE_IDS.put("recruiter", 2);
        USER_TYPE_IDS.put("operator", 3);
    }

    private Utils() {}

    // ========================================================================
    // GENERAL UTILITIES - Common methods for any application
    // ========================================================================

    // ================================
    // VALIDATION RESULT CLASS
    // ================================

    public static class ValidationResult {
        private final boolean isValid;
        private final String errorMessage;

        public ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult failure(String errorMessage) {
            return new ValidationResult(false, errorMessage);
        }
    }

    // ================================
    // FUNCTIONAL VALIDATION INTERFACE
    // ================================

    @FunctionalInterface
    public interface ValidationFunction {
        ValidationResult validate(String value);
    }

    // ================================
    // BASIC VALIDATION METHODS
    // ================================

    /**
     * Validates that text is not null or empty
     */
    public static boolean validateNonEmptyText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Validates salary is positive
     */
    public static boolean validateSalary(double salary) {
        return salary > 0;
    }

    /**
     * Validates numeric range
     */
    public static boolean validateNumericRange(double number, double min, double max) {
        return number >= min && number <= max;
    }

    /**
     * Checks if text is numeric
     */
    public static boolean isNumeric(String text) {
        return text != null && !text.trim().isEmpty() && text.trim().matches("\\d+");
    }

    /**
     * Checks if text is valid decimal
     */
    public static boolean isValidDecimal(String text) {
        return text != null && !text.trim().isEmpty() && text.trim().matches("\\d+(\\.\\d+)?");
    }

    // ================================
    // DATE UTILITIES
    // ================================

    /**
     * Validates date format dd/MM/yyyy
     */
    public static boolean validateDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }

        try {
            String[] dateArray = date.split("/");
            if (dateArray.length != 3) {
                return false;
            }

            int day = Integer.parseInt(dateArray[0]);
            int month = Integer.parseInt(dateArray[1]);
            int year = Integer.parseInt(dateArray[2]);

            if (day < 1 || day > 31 || month < 1 || month > 12 || year < 1900 || year > 2100) {
                return false;
            }

            LocalDate.of(year, month, day);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates that start date is before or equal to end date
     */
    public static boolean validateDateRange(String startDate, String endDate) {
        if (!validateDate(startDate) || !validateDate(endDate)) {
            return false;
        }

        try {
            Date start = STANDARD_FORMAT.parse(startDate);
            Date end = STANDARD_FORMAT.parse(endDate);
            return !start.after(end);
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Converts date from dd/MM/yyyy to yyyy-MM-dd
     */
    public static String convertToISODate(String date) throws ParseException {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be null or empty");
        }
        Date dateObj = STANDARD_FORMAT.parse(date);
        return ISO_FORMAT.format(dateObj);
    }

    /**
     * Converts date from yyyy-MM-dd to dd/MM/yyyy
     */
    public static String convertToStandardDate(String date) throws ParseException {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be null or empty");
        }
        Date dateObj = ISO_FORMAT.parse(date);
        return STANDARD_FORMAT.format(dateObj);
    }

    /**
     * Gets current date in dd/MM/yyyy format
     */
    public static String getCurrentDate() {
        return STANDARD_FORMAT.format(new Date());
    }

    /**
     * Gets current date in yyyy-MM-dd format
     */
    public static String getCurrentISODate() {
        return ISO_FORMAT.format(new Date());
    }

    // ================================
    // SECURITY UTILITIES
    // ================================

    /**
     * Hashes password using SHA-256
     */
    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating password hash", e);
        }
    }

    // ================================
    // TEXT FORMATTING UTILITIES
    // ================================

    /**
     * Capitalizes first letter of each word
     */
    public static String capitalizeText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        String[] words = text.trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                result.append(" ");
            }

            String word = words[i].toLowerCase();
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1));
                }
            }
        }

        return result.toString();
    }

    /**
     * Cleans text by trimming and removing extra spaces
     */
    public static String cleanText(String text) {
        return text == null ? null : text.trim().replaceAll("\\s+", " ");
    }

    /**
     * Formats currency amount
     */
    public static String formatCurrency(double amount) {
        return String.format("Q %.2f", amount);
    }

    // ================================
    // GENERAL UTILITY METHODS
    // ================================

    /**
     * Gets next ID from max ID
     */
    public static int getNextId(int maxId) {
        return maxId + 1;
    }

    // ========================================================================
    // CLASS-SPECIFIC UTILITIES - Methods specific to HR system classes
    // ========================================================================

    // ================================
    // ENHANCED VALIDATION METHODS
    // ================================

    private static ValidationResult validateField(String value, Pattern pattern, String emptyError, String invalidError) {
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.failure(emptyError);
        }
        return pattern.matcher(value.trim()).matches() ?
                ValidationResult.success() :
                ValidationResult.failure(invalidError);
    }

    public static ValidationResult validateEmailDetailed(String email) {
        return validateField(email, EMAIL_PATTERN, EMPTY_FIELD_ERROR, INVALID_EMAIL_ERROR);
    }

    public static ValidationResult validatePhoneDetailed(String phone) {
        return validateField(phone, PHONE_PATTERN, EMPTY_FIELD_ERROR, INVALID_PHONE_ERROR);
    }

    public static ValidationResult validateUsernameDetailed(String username) {
        return validateField(username, USERNAME_PATTERN, EMPTY_FIELD_ERROR, INVALID_USERNAME_ERROR);
    }

    public static ValidationResult validateNameDetailed(String name) {
        return validateField(name, NAMES_PATTERN, EMPTY_FIELD_ERROR, INVALID_NAME_ERROR);
    }

    public static ValidationResult validatePasswordDetailed(String password) {
        if (password == null || password.trim().isEmpty()) {
            return ValidationResult.failure(EMPTY_FIELD_ERROR);
        }
        return PASSWORD_PATTERN.matcher(password).matches() ?
                ValidationResult.success() :
                ValidationResult.failure(INSECURE_PASSWORD_ERROR);
    }

    // ================================
    // SIMPLE VALIDATION METHODS (COMPATIBILITY)
    // ================================

    public static boolean validateEmail(String email) {
        return validateEmailDetailed(email).isValid();
    }

    public static boolean validatePhone(String phone) {
        return validatePhoneDetailed(phone).isValid();
    }

    public static boolean validateName(String name) {
        return validateNameDetailed(name).isValid();
    }

    public static boolean validateUsername(String username) {
        return validateUsernameDetailed(username).isValid();
    }

    public static boolean validateSecurePassword(String password) {
        return validatePasswordDetailed(password).isValid();
    }

    // ================================
    // USER CLASS UTILITIES
    // ================================

    /**
     * Validates all user fields using functional approach
     */
    public static List<String> validateUserData(String email, String phone, String username, String fullName, String password) {
        List<String> errors = new ArrayList<>();

        // Define validation map for cleaner code
        Map<String, ValidationFunction> validations = Map.of(
                "email", Utils::validateEmailDetailed,
                "phone", Utils::validatePhoneDetailed,
                "username", Utils::validateUsernameDetailed,
                "fullName", Utils::validateNameDetailed
        );

        Map<String, String> values = Map.of(
                "email", email != null ? email : "",
                "phone", phone != null ? phone : "",
                "username", username != null ? username : "",
                "fullName", fullName != null ? fullName : ""
        );

        // Validate required fields
        validations.forEach((field, validator) -> {
            ValidationResult result = validator.validate(values.get(field));
            if (!result.isValid()) {
                errors.add(result.getErrorMessage());
            }
        });

        // Validate password if provided
        if (password != null && !password.trim().isEmpty()) {
            ValidationResult passwordResult = validatePasswordDetailed(password);
            if (!passwordResult.isValid()) {
                errors.add(passwordResult.getErrorMessage());
            }
        }

        return errors;
    }

    /**
     * Quick validation with early return for better performance
     */
    public static ValidationResult validateUserDataQuick(String email, String phone, String username, String fullName) {
        ValidationResult result = validateEmailDetailed(email);
        if (!result.isValid()) return result;

        result = validatePhoneDetailed(phone);
        if (!result.isValid()) return result;

        result = validateUsernameDetailed(username);
        if (!result.isValid()) return result;

        return validateNameDetailed(fullName);
    }

    /**
     * Gets formatted validation message
     */
    public static String validateUserDataMessage(String email, String phone, String username, String fullName, String password) {
        List<String> errors = validateUserData(email, phone, username, fullName, password);

        if (errors.isEmpty()) {
            return null;
        }

        StringBuilder message = new StringBuilder("Validation errors found:\n");
        for (int i = 0; i < errors.size(); i++) {
            message.append(String.format("%d. %s\n", i + 1, errors.get(i)));
        }

        return message.toString();
    }

    /**
     * Gets user type ID from name
     */
    public static int getUserTypeId(String userType) {
        if (userType == null) return 0;
        return USER_TYPE_IDS.getOrDefault(userType.toLowerCase().trim(), 0);
    }

    /**
     * Gets user type name from ID
     */
    public static String getUserTypeName(int id) {
        return USER_TYPES.getOrDefault(id, "Unknown");
    }

    /**
     * Gets all user types
     */
    public static Map<Integer, String> getAllUserTypes() {
        return new HashMap<>(USER_TYPES);
    }

    // ================================
    // EMPLOYEE CLASS UTILITIES
    // ================================

    /**
     * Calculates the years of service of an employee from their hiring date
     */
    public static int calculateServiceYears(String hiringDate) {
        if (hiringDate == null || hiringDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Hiring date cannot be null or empty");
        }

        if (!validateDate(hiringDate)) {
            throw new IllegalArgumentException("Invalid hiring date: " + hiringDate);
        }

        try {
            // Parse the hiring date
            LocalDate contractDate = LocalDate.parse(hiringDate, LOCAL_DATE_FORMAT);

            // Get current date
            LocalDate currentDate = LocalDate.now();

            // Verify that hiring date is not in the future
            if (contractDate.isAfter(currentDate)) {
                throw new IllegalArgumentException("Hiring date cannot be in the future");
            }

            // Calculate period between dates
            Period period = Period.between(contractDate, currentDate);

            // Return only complete years
            return period.getYears();

        } catch (Exception e) {
            System.out.println("Error calculating service years: " + e.getMessage());
            throw new IllegalArgumentException("Error processing hiring date: " + hiringDate, e);
        }
    }

    /**
     * Calculates the months of service of an employee (including years)
     */
    public static int calculateServiceMonths(String hiringDate) {
        if (!validateDate(hiringDate)) {
            throw new IllegalArgumentException("Invalid hiring date: " + hiringDate);
        }

        try {
            LocalDate contractDate = LocalDate.parse(hiringDate, LOCAL_DATE_FORMAT);
            LocalDate currentDate = LocalDate.now();

            if (contractDate.isAfter(currentDate)) {
                throw new IllegalArgumentException("Hiring date cannot be in the future");
            }

            Period period = Period.between(contractDate, currentDate);

            // Convert years and months to total months
            return (period.getYears() * 12) + period.getMonths();

        } catch (Exception e) {
            System.out.println("Error calculating service months: " + e.getMessage());
            throw new IllegalArgumentException("Error processing hiring date: " + hiringDate, e);
        }
    }

    /**
     * Gets detailed service time information
     */
    public static String getDetailedServiceTime(String hiringDate) {
        if (!validateDate(hiringDate)) {
            return "Invalid hiring date";
        }

        try {
            LocalDate contractDate = LocalDate.parse(hiringDate, LOCAL_DATE_FORMAT);
            LocalDate currentDate = LocalDate.now();

            if (contractDate.isAfter(currentDate)) {
                return "Hiring date is in the future";
            }

            Period period = Period.between(contractDate, currentDate);

            return String.format("%d year(s), %d month(s), %d day(s)",
                    period.getYears(),
                    period.getMonths(),
                    period.getDays());

        } catch (Exception e) {
            return "Error calculating service time: " + e.getMessage();
        }
    }

    // ================================
    // LEGACY METHODS (DEPRECATED)
    // ================================

    /**
     * @deprecated Use calculateServiceYears instead
     * Legacy method kept for backward compatibility
     */
    @Deprecated
    public static int calculateServiceYearsLegacy(String hiringDate) {
        return calculateServiceYears(hiringDate);
    }

    /**
     * @deprecated Use calculateServiceMonths instead
     * Legacy method kept for backward compatibility
     */
    @Deprecated
    public static int calculateServiceMonthsLegacy(String hiringDate) {
        return calculateServiceMonths(hiringDate);
    }

    /**
     * @deprecated Use getDetailedServiceTime instead
     * Legacy method kept for backward compatibility
     */
    @Deprecated
    public static String getDetailedServiceTimeLegacy(String hiringDate) {
        return getDetailedServiceTime(hiringDate);
    }

    // ================================
    // ADDITIONAL UTILITY METHODS
    // ================================

    /**
     * Validates that a string contains only alphabetic characters and spaces
     *
     * @param text text to validate
     * @return true if valid, false otherwise
     */
    public static boolean isAlphabetic(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        return text.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");
    }

    /**
     * Validates that a string is a valid positive integer
     *
     * @param text text to validate
     * @return true if valid positive integer, false otherwise
     */
    public static boolean isPositiveInteger(String text) {
        if (!isNumeric(text)) {
            return false;
        }
        try {
            int number = Integer.parseInt(text);
            return number > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates that a double value is within acceptable salary range
     *
     * @param salary salary value to validate
     * @param minSalary minimum acceptable salary
     * @param maxSalary maximum acceptable salary
     * @return true if within range, false otherwise
     */
    public static boolean validateSalaryRange(double salary, double minSalary, double maxSalary) {
        return salary >= minSalary && salary <= maxSalary;
    }

    /**
     * Formats a name by capitalizing each word and removing extra spaces
     *
     * @param name name to format
     * @return formatted name
     */
    public static String formatName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return name;
        }
        return capitalizeText(cleanText(name));
    }

    /**
     * Validates that an ID is valid (positive integer)
     *
     * @param id ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean validateId(int id) {
        return id > 0;
    }

    /**
     * Validates that a string ID is valid
     *
     * @param idString string representation of ID
     * @return true if valid, false otherwise
     */
    public static boolean validateIdString(String idString) {
        if (!isNumeric(idString)) {
            return false;
        }
        try {
            int id = Integer.parseInt(idString);
            return validateId(id);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Generates a formatted error message for validation failures
     *
     * @param fieldName name of the field that failed validation
     * @param errorType type of validation error
     * @return formatted error message
     */
    public static String generateValidationErrorMessage(String fieldName, String errorType) {
        return String.format("Validation error in field '%s': %s", fieldName, errorType);
    }

    /**
     * Checks if a date string represents a future date
     *
     * @param dateString date in dd/MM/yyyy format
     * @return true if date is in the future, false otherwise
     */
    public static boolean isFutureDate(String dateString) {
        if (!validateDate(dateString)) {
            return false;
        }

        try {
            LocalDate inputDate = LocalDate.parse(dateString, LOCAL_DATE_FORMAT);
            LocalDate today = LocalDate.now();
            return inputDate.isAfter(today);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if a date string represents a past date
     *
     * @param dateString date in dd/MM/yyyy format
     * @return true if date is in the past, false otherwise
     */
    public static boolean isPastDate(String dateString) {
        if (!validateDate(dateString)) {
            return false;
        }

        try {
            LocalDate inputDate = LocalDate.parse(dateString, LOCAL_DATE_FORMAT);
            LocalDate today = LocalDate.now();
            return inputDate.isBefore(today);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Calculates age from birth date string
     *
     * @param birthDateString birth date in dd/MM/yyyy format
     * @return age in years, or -1 if invalid date
     */
    public static int calculateAge(String birthDateString) {
        if (!validateDate(birthDateString)) {
            return -1;
        }

        try {
            LocalDate birthDate = LocalDate.parse(birthDateString, LOCAL_DATE_FORMAT);
            LocalDate today = LocalDate.now();

            if (birthDate.isAfter(today)) {
                return -1; // Cannot calculate age for future birth date
            }

            Period period = Period.between(birthDate, today);
            return period.getYears();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Converts a salary amount to formatted currency string
     *
     * @param salary salary amount
     * @param includeCurrency whether to include currency symbol
     * @return formatted salary string
     */
    public static String formatSalary(double salary, boolean includeCurrency) {
        if (includeCurrency) {
            return formatCurrency(salary);
        } else {
            return String.format("%.2f", salary);
        }
    }

    /**
     * Validates that a department name is not empty and properly formatted
     *
     * @param departmentName department name to validate
     * @return true if valid, false otherwise
     */
    public static boolean validateDepartmentName(String departmentName) {
        if (!validateNonEmptyText(departmentName)) {
            return false;
        }

        // Department name should be between 2 and 100 characters
        String cleaned = cleanText(departmentName);
        return cleaned.length() >= 2 && cleaned.length() <= 100;
    }

    /**
     * Validates that a position title is not empty and properly formatted
     *
     * @param positionTitle position title to validate
     * @return true if valid, false otherwise
     */
    public static boolean validatePositionTitle(String positionTitle) {
        if (!validateNonEmptyText(positionTitle)) {
            return false;
        }

        // Position title should be between 2 and 80 characters
        String cleaned = cleanText(positionTitle);
        return cleaned.length() >= 2 && cleaned.length() <= 80;
    }

    /**
     * Generates a summary string for employee service time
     *
     * @param hiringDate hiring date in dd/MM/yyyy format
     * @return summary string with years and months of service
     */
    public static String generateServiceSummary(String hiringDate) {
        try {
            int years = calculateServiceYears(hiringDate);
            int totalMonths = calculateServiceMonths(hiringDate);
            int months = totalMonths % 12;

            if (years == 0) {
                return String.format("%d month(s) of service", months);
            } else if (months == 0) {
                return String.format("%d year(s) of service", years);
            } else {
                return String.format("%d year(s) and %d month(s) of service", years, months);
            }
        } catch (Exception e) {
            return "Unable to calculate service time";
        }
    }
}
