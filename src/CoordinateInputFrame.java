import javax.swing.*;
import java.awt.*;

public class CoordinateInputFrame extends JFrame {
    private JTextField latField;
    private JTextField lonField;

    public CoordinateInputFrame() {
        setTitle("Kullanıcı Koordinatları Giriniz");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel oluştur
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(184, 184, 184));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Enlem Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel latLabel = new JLabel("Latitude (Enlem):");
        latLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(latLabel, gbc);

        // Enlem TextField
        gbc.gridx = 1;
        latField = new JTextField(15);
        latField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(latField, gbc);

        // Boylam Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lonLabel = new JLabel("Longitude (Boylam):");
        lonLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lonLabel, gbc);

        // Boylam TextField
        gbc.gridx = 1;
        lonField = new JTextField(15);
        lonField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(lonField, gbc);

        // Onayla Butonu
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton btnConfirm = new JButton("Onayla");
        btnConfirm.setFont(new Font("Arial", Font.BOLD, 14));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setBackground(new Color(50, 150, 250));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setOpaque(true);
        panel.add(btnConfirm, gbc);

        btnConfirm.addActionListener(e -> {
            try {
                double userLat = Double.parseDouble(latField.getText());
                double userLon = Double.parseDouble(lonField.getText());

                this.dispose(); // Pencereyi kapat
                Main.startMainApplication(userLat, userLon); // Ana uygulamayı başlat

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Lütfen geçerli koordinatlar girin!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }
}
