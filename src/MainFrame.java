import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private Root root;
    private UserLocationHandler locationHandler;
    private RouteFinder routeFinder;
    private YolBulucu yolBulucu;
    private Yazdırma yazdirma;
    private Coordinates coordinates;
    private SadeceOtobus sadeceOtobus;
    private SadeceTramvay sadeceTramvay;
    private Durak nearestDurak;
    private String userType = "Normal"; // Varsayılan kullanıcı tipi
    private Taxi taxi ;
    // Arayüz bileşenleri (Hedef durak giriş alanı, ComboBox, Buton)
    private JTextField hedefDurakField;
    private JComboBox<String> islemCombo;
    private JButton calistirButton;
    // Çıktıları göstermek için JTextArea
    private JTextArea outputArea;

    private MainFrameLogic mainFrameLogic;

    public MainFrame(Durak durak, Root root,
                     UserLocationHandler locationHandler,
                     RouteFinder routeFinder,
                     Durak nearestDurak,
                     Coordinates coordinates,
                     Taxi taxiInfo,
                     YolBulucu yolBulucu,
                     Yazdırma yazdirma,
                     SadeceOtobus sadeceotobus,
                     SadeceTramvay sadeceTramvay) {

        // Gelen parametreleri sakla
        this.root = root;
        this.locationHandler = locationHandler;
        this.routeFinder = routeFinder;
        this.yazdirma = yazdirma;
        this.sadeceOtobus = sadeceOtobus;
        this.sadeceTramvay = sadeceTramvay;
        this.yolBulucu = yolBulucu;
        this.nearestDurak = nearestDurak;
        this.coordinates = coordinates;
        this.taxi = taxiInfo;
        this.mainFrameLogic = new MainFrameLogic(
                this.root,
                this.locationHandler,
                this.routeFinder,
                this.nearestDurak,
                this.coordinates,
                this.taxi,
                this.yolBulucu,
                this.yazdirma,
                this.sadeceOtobus,
                this.sadeceTramvay
        );
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
        JLabel lblDurakBilgisi = new JLabel("Bulunduğun Durak: " +
                (nearestDurak != null ? nearestDurak.getId() : "Bilinmiyor"));

        JLabel lblKoordinat = new JLabel("Koordinatlar: " + coordinates.getUserLatGuncel() + " , " + coordinates.getUserLonGuncel());

        lblDurakBilgisi.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblDurakBilgisi.getPreferredSize().height));
        lblKoordinat.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblKoordinat.getPreferredSize().height));
        lblDurakBilgisi.setAlignmentX(Component.LEFT_ALIGNMENT);
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
                    userType = "Ogrenci";
                    mainFrameLogic.setUserType(userType);
                } else if (rbYasli.isSelected()) {
                    userType = "Yasli";
                    mainFrameLogic.setUserType(userType);
                } else {
                    userType = "Normal";
                    mainFrameLogic.setUserType(userType);
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
                "1. Gitmek İstediğim durağa en ucuz yol",
                "2. Otobüs Duraklarının isimlerine bakma",
                "3. Tramvay Duraklarının isimlerine bakma",
                "4. Sadece Otobüs ile gitmek için yol(Transfersiz)",
                "5. Sadece Tramvay ile gitmek için yol(Transfersiz)",
                "6. Konumdan Durağa Taksi Ücreti.",
                "7. Gitmek istediğin durağa en hızlı yol."
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
                int secilenIslem = islemCombo.getSelectedIndex() + 1;
                String hedefDurakIsmi = hedefDurakField.getText().trim();

                // ✅ Tüm mantığı mainFrameLogic'e devrediyoruz
                String result = mainFrameLogic.handleOperation(secilenIslem, hedefDurakIsmi, outputArea);
                outputArea.setText(result);
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
        if (userType.equals("Ogrenci")) {
            discount = "%20";
        } else if (userType.equals("Yasli")) {
            discount = "%30";
        } else {
            discount = "%0";
        }
        header.setText(userType + " için:");
        value.setText("indirim : " + discount);
    }
}
