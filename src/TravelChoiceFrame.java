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
        setLayout(new BorderLayout());
        JLabel infoLabel = new JLabel("En yakın durak: "
                + nearestDurak.getId()
                + String.format(" (%.2f km)", enYakinDurakMesafe),
                SwingConstants.CENTER);
        add(infoLabel, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel();
        if (enYakinDurakMesafe > 3) {
            JLabel taksiLabel = new JLabel(
                    "<html>Mesafe 3 km'den fazla olduğu için otomatik olarak <br>" +
                            "taksi kullanmanız önerilir. Taksi ücreti: <b>"
                            + String.format("%.2f TL</b></html>", taksiUcreti),
                    SwingConstants.CENTER
            );
            add(taksiLabel, BorderLayout.CENTER);
            JButton devamButton = new JButton("Devam");
            devamButton.addActionListener(e -> {
                onChoiceSelected.accept("taksi");
                dispose();
            });
            buttonPanel.add(devamButton);
        } else {
            JButton taksiButton = new JButton("Taksi ile Git");
            JButton yuruyerekButton = new JButton("Yürüyerek Git");
            taksiButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(this,
                        "Taksi ücreti: " + String.format("%.2f TL", taksiUcreti),
                        "Taksi Bilgisi",
                        JOptionPane.INFORMATION_MESSAGE
                );
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
