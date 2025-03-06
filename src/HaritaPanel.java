import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class HaritaPanel extends JPanel {

    private List<Durak> durakList;
    private Durak currentDurak;  // Şu an bulunduğunuz durak

    // Gerçek min-max değerler (durakların enlem, boylam aralığı)
    private double minLat, maxLat, minLon, maxLon;

    // Tüm durakların "merkezi" (orta nokta)
    private double centerLat, centerLon;

    // Tüm durakların aralığı
    private double latRange, lonRange;

    // Zoom faktörü, başlangıçta 1 (normal)
    private double scaleFactor = 1.0;

    // Panning için başlangıç noktası
    private Point lastDragPoint = null;

    // Arka plan harita resmi (dosyadan veya kaynaklardan yüklenebilir)
    private Image backgroundImage;

    // currentDurak'ı ayarlamak için bir setter ekleyelim
    public void setCurrentDurak(Durak currentDurak) {
        this.currentDurak = currentDurak;
        repaint();
    }

    public HaritaPanel(List<Durak> durakList) {
        this.durakList = durakList;

        // Harita resmi yükleme (örneğin "harita.png" dosyası)
        backgroundImage = new ImageIcon("harita.png").getImage();

        if (durakList != null && !durakList.isEmpty()) {
            // İlk durak değerleriyle başla
            minLat = durakList.get(0).getLat();
            maxLat = durakList.get(0).getLat();
            minLon = durakList.get(0).getLon();
            maxLon = durakList.get(0).getLon();

            // Tüm durakların min-max lat-lon değerini bul
            for (Durak d : durakList) {
                double lat = d.getLat();
                double lon = d.getLon();

                if (lat < minLat) minLat = lat;
                if (lat > maxLat) maxLat = lat;
                if (lon < minLon) minLon = lon;
                if (lon > maxLon) maxLon = lon;
            }

            // Toplam aralık
            latRange = maxLat - minLat;
            lonRange = maxLon - minLon;

            // Merkez (ortalama) konum
            centerLat = (maxLat + minLat) / 2.0;
            centerLon = (maxLon + minLon) / 2.0;
        }

        // Zoom için mouse tekerleği dinleyicisi
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                if (notches < 0) {
                    scaleFactor *= 1.1;
                } else {
                    scaleFactor *= 0.9;
                }
                if (scaleFactor < 0.1) {
                    scaleFactor = 0.1;
                } else if (scaleFactor > 50) {
                    scaleFactor = 50;
                }
                repaint();
            }
        });

        // Panning için mouse basma ve sürükleme dinleyicileri
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastDragPoint = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point currentPoint = e.getPoint();
                int dx = currentPoint.x - lastDragPoint.x;
                int dy = currentPoint.y - lastDragPoint.y;

                int w = getWidth();
                int h = getHeight();
                int margin = 30;
                double usableWidth = w - 2.0 * margin;
                double usableHeight = h - 2.0 * margin;

                // Mevcut zoom'a göre gösterilen aralık
                double latRangeCurrent = (latRange == 0) ? 1 : (latRange / scaleFactor);
                double lonRangeCurrent = (lonRange == 0) ? 1 : (lonRange / scaleFactor);

                // Piksel farkını coğrafi farklara çeviriyoruz.
                double dLon = -dx / usableWidth * lonRangeCurrent;
                double dLat = -dy / usableHeight * latRangeCurrent;

                centerLon += dLon;
                centerLat += dLat;

                lastDragPoint = currentPoint;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int w = getWidth();
        int h = getHeight();
        int margin = 30;
        double usableWidth = w - 2.0 * margin;
        double usableHeight = h - 2.0 * margin;

        // Eğer durak yoksa, mesaj yazıp çıkalım
        if (durakList == null || durakList.isEmpty()) {
            g.drawString("Durak bulunamadı.", 10, 20);
            return;
        }

        // Zoom katsayısına göre "gösterilen" aralık
        double latRangeCurrent = (latRange == 0) ? 1 : (latRange / scaleFactor);
        double lonRangeCurrent = (lonRange == 0) ? 1 : (lonRange / scaleFactor);

        // Merkeze göre güncel min-max değerleri hesapla
        double minLatCurrent = centerLat - latRangeCurrent / 2.0;
        double maxLatCurrent = centerLat + latRangeCurrent / 2.0;
        double minLonCurrent = centerLon - lonRangeCurrent / 2.0;
        double maxLonCurrent = centerLon + lonRangeCurrent / 2.0;

        double latRangeUsed = maxLatCurrent - minLatCurrent;
        double lonRangeUsed = maxLonCurrent - minLonCurrent;

        // Arka plan resmi varsa, önce onu çizelim.
        if (backgroundImage != null) {
            int bgX1 = (int) (margin + ((minLon - minLonCurrent) / lonRangeUsed) * usableWidth);
            int bgY1 = (int) (margin + ((minLat - minLatCurrent) / latRangeUsed) * usableHeight);
            int bgX2 = (int) (margin + ((maxLon - minLonCurrent) / lonRangeUsed) * usableWidth);
            int bgY2 = (int) (margin + ((maxLat - minLatCurrent) / latRangeUsed) * usableHeight);

            g.drawImage(backgroundImage, bgX1, bgY1, bgX2 - bgX1, bgY2 - bgY1, this);
        }

        // Her durağı ekrana çiz
        for (Durak d : durakList) {
            double lat = d.getLat();
            double lon = d.getLon();

            // Oranlar
            double xRatio = (lon - minLonCurrent) / lonRangeUsed;
            double yRatio = (lat - minLatCurrent) / latRangeUsed;

            int x = (int) (margin + xRatio * usableWidth);
            int y = (int) (margin + yRatio * usableHeight);

            int pointSize = 8;
            // Eğer bu durak currentDurak ise, rengi mavi yapalım; değilse kırmızı
            if (currentDurak != null && currentDurak.equals(d)) {
                g.setColor(Color.BLUE);
            } else {
                g.setColor(Color.RED);
            }
            g.fillOval(x - pointSize / 2, y - pointSize / 2, pointSize, pointSize);

            g.setColor(Color.BLACK);
            g.drawString(d.getId(), x + 6, y);
        }
    }
}
