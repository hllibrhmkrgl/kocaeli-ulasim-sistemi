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

    // Arayüz bileşenleri (Hedef durak giriş alanı, ComboBox, Buton)
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

        // Gelen parametreleri sakla
        this.root = root;
        this.locationHandler = locationHandler;
        this.routeFinder = routeFinder;
        this.routeService = routeService;
        this.nearestDurak = nearestDurak;
        this.userLat = userLat;
        this.userLon = userLon;

        // Temel pencere ayarları
        setTitle("Ulaşım Uygulaması");
        setSize(1280, 720); // Daha geniş bir boyut (isteğe göre değiştirilebilir)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ana pencere için BorderLayout kullanıyoruz:
        setLayout(new BorderLayout());

        // -------------------------------------------------------------------
        // 1) PROFİL PANELİ (sol üst - WEST)
        // -------------------------------------------------------------------
        JPanel profilPanel = new JPanel();
        profilPanel.setLayout(new BoxLayout(profilPanel, BoxLayout.Y_AXIS));
        profilPanel.setBorder(BorderFactory.createTitledBorder("Profil Bilgisi"));

        // Örnek profil bilgisi gösterimi
        JLabel lblDurakBilgisi = new JLabel("En Yakın Durak: "
                + (nearestDurak != null ? nearestDurak.getId() : "Bilinmiyor"));
        JLabel lblKoordinat = new JLabel("Koordinatlar (lat, lon): " + userLat + " , " + userLon);

        // Profil paneline ekle
        profilPanel.add(lblDurakBilgisi);
        profilPanel.add(lblKoordinat);

        // -------------------------------------------------------------------
        // 2) HARİTA PANELİ (orta - CENTER)
        // -------------------------------------------------------------------
        JPanel haritaPanel = new JPanel();
        haritaPanel.setBorder(BorderFactory.createTitledBorder("Harita Alanı"));

        // Şimdilik bir placeholder label kullanalım
        // Gerçek harita görüntüleme kütüphanesi buraya eklenebilir.
        JLabel lblHaritaPlaceholder = new JLabel("Burada harita gösterilecek (placeholder)");
        haritaPanel.add(lblHaritaPlaceholder);

        // -------------------------------------------------------------------
        // 3) İŞLEM (HEDEF DURAK) PANELİ (sağ - EAST)
        // -------------------------------------------------------------------
        JPanel islemPanel = new JPanel();
        islemPanel.setLayout(new BoxLayout(islemPanel, BoxLayout.Y_AXIS));
        islemPanel.setBorder(BorderFactory.createTitledBorder("Hedef & İşlemler"));

        // a) Hedef durak ismi girişi
        JLabel hedefDurakLabel = new JLabel("Hedef Durak İsmi:");
        hedefDurakLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        islemPanel.add(hedefDurakLabel);

        hedefDurakField = new JTextField();
        hedefDurakField.setMaximumSize(new Dimension(200, 30));
        islemPanel.add(hedefDurakField);

        // b) 1, 2, 3, 4 işlemleri için ComboBox
        String[] islemler = {
                "1. Gitmek İstediğim durağa en kısa yol",
                "2. Otobüs Duraklarının isimlerine bakma",
                "3. Tramvay Duraklarının isimlerine bakma",
                "4. Sadece Otobüs ile gitmek için yol"
        };
        islemCombo = new JComboBox<>(islemler);
        islemCombo.setMaximumSize(new Dimension(200, 30));
        islemPanel.add(islemCombo);

        // c) Çalıştır butonu
        calistirButton = new JButton("Çalıştır");
        calistirButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        islemPanel.add(calistirButton);

        // Butonun tıklanma olayı
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

                        // Root içindeki duraklar arasında arama yap
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

        // -------------------------------------------------------------------
        // Panelleri ana pencereye (BorderLayout) ekliyoruz
        // -------------------------------------------------------------------
        add(profilPanel, BorderLayout.WEST);   // Sol kısma profil paneli
        add(haritaPanel, BorderLayout.CENTER); // Ortaya harita paneli
        add(islemPanel, BorderLayout.EAST);    // Sağa işlem paneli

        // Pencereyi görünür yap
        setVisible(true);
    }
}
