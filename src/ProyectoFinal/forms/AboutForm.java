package ProyectoFinal.forms;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import ProyectoFinal.DeveloperInfo;

/**
 * About dialog form showing developer and project information
 *
 * @author Juan Samayoa
 */
@SuppressWarnings("this-escape")
public class AboutForm extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTextArea infoTextArea;
    private JButton closeButton;
    private JButton githubButton;
    private JButton linkedinButton;

    /**
     * Creates new AboutForm
     */
    public AboutForm(Frame parent) {
        super(parent, "Acerca de - Sistema de Recursos Humanos", true);
        initComponents();
        loadDeveloperInfo();
        setupEventHandlers();
    }

    /**
     * Initialize the form components
     */
    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Create main content panel
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Create the header panel with title and icon
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(59, 89, 152));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Sistema de Recursos Humanos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel versionLabel = new JLabel("Version: " + DeveloperInfo.getProjectStatus());
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        versionLabel.setForeground(Color.WHITE);
        versionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(versionLabel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create the main content panel with developer information
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        infoTextArea = new JTextArea();
        infoTextArea.setEditable(false);
        infoTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        infoTextArea.setBackground(getBackground());
        infoTextArea.setLineWrap(true);
        infoTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(infoTextArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Información del Sistema"));
        scrollPane.setPreferredSize(new Dimension(550, 300));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create the button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        githubButton = new JButton("GitHub");
        githubButton.setIcon(createColorIcon(new Color(33, 37, 41)));
        githubButton.setToolTipText("Visitar perfil de GitHub");

        linkedinButton = new JButton("LinkedIn");
        linkedinButton.setIcon(createColorIcon(new Color(0, 119, 181)));
        linkedinButton.setToolTipText("Visitar perfil de LinkedIn");

        closeButton = new JButton("Cerrar");
        closeButton.setPreferredSize(new Dimension(100, 30));

        panel.add(githubButton);
        panel.add(linkedinButton);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(closeButton);

        return panel;
    }

    /**
     * Create a simple colored icon
     */
    private Icon createColorIcon(Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(color);
                g.fillOval(x, y, getIconWidth(), getIconHeight());
            }

            @Override
            public int getIconWidth() {
                return 12;
            }

            @Override
            public int getIconHeight() {
                return 12;
            }
        };
    }

    /**
     * Load developer information into the text area
     */
    private void loadDeveloperInfo() {
        StringBuilder info = new StringBuilder();

        info.append("════════════════════════════════════════════════════════════════\n");
        info.append("                    SISTEMA DE RECURSOS HUMANOS\n");
        info.append("════════════════════════════════════════════════════════════════\n\n");

        info.append("📋 INFORMACIÓN DEL DESARROLLADOR\n");
        info.append("─────────────────────────────────────────────────────────────\n");
        info.append("👤 Nombre: ").append(DeveloperInfo.getDeveloperName()).append("\n");
        info.append("📧 Email: ").append(DeveloperInfo.getDeveloperEmail()).append("\n");
        info.append("💼 Rol: ").append(DeveloperInfo.getDeveloperRole()).append("\n");
        info.append("🐙 GitHub: ").append(DeveloperInfo.getDeveloperGitHub()).append("\n");
        info.append("💼 LinkedIn: ").append(DeveloperInfo.getDeveloperLinkedIn()).append("\n\n");

        info.append("🚀 INFORMACIÓN DEL PROYECTO\n");
        info.append("─────────────────────────────────────────────────────────────\n");
        info.append("📅 Año de inicio: ").append(DeveloperInfo.getProjectStartDate()).append("\n");
        info.append("✅ Estado: ").append(DeveloperInfo.getProjectStatus()).append("\n");
        info.append("⚖️ Licencia: ").append(DeveloperInfo.getProjectLicense()).append("\n");
        info.append("📦 Repositorio: ").append(DeveloperInfo.getProjectRepository()).append("\n\n");

        info.append("🛠️ TECNOLOGÍAS UTILIZADAS\n");
        info.append("─────────────────────────────────────────────────────────────\n");
        info.append("🔧 Tecnologías: ").append(DeveloperInfo.getTechnologies()).append("\n");
        info.append("🏗️ Patrones: ").append(DeveloperInfo.getPatterns()).append("\n");
        info.append("💻 IDE: ").append(DeveloperInfo.getIDE()).append("\n");
        info.append("🔨 Build Tool: ").append(DeveloperInfo.getBuildTool()).append("\n\n");

        info.append("© ").append(DeveloperInfo.getProjectStartDate()).append(" ")
                .append(DeveloperInfo.getDeveloperName()).append(" - ")
                .append(DeveloperInfo.getProjectLicense()).append("\n");

        infoTextArea.setText(info.toString());
        infoTextArea.setCaretPosition(0);
    }

    /**
     * Setup event handlers for buttons
     */
    private void setupEventHandlers() {
        closeButton.addActionListener(_ -> dispose());

        githubButton.addActionListener(_ -> openWebsite(DeveloperInfo.getDeveloperGitHub()));

        linkedinButton.addActionListener(_ -> openWebsite(DeveloperInfo.getDeveloperLinkedIn()));
    }

    /**
     * Open website in default browser
     */
    private void openWebsite(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se puede abrir el navegador automáticamente.\n" +
                                "Por favor, visita manualmente: " + url,
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al abrir el enlace: " + ex.getMessage() + "\n" +
                            "URL: " + url,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
