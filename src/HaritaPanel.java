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

        if (durakList == null || durakList.isEmpty()) {
            g.drawString("Durak bulunamadı.", 10, 20);
            return;
        }

        double latRangeCurrent = (latRange == 0) ? 1 : (latRange / scaleFactor);
        double lonRangeCurrent = (lonRange == 0) ? 1 : (lonRange / scaleFactor);

        double minLatCurrent = centerLat - latRangeCurrent / 2.0;
        double maxLatCurrent = centerLat + latRangeCurrent / 2.0;
        double minLonCurrent = centerLon - lonRangeCurrent / 2.0;
        double maxLonCurrent = centerLon + lonRangeCurrent / 2.0;

        double latRangeUsed = maxLatCurrent - minLatCurrent;
        double lonRangeUsed = maxLonCurrent - minLonCurrent;

        if (backgroundImage != null) {
            int bgX1 = (int) (margin + ((minLon - minLonCurrent) / lonRangeUsed) * usableWidth);
            int bgY1 = (int) (margin + ((minLat - minLatCurrent) / latRangeUsed) * usableHeight);
            int bgX2 = (int) (margin + ((maxLon - minLonCurrent) / lonRangeUsed) * usableWidth);
            int bgY2 = (int) (margin + ((maxLat - minLatCurrent) / latRangeUsed) * usableHeight);
            g.drawImage(backgroundImage, bgX1, bgY1, bgX2 - bgX1, bgY2 - bgY1, this);
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));

        // Bağlantıları çizelim (Bağlantı çizgilerini yalnızca bir kez çiziyoruz)
        g2.setColor(Color.GREEN);
        for (Durak d : durakList) {
            double lat = d.getLat();
            double lon = d.getLon();
            double xRatio = (lon - minLonCurrent) / lonRangeUsed;
            double yRatio = (lat - minLatCurrent) / latRangeUsed;
            int x1 = (int) (margin + xRatio * usableWidth);
            int y1 = (int) (margin + yRatio * usableHeight);

            // Bağlantı çizgisi
            if (d.getNextStops() != null) {
                for (NextStop ns : d.getNextStops()) {
                    for (Durak d2 : durakList) {
                        if (d2.getId().equals(ns.getStopId())) {
                            double lat2 = d2.getLat();
                            double lon2 = d2.getLon();
                            double xRatio2 = (lon2 - minLonCurrent) / lonRangeUsed;
                            double yRatio2 = (lat2 - minLatCurrent) / latRangeUsed;
                            int x2 = (int) (margin + xRatio2 * usableWidth);
                            int y2 = (int) (margin + yRatio2 * usableHeight);
                            g2.drawLine(x1, y1, x2, y2);

                            // Süreyi yazdır
                            String sure = ns.getSure() + " dk";
                            int midX = (x1 + x2) / 2;
                            int midY = (y1 + y2) / 2;
                            g2.setColor(Color.green);
                            g2.setFont(new Font("Arial", Font.BOLD, 15));
                            g2.drawString(sure, midX, midY);
                            break;
                        }
                    }
                }
            }

            // Transfer çizgilerini çizerken farklı bir renk kullanıyoruz
            if (d.getTransfer() != null) {
                String transferId = d.getTransfer().getTransferStopId();
                for (Durak d2 : durakList) {
                    if (d2.getId().equals(transferId)) {
                        double lat2 = d2.getLat();
                        double lon2 = d2.getLon();
                        double xRatio2 = (lon2 - minLonCurrent) / lonRangeUsed;
                        double yRatio2 = (lat2 - minLatCurrent) / latRangeUsed;
                        int x2 = (int) (margin + xRatio2 * usableWidth);
                        int y2 = (int) (margin + yRatio2 * usableHeight);
                        g2.setColor(Color.ORANGE);  // Transfer çizgileri için turuncu renk
                        g2.drawLine(x1, y1, x2, y2);
                        g2.setColor(Color.GREEN);  // Transfer çizgisinin üzerine tekrar yeşil renk
                        break;
                    }
                }
            }
        }

        // Durak noktalarını çizelim
        for (Durak d : durakList) {
            double lat = d.getLat();
            double lon = d.getLon();

            double xRatio = (lon - minLonCurrent) / lonRangeUsed;
            double yRatio = (lat - minLatCurrent) / latRangeUsed;

            int x = (int) (margin + xRatio * usableWidth);
            int y = (int) (margin + yRatio * usableHeight);

            int pointSize = 8;
            if (currentDurak != null && currentDurak.equals(d)) {
                g2.setColor(Color.BLUE);  // Aktif durak için mavi renk
            } else {
                g2.setColor(Color.RED);  // Diğer duraklar için kırmızı renk
            }
            g2.fillOval(x - pointSize / 2, y - pointSize / 2, pointSize, pointSize);

            g2.setColor(Color.BLACK);
            g2.drawString(d.getId(), x + 6, y);
        }
    }
}
