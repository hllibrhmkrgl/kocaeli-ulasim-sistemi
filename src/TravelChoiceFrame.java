import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class TravelChoiceFrame extends JFrame {
    private Durak nearestDurak;
    private double enYakinDurakMesafe;
    private double taksiUcreti;
    private Consumer<String> onChoiceSelected; // tıklama callback

    public TravelChoiceFrame(Durak nearestDurak,
                             double enYakinDurakMesafe,
                             double taksiUcreti,
                             Consumer<String> onChoiceSelected) {
        this.nearestDurak = nearestDurak;
        this.enYakinDurakMesafe = enYakinDurakMesafe;
        this.taksiUcreti = taksiUcreti;
        this.onChoiceSelected = onChoiceSelected;

        setTitle("Nasıl Gitmek İstersiniz?");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Ana panel
        setLayout(new BorderLayout());

        // Bilgi etiketi (en yakın durak + mesafe)
        JLabel infoLabel = new JLabel("En yakın durak: "
                + nearestDurak.getId()
                + String.format(" (%.2f km)", enYakinDurakMesafe),
                SwingConstants.CENTER);
        add(infoLabel, BorderLayout.NORTH);

        // Butonların yer alacağı panel
        JPanel buttonPanel = new JPanel();

        // Eğer mesafe 3 km'den fazlaysa, otomatik taksi ve "Devam" butonu
        if (enYakinDurakMesafe > 3) {
            // Kullanıcıya taksi ücretini gösteren bir açıklama
            JLabel taksiLabel = new JLabel(
                    "<html>Mesafe 3 km'den fazla olduğu için otomatik olarak <br>" +
                            "taksi kullanmanız önerilir. Taksi ücreti: <b>"
                            + String.format("%.2f TL</b></html>", taksiUcreti),
                    SwingConstants.CENTER
            );
            add(taksiLabel, BorderLayout.CENTER);

            JButton devamButton = new JButton("Devam");
            devamButton.addActionListener(e -> {
                // Otomatik olarak taksi seçelim
                onChoiceSelected.accept("taksi");
                dispose();
            });

            buttonPanel.add(devamButton);

        } else {
            // Mesafe <= 3 km ise taksi mi yürüyerek mi diye soralım
            JButton taksiButton = new JButton("Taksi ile Git");
            JButton yuruyerekButton = new JButton("Yürüyerek Git");

            taksiButton.addActionListener(e -> {
                // Taksi seçilince ücret gösterelim
                JOptionPane.showMessageDialog(this,
                        "Taksi ücreti: " + String.format("%.2f TL", taksiUcreti),
                        "Taksi Bilgisi",
                        JOptionPane.INFORMATION_MESSAGE
                );
                // Seçim callback'ini çağır
                onChoiceSelected.accept("taksi");
                dispose();
            });

            yuruyerekButton.addActionListener(e -> {
                onChoiceSelected.accept("yuruyus");
                dispose();
            });

            buttonPanel.add(taksiButton);
            buttonPanel.add(yuruyerekButton);
        }

        add(buttonPanel, BorderLayout.SOUTH);
    }
}
