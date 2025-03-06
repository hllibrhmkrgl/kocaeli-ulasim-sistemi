import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {
    private Root root;
    private UserLocationHandler locationHandler;
    private RouteFinder routeFinder;
    private RouteService routeService;
    private Durak nearestDurak;
    private double userLat;
    private double userLon;

    // Arayüz bileşenleri
    private JTextField hedefDurakField;
    private JComboBox<String> islemCombo;
    private JButton calistirButton;

    public MainFrame(Durak durak, Root root,
                     UserLocationHandler locationHandler,
                     RouteFinder routeFinder,
                     RouteService routeService,
                     Durak nearestDurak,
                     double userLat,
                     double userLon) {
        this.root = root;
        this.locationHandler = locationHandler;
        this.routeFinder = routeFinder;
        this.routeService = routeService;
        this.nearestDurak = nearestDurak;
        this.userLat = userLat;
        this.userLon = userLon;

        // Temel pencere ayarları
        setTitle("Ulaşım Uygulaması");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ana panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // 1) Hedef durak ismi girişi
        JLabel hedefDurakLabel = new JLabel("Hedef Durak İsmi:");
        hedefDurakLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(hedefDurakLabel);

        hedefDurakField = new JTextField();
        hedefDurakField.setMaximumSize(new Dimension(300, 30));
        panel.add(hedefDurakField);

        // 2) 1, 2, 3, 4 işlemleri için ComboBox
        String[] islemler = {
                "1. Gitmek İstediğim durağa olan en kısa yol",
                "2. Otobüs Duraklarının ismine bakma",
                "3. Tramvay Duraklarının ismine bakma",
                "4. Sadece Otobüs ile gitmek için yol"
        };
        islemCombo = new JComboBox<>(islemler);
        islemCombo.setMaximumSize(new Dimension(300, 30));
        panel.add(islemCombo);

        // 3) Çalıştır butonu
        calistirButton = new JButton("Çalıştır");
        calistirButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(calistirButton);

        calistirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int secilenIslem = islemCombo.getSelectedIndex() + 1;

                switch (secilenIslem) {
                    // 2 ve 3 numaralı işlemler hedef durak gerektirmiyor
                    case 2:
                        System.out.println("2. Otobüs Duraklarının ismine bakma");
                        String busInfo = routeFinder.getAllBusInfo();
                        System.out.println(busInfo);
                        JOptionPane.showMessageDialog(MainFrame.this, busInfo);
                        break;


                    case 3:
                        System.out.println("3. Tramvay Duraklarının ismine bakma");
                        String tramInfo = routeFinder.getAllTramInfo();
                        System.out.println(tramInfo);
                        JOptionPane.showMessageDialog(MainFrame.this, tramInfo);
                        break;

                    // 1 ve 4 numaralı işlemler için hedef durak girişi gerekiyor
                    case 1:
                    case 4:
                        // Metin kutusundan hedef durak alalım
                        String hedefDurakIsmi = hedefDurakField.getText().trim();
                        boolean durakVarMi = false;
                        Durak hedefDurak = null;

                        for (Durak d : root.getDuraklar()) {
                            if (d.getId().equalsIgnoreCase(hedefDurakIsmi)) {
                                hedefDurak = d;
                                durakVarMi = true;
                                break;
                            }
                        }

                        if (!durakVarMi) {
                            JOptionPane.showMessageDialog(MainFrame.this,
                                    "Hata! Girdiğiniz durak listede bulunmuyor.");
                            System.out.println("Hata! Girdiğiniz durak listede bulunmuyor.");
                            return;
                        }

                        // Hedef durak bulunduğuna göre işlemi yapıyoruz
                        if (secilenIslem == 1) {
                            System.out.println("1. Gitmek İstediğim durağa olan en kısa yol");
                            routeService.findAndPrintRoute(nearestDurak.getId(), hedefDurak.getId());
                        } else {
                            System.out.println("4. Sadece Otobüs ile gitmek için yol");
                            routeFinder.getOnlyBusRoute(nearestDurak.getId(), hedefDurak.getId());
                        }

                        JOptionPane.showMessageDialog(MainFrame.this,
                                "İşlem tamamlandı, detaylar terminalde gösterildi.");
                        break;

                    default:
                        // Farklı bir işlem yoksa
                        System.out.println("Hatalı seçim");
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "Hatalı seçim yaptınız.");
                }
            }
        });


        add(panel);
    }
}
