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
    private String userType = "Normal"; // Varsayılan kullanıcı tipi
    private Taxi taxi ;
    // Arayüz bileşenleri (Hedef durak giriş alanı, ComboBox, Buton)
    private JTextField hedefDurakField;
    private JComboBox<String> islemCombo;
    private JButton calistirButton;
    // Çıktıları göstermek için JTextArea
    private JTextArea outputArea;

    public MainFrame(Durak durak, Root root,
                     UserLocationHandler locationHandler,
                     RouteFinder routeFinder,
                     RouteService routeService,
                     Durak nearestDurak,
                     double userLat,
                     double userLon,
                     Taxi taxiInfo) {

        // Gelen parametreleri sakla
        this.root = root;
        this.locationHandler = locationHandler;
        this.routeFinder = routeFinder;
        this.routeService = routeService;
        this.nearestDurak = nearestDurak;
        this.userLat = userLat;
        this.userLon = userLon;
        this.taxi = taxiInfo;
        // Temel pencere ayarları
        setTitle("Ulaşım Uygulaması");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        // -------------------------------------------------------------------
        // 1) PROFİL PANELİ (sol - WEST)
        // -------------------------------------------------------------------
        JPanel profilPanel = new JPanel();
        profilPanel.setLayout(new BoxLayout(profilPanel, BoxLayout.Y_AXIS));
        profilPanel.setBorder(BorderFactory.createTitledBorder("Profil Bilgisi"));

        // Profil bilgileri
        JLabel lblDurakBilgisi = new JLabel("En Yakın Durak: " +
                (nearestDurak != null ? nearestDurak.getId() : "Bilinmiyor"));
        JLabel BaslangicTaksiBilgisi = new JLabel("En yakın durağa Taksi ücreti : "+
                routeFinder.calculateTaxiCost(userLat, userLon, nearestDurak, taxiInfo)
                      +" TL");
        JLabel lblKoordinat = new JLabel("Koordinatlar: " + userLat + " , " + userLon);


        lblDurakBilgisi.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblDurakBilgisi.getPreferredSize().height));
        lblKoordinat.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblKoordinat.getPreferredSize().height));
        BaslangicTaksiBilgisi.setMaximumSize(new Dimension(Integer.MAX_VALUE, BaslangicTaksiBilgisi.getPreferredSize().height));
        lblDurakBilgisi.setAlignmentX(Component.LEFT_ALIGNMENT);
        BaslangicTaksiBilgisi.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblKoordinat.setAlignmentX(Component.LEFT_ALIGNMENT);


        JPanel userTypePanel = new JPanel();
        userTypePanel.setLayout(new BoxLayout(userTypePanel, BoxLayout.Y_AXIS));
        userTypePanel.setBorder(BorderFactory.createTitledBorder("Kullanıcı Tipi"));

        JRadioButton rbOgrenci = new JRadioButton("Öğrenci");
        JRadioButton rbYasli = new JRadioButton("Yaşlı");
        JRadioButton rbNormal = new JRadioButton("Normal");
        rbNormal.setSelected(true); // Varsayılan seçim

        ButtonGroup userTypeGroup = new ButtonGroup();
        userTypeGroup.add(rbOgrenci);
        userTypeGroup.add(rbYasli);
        userTypeGroup.add(rbNormal);

        // Radiobutton'ların genişliği ve hizalaması
        rbOgrenci.setMaximumSize(new Dimension(Integer.MAX_VALUE, rbOgrenci.getPreferredSize().height));
        rbYasli.setMaximumSize(new Dimension(Integer.MAX_VALUE, rbYasli.getPreferredSize().height));
        rbNormal.setMaximumSize(new Dimension(Integer.MAX_VALUE, rbNormal.getPreferredSize().height));
        rbOgrenci.setAlignmentX(Component.LEFT_ALIGNMENT);
        rbYasli.setAlignmentX(Component.LEFT_ALIGNMENT);
        rbNormal.setAlignmentX(Component.LEFT_ALIGNMENT);

        userTypePanel.add(rbOgrenci);
        userTypePanel.add(rbYasli);
        userTypePanel.add(rbNormal);
        userTypePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // İndirim paneli oluşturma
        JPanel discountPanel = new JPanel();
        discountPanel.setLayout(new BoxLayout(discountPanel, BoxLayout.Y_AXIS));
        discountPanel.setBorder(BorderFactory.createTitledBorder("İndirim Bilgisi"));
        discountPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDiscountHeader = new JLabel();
        JLabel lblDiscountValue = new JLabel();
        updateDiscountLabels(lblDiscountHeader, lblDiscountValue, userType);

        // Genişlik ayarları
        lblDiscountHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblDiscountHeader.getPreferredSize().height));
        lblDiscountValue.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblDiscountValue.getPreferredSize().height));
        lblDiscountHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDiscountValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        discountPanel.add(lblDiscountHeader);
        discountPanel.add(lblDiscountValue);

        // Radiobutton seçimlerini dinleyen ActionListener
        ActionListener userTypeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rbOgrenci.isSelected()) {
                    userType = "Öğrenci";
                } else if (rbYasli.isSelected()) {
                    userType = "Yaşlı";
                } else {
                    userType = "Normal";
                }
                System.out.println("Kullanıcı tipi seçildi: " + userType);
                updateDiscountLabels(lblDiscountHeader, lblDiscountValue, userType);
            }
        };

        rbOgrenci.addActionListener(userTypeListener);
        rbYasli.addActionListener(userTypeListener);
        rbNormal.addActionListener(userTypeListener);

        // Çıktı alanı (output area) ekleyelim
        outputArea = new JTextArea(5, 20);
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Çıktı Bilgisi"));
        outputScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Profil paneline ekle
        profilPanel.add(lblDurakBilgisi);
        profilPanel.add(BaslangicTaksiBilgisi);
        profilPanel.add(lblKoordinat);
        profilPanel.add(Box.createVerticalStrut(10)); // araya boşluk ekle
        profilPanel.add(userTypePanel);
        profilPanel.add(Box.createVerticalStrut(10));
        profilPanel.add(discountPanel);
        profilPanel.add(Box.createVerticalStrut(10));
        profilPanel.add(outputScroll);
        // -------------------------------------------------------------------
        // 2) HARİTA PANELİ (orta - CENTER)
        // -------------------------------------------------------------------
        HaritaPanel haritaPanel = new HaritaPanel(root.getDuraklar());
        haritaPanel.setCurrentDurak(nearestDurak); // current durak ayarı yapılabilir
        haritaPanel.setBorder(BorderFactory.createTitledBorder("Harita Alanı"));

        // Şimdilik placeholder label
        JLabel lblHaritaPlaceholder = new JLabel("Harita");
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

        // b) İşlem seçenekleri ComboBox
        String[] islemler = {
                "1. Gitmek İstediğim durağa en kısa yol",
                "2. Otobüs Duraklarının isimlerine bakma",
                "3. Tramvay Duraklarının isimlerine bakma",
                "4. Sadece Otobüs ile gitmek için yol",
                "5. Sadece Tramvay ile gitmek için yol"
        };
        islemCombo = new JComboBox<>(islemler);
        islemCombo.setMaximumSize(new Dimension(200, 30));
        islemPanel.add(islemCombo);

        // c) Çalıştır butonu
        calistirButton = new JButton("Çalıştır");
        calistirButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        islemPanel.add(calistirButton);

        // Butonun tıklanma olayı: Çalıştır butonuna basıldığında işlem sonucu outputArea'ya yazılsın
        calistirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder output = new StringBuilder();
                int secilenIslem = islemCombo.getSelectedIndex() + 1;
                switch (secilenIslem) {
                    case 2:
                        output.append("2. Otobüs Duraklarının ismine bakma\n");
                        String busInfo = routeFinder.getAllBusInfo();
                        output.append(busInfo).append("\n");
                        System.out.println(busInfo);
                        JOptionPane.showMessageDialog(MainFrame.this, "İşlem Başarılı");
                        break;
                    case 3:
                        output.append("3. Tramvay Duraklarının ismine bakma\n");
                        String tramInfo = routeFinder.getAllTramInfo();
                        System.out.println(tramInfo);
                        output.append(tramInfo).append("\n");
                        JOptionPane.showMessageDialog(MainFrame.this,"İşlem Başarılı");
                        break;
                    case 1:
                    case 4:
                    case 5:
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
                            output.append("Hata! Girdiğiniz durak listede bulunmuyor.\n");
                            System.out.println("Hata! Girdiğiniz durak listede bulunmuyor.");
                            outputArea.setText(output.toString());
                            return;
                        }
                        if (secilenIslem == 1) {
                            output.append("1. Gitmek İstediğim durağa olan en kısa yol\n");
                            routeService.findAndPrintRoute(nearestDurak.getId(), hedefDurak.getId());
                            output.append(routeService.printRouteDetails(nearestDurak.getId(), hedefDurak.getId()));
                        }
                        else if(secilenIslem == 4){
                            output.append("4. Sadece Otobüs ile gitmek için yol (TRANSFERSİZ)\n");
                            routeFinder.getOnlyBusRoute(nearestDurak.getId(), hedefDurak.getId());
                            output.append(routeFinder.getOnlyBusRouteInfo(nearestDurak.getId(), hedefDurak.getId()));
                        } else if (secilenIslem == 5) {
                            output.append("5. Sadece tramvay ile gitmek için yol (TRANSFERSİZ)\n");
                            System.out.println(routeFinder.getOnlyTramRouteString(nearestDurak.getId(), hedefDurak.getId()));
                            output.append(routeFinder.getOnlyTramRouteString(nearestDurak.getId(), hedefDurak.getId()));
                        }
                        output.append("\nİşlem tamamlandı.\n");
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "İşlem tamamlandı.");
                        break;
                    default:
                        output.append("Hatalı seçim\n");
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "Hatalı seçim yaptınız.");
                }
                // outputArea'ya sonucu yaz
                outputArea.setText(output.toString());
            }
        });

        // -------------------------------------------------------------------
        // Panelleri ana pencereye ekleyelim (BorderLayout)
        // -------------------------------------------------------------------
        add(profilPanel, BorderLayout.WEST);   // Sol kısım: Profil paneli
        add(haritaPanel, BorderLayout.CENTER);  // Ortada: Harita paneli
        add(islemPanel, BorderLayout.EAST);     // Sağda: İşlem paneli

        setVisible(true);
    }

    // Kullanıcı tipi seçimine göre indirim bilgilerini güncelleyen yardımcı metot
    private void updateDiscountLabels(JLabel header, JLabel value, String userType) {
        String discount;
        if (userType.equals("Öğrenci")) {
            discount = "%20";
        } else if (userType.equals("Yaşlı")) {
            discount = "%30";
        } else {
            discount = "%0";
        }
        header.setText(userType + " için:");
        value.setText("indirim : " + discount);
    }
}
