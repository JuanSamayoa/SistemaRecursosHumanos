package ProyectoFinal;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Password Security Utilities
 * Handles password encryption, decryption, and hashing for the HR System
 *
 * @author Juan Samayoa
 */
public class PasswordSecurity {

    private static final Logger logger = Logger.getLogger(PasswordSecurity.class.getName());
    
    // AES Configuration
    private static final String AES_ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    
    // Get encryption key from environment or use default (should be in .env)
    private static final String ENCRYPTION_KEY = getEncryptionKey();
    
    /**
     * Gets encryption key from configuration
     */
    private static String getEncryptionKey() {
        String key = System.getProperty("ENCRYPTION_KEY", "HRSystem2025Key!"); // Default fallback
        return key.length() >= 16 ? key.substring(0, 16) : padKey(key);
    }
    
    /**
     * Pads key to 16 characters if shorter
     */
    private static String padKey(String key) {
        StringBuilder paddedKey = new StringBuilder(key);
        while (paddedKey.length() < 16) {
            paddedKey.append("0");
        }
        return paddedKey.toString();
    }

    /**
     * Encrypts a password using AES encryption
     */
    public static String encryptPassword(String plainPassword) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encryptedBytes = cipher.doFinal(plainPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error encrypting password: " + e.getMessage());
            throw new RuntimeException("Password encryption failed", e);
        }
    }

    /**
     * Decrypts an encrypted password
     */
    public static String decryptPassword(String encryptedPassword) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error decrypting password: " + e.getMessage());
            throw new RuntimeException("Password decryption failed", e);
        }
    }

    /**
     * Hashes a password using SHA-256 with the specific implementation requested
     */
    public static String hashPassword(String pass) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(pass.getBytes());
            byte[] digest = md.digest();
            pass = String.format("%064x", new java.math.BigInteger(1, digest));
            return pass;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error hashing password: " + e.getMessage());
            System.out.println("Error al encriptar la contraseña: " + e);
        }
        return null;
    }

    /**
     * Verifies a plain password against a hashed password
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        String hashedInput = hashPassword(plainPassword);
        return hashedInput != null && hashedInput.equals(hashedPassword);
    }

    /**
     * Checks if a string appears to be encrypted (Base64 format)
     */
    public static boolean isEncrypted(String password) {
        try {
            Base64.getDecoder().decode(password);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Gets database password from configuration, decrypting if necessary
     */
    public static String getDatabasePassword() {
        String password = ConfigurationSystem.getDatabasePassword();
        
        if (password.isEmpty()) {
            return password; // Empty for Windows auth
        }
        
        // Check if password is encrypted
        if (isEncrypted(password)) {
            try {
                return decryptPassword(password);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Could not decrypt password, using as-is: " + e.getMessage());
                return password; // Return as-is if decryption fails
            }
        }
        
        return password; // Return plain password
    }

    /**
     * Utility method to encrypt and display a password (for setup)
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            String command = args[0];
            
            switch (command.toLowerCase()) {
                case "encrypt":
                    if (args.length > 1) {
                        String password = args[1];
                        String encrypted = encryptPassword(password);
                        System.out.println("Encrypted: " + encrypted);
                    } else {
                        System.out.println("Usage: java PasswordSecurity encrypt <password>");
                    }
                    break;
                    
                case "decrypt":
                    if (args.length > 1) {
                        String encrypted = args[1];
                        try {
                            String decrypted = decryptPassword(encrypted);
                            System.out.println("Decrypted: " + decrypted);
                        } catch (Exception e) {
                            System.out.println("Error decrypting: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Usage: java PasswordSecurity decrypt <encrypted_password>");
                    }
                    break;
                    
                case "hash":
                    if (args.length > 1) {
                        String password = args[1];
                        String hash = hashPassword(password);
                        System.out.println("Hash: " + hash);
                    } else {
                        System.out.println("Usage: java PasswordSecurity hash <password>");
                    }
                    break;
                    
                case "test":
                    testDatabaseConnection();
                    break;
                    
                default:
                    System.out.println("Available commands:");
                    System.out.println("  encrypt <password>  - Encrypt a password");
                    System.out.println("  decrypt <encrypted> - Decrypt an encrypted password");
                    System.out.println("  hash <password>     - Generate SHA-256 hash");
                    System.out.println("  test                - Test database connection");
            }
        } else {
            // Default behavior - show example
            System.out.println("Password Security Utility");
            System.out.println("Example encryption of 'root':");
            String testPassword = "root";
            String encrypted = encryptPassword(testPassword);
            String hash = hashPassword(testPassword);
            System.out.println("Original: " + testPassword);
            System.out.println("Encrypted: " + encrypted);
            System.out.println("Hash: " + hash);
            System.out.println("Add encrypted to .env: DB_PASSWORD=" + encrypted);
        }
    }
    
    /**
     * Tests database connection with current configuration
     */
    private static void testDatabaseConnection() {
        System.out.println("Testing database connection...");
        
        try {
            String username = ConfigurationSystem.getDatabaseUsername();
            String password = getDatabasePassword();
            boolean isEncrypted = isEncrypted(ConfigurationSystem.getDatabasePassword());
            
            System.out.println("Username: " + username);
            System.out.println("Password encrypted in .env: " + isEncrypted);
            System.out.println("Decoded password: " + (password.isEmpty() ? "[empty - Windows auth]" : "[configured]"));
            
            var conn = DatabaseManager.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Connection successful");
                DatabaseManager.closeConnection();
            }
        } catch (Exception e) {
            System.out.println("✗ Connection error: " + e.getMessage());
        }
    }
}
