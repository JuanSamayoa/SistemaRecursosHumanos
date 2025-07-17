package ProyectoFinal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to load and manage developer information from properties file
 * 
 * @author Juan Samayoa
 */
public class DeveloperInfo {
    
    private static Properties properties;
    private static final String PROPERTIES_FILE = "/DEVELOPER_INFO.properties";
    
    static {
        loadProperties();
    }
    
    /**
     * Load developer properties from file
     */
    private static void loadProperties() {
        properties = new Properties();
        try (InputStream input = DeveloperInfo.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (input != null) {
                properties.load(input);
            } else {
                System.err.println("DEVELOPER_INFO.properties file not found");
            }
        } catch (IOException e) {
            System.err.println("Error loading developer properties: " + e.getMessage());
        }
    }
    
    /**
     * Get developer name
     * @return developer name
     */
    public static String getDeveloperName() {
        return properties.getProperty("Developer.Name", "Unknown Developer");
    }
    
    /**
     * Get developer email
     * @return developer email
     */
    public static String getDeveloperEmail() {
        return properties.getProperty("Developer.Email", "");
    }
    
    /**
     * Get developer GitHub profile
     * @return GitHub URL
     */
    public static String getDeveloperGitHub() {
        return properties.getProperty("Developer.GitHub", "");
    }
    
    /**
     * Get developer LinkedIn profile
     * @return LinkedIn URL
     */
    public static String getDeveloperLinkedIn() {
        return properties.getProperty("Developer.LinkedIn", "");
    }
    
    /**
     * Get developer role
     * @return developer role
     */
    public static String getDeveloperRole() {
        return properties.getProperty("Developer.Role", "");
    }
    
    /**
     * Get project start date
     * @return project start date
     */
    public static String getProjectStartDate() {
        return properties.getProperty("Project.StartDate", "");
    }
    
    /**
     * Get project status
     * @return project status
     */
    public static String getProjectStatus() {
        return properties.getProperty("Project.Status", "");
    }
    
    /**
     * Get project license
     * @return project license
     */
    public static String getProjectLicense() {
        return properties.getProperty("Project.License", "");
    }
    
    /**
     * Get project repository URL
     * @return repository URL
     */
    public static String getProjectRepository() {
        return properties.getProperty("Project.Repository", "");
    }
    
    /**
     * Get technologies used
     * @return technologies string
     */
    public static String getTechnologies() {
        return properties.getProperty("Technologies", "");
    }
    
    /**
     * Get design patterns used
     * @return patterns string
     */
    public static String getPatterns() {
        return properties.getProperty("Patterns", "");
    }
    
    /**
     * Get IDE used
     * @return IDE name
     */
    public static String getIDE() {
        return properties.getProperty("IDE", "");
    }
    
    /**
     * Get build tool used
     * @return build tool name
     */
    public static String getBuildTool() {
        return properties.getProperty("Build.Tool", "");
    }
    
    /**
     * Get complete developer information as formatted string
     * @return formatted developer info
     */
    public static String getCompleteInfo() {
        StringBuilder info = new StringBuilder();
        info.append("=== INFORMACIÓN DEL DESARROLLADOR ===\n");
        info.append("Nombre: ").append(getDeveloperName()).append("\n");
        info.append("Email: ").append(getDeveloperEmail()).append("\n");
        info.append("Rol: ").append(getDeveloperRole()).append("\n");
        info.append("GitHub: ").append(getDeveloperGitHub()).append("\n");
        info.append("LinkedIn: ").append(getDeveloperLinkedIn()).append("\n\n");
        
        info.append("=== INFORMACIÓN DEL PROYECTO ===\n");
        info.append("Inicio: ").append(getProjectStartDate()).append("\n");
        info.append("Estado: ").append(getProjectStatus()).append("\n");
        info.append("Licencia: ").append(getProjectLicense()).append("\n");
        info.append("Repositorio: ").append(getProjectRepository()).append("\n\n");
        
        info.append("=== TECNOLOGÍAS UTILIZADAS ===\n");
        info.append("Tecnologías: ").append(getTechnologies()).append("\n");
        info.append("Patrones: ").append(getPatterns()).append("\n");
        info.append("IDE: ").append(getIDE()).append("\n");
        info.append("Build Tool: ").append(getBuildTool()).append("\n");
        
        return info.toString();
    }
    
    /**
     * Get system signature for reports
     * @return system signature
     */
    public static String getSystemSignature() {
        return String.format("Sistema desarrollado por %s - %s", 
                           getDeveloperName(), 
                           getProjectStatus());
    }
    
    /**
     * Get copyright notice
     * @return copyright notice
     */
    public static String getCopyrightNotice() {
        return String.format("© %s %s - %s", 
                           getProjectStartDate(),
                           getDeveloperName(),
                           getProjectLicense());
    }
}
