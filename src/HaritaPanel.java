import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class HaritaPanel extends JPanel {

    private List<Durak> durakList;
    private Durak currentDurak;
    private Image busLogo;
    private Image tramLogo;
    private double minLat, maxLat, minLon, maxLon;
    private double centerLat, centerLon;
    private double latRange, lonRange;
    private double scaleFactor = 1.0;
    private Point lastDragPoint = null;
    private Image backgroundImage;
    public void setCurrentDurak(Durak currentDurak) {
        this.currentDurak = currentDurak;
        repaint();
    }
    public HaritaPanel(List<Durak> durakList) {
        busLogo = new ImageIcon("bus_logo.png").getImage();
        tramLogo = new ImageIcon("tram_logo.png").getImage();
        this.durakList = durakList;
        backgroundImage = new ImageIcon("izmit_map.jpg").getImage();
        minLat = 40.758;
        maxLat = 40.786;
        minLon = 29.918;
        maxLon = 29.973;
        latRange = maxLat - minLat;
        lonRange = maxLon - minLon;
        centerLat = (maxLat + minLat) / 2.0;
        centerLon = (maxLon + minLon) / 2.0;
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
                double latRangeCurrent = (latRange == 0) ? 1 : (latRange / scaleFactor);
                double lonRangeCurrent = (lonRange == 0) ? 1 : (lonRange / scaleFactor);
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
        g2.setColor(Color.GREEN);
        for (Durak d : durakList) {
            double lat = d.getLat();
            double lon = d.getLon();
            double xRatio = (lon - minLonCurrent) / lonRangeUsed;
            double yRatio = (lat - minLatCurrent) / latRangeUsed;
            int x1 = (int) (margin + xRatio * usableWidth);
            int y1 = (int) (margin + yRatio * usableHeight);
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
                        g2.setColor(Color.ORANGE);
                        g2.drawLine(x1, y1, x2, y2);
                        g2.setColor(Color.GREEN);
                        break;
                    }
                }
            }
        }
        for (Durak d : durakList) {
            double lat = d.getLat();
            double lon = d.getLon();
            double xRatio = (lon - minLonCurrent) / lonRangeUsed;
            double yRatio = (lat - minLatCurrent) / latRangeUsed;
            int x = (int) (margin + xRatio * usableWidth);
            int y = (int) (margin + yRatio * usableHeight);
            int pointSize = 8;
            if (currentDurak != null && currentDurak.equals(d)) {
                g2.setColor(Color.BLUE);
            } else {
                g2.setColor(Color.RED);
            }
            Image logoImage = null;
            if (d.getId().startsWith("bus_")) {
                logoImage = busLogo;
            } else if (d.getId().startsWith("tram_")) {
                logoImage = tramLogo;
            }
            int iconSize = 20;
            if (logoImage != null) {
                g.drawImage(logoImage, x - iconSize / 2, y - iconSize / 2, iconSize, iconSize, this);
            }
            g2.setColor(Color.BLACK);
            g2.drawString(d.getId(), x + 12, y - 5);
        }
    }
}
