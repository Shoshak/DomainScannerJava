package dev.moto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ScanUI extends JFrame {
    private JTextField siteName;
    private JButton open;
    private JTextField domainPath;
    private JPanel content;
    private JList<String> scanResult;
    private JButton scanButton;
    private DefaultListModel<String> model;

    public ScanUI() {
        createUIComponents();
        Scanner scanner = new Scanner(model);
        open.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int response = fileChooser.showOpenDialog(null);
            if (response != JFileChooser.APPROVE_OPTION) return;
            String pathToDomains = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                List<String> domains = Files.readAllLines(Path.of(pathToDomains));
                scanner.setDomains(domains);
                domainPath.setText(pathToDomains);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        scanButton.addActionListener(e -> {
            String name = siteName.getText();
            if (name.isEmpty()) {
                showError("Invalid site name");
                return;
            }
            scanner.setSiteName(name);
            String domainsPath = domainPath.getText();
            if (domainsPath.isEmpty()) {
                showError("Domains not set");
                return;
            }
            model.clear();
            try {
                scanner.scan();
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        });
        scanResult.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    try {
                        URI link = new URI(scanResult.getSelectedValue());
                        Desktop desktop = Desktop.getDesktop();
                        if (desktop.isSupported(Desktop.Action.BROWSE)) {
                            desktop.browse(link);
                        } else {
                            Runtime runtime = Runtime.getRuntime();
                            runtime.exec(new String[] { "xdg-open", link.toString() });
                        }
                    } catch (URISyntaxException ex) {
                        throw new RuntimeException(ex);
                    } catch (IOException ignore) {}
                }
            }
        });
        setContentPane(content);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
        setTitle("Domain scanner");
        setSize(600, 600);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createUIComponents() {
        model = new DefaultListModel<>();
        scanResult.setModel(model);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
