import java.util.*;

public class RouteFinder {
    private Map<String, Durak> durakMap;
    // Getter metodu ekleyin
    public RouteFinder(ArrayList<Durak> durakList) {
        durakMap = new HashMap<>();
        for (Durak d : durakList) {
            durakMap.put(d.getId(), d);
        }
    }
    public String getAllBusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("🚌 Otobüs Durakları ve Bağlantıları:\n");
        for (Durak d : durakMap.values()) {
            if (d.getType().equals("bus")) {
                sb.append("📍 ")
                        .append(d.getId())
                        .append(" → ");
                List<String> busNextStops = new ArrayList<>();
                if (d.getNextStops() != null) {
                    for (NextStop ns : d.getNextStops()) {
                        Durak nextDurak = durakMap.get(ns.getStopId());
                        if (nextDurak != null && nextDurak.getType().equals("bus")) {
                            busNextStops.add(nextDurak.getId());
                        }
                    }
                }
                if (busNextStops.isEmpty()) {
                    sb.append("Son Durak\n");
                } else {
                    sb.append(String.join(", ", busNextStops)).append("\n");
                }
            }
        }
        return sb.toString();
    }
    public String getAllTramInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("🚊 Tramvay Durakları ve Bağlantıları:\n");
        for (Durak d : durakMap.values()) {
            if (d.getType().equals("tram")) {
                sb.append("📍 ")
                        .append(d.getId())
                        .append(" → ");

                List<String> tramNextStops = new ArrayList<>();
                if (d.getNextStops() != null) {
                    for (NextStop ns : d.getNextStops()) {
                        Durak nextDurak = durakMap.get(ns.getStopId());
                        // Sadece tramvay durağına giden bağlantıları listeleyelim
                        if (nextDurak != null && nextDurak.getType().equals("tram")) {
                            tramNextStops.add(nextDurak.getId());
                        }
                    }
                }
                // Eğer sonraki tramvay durağı yoksa "Son Durak" yazdır
                if (tramNextStops.isEmpty()) {
                    sb.append("Son Durak\n");
                } else {
                    sb.append(String.join(", ", tramNextStops)).append("\n");
                }
            }
        }
        return sb.toString();
    }
    public double haversineDistance(Durak d1, Durak d2) {
        if (d1 == null || d2 == null) {
            throw new IllegalArgumentException("D1 veya D2 null olamaz.");
        }
        return haversineTaxiDistance(d1.getLat(), d1.getLon(), d2.getLat(), d2.getLon());
    }
    public double haversineTaxiDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Dünya yarıçapı (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    public double calculateTaxiCost(double userLat, double userLon, Durak durak, Taxi taxi) {
        // Kullanıcı ile durak arasındaki mesafeyi haversineTaxiDistance kullanarak hesapla
        double distanceKm = haversineTaxiDistance(userLat, userLon, durak.getLat(), durak.getLon());
        double cost = taxi.getOpeningFee() + (distanceKm * taxi.getCostPerKm());
        return cost;
    }
    public Durak findNearestDurak(double userLat, double userLon) {
        Durak nearest = null;
        double minDistance = Double.MAX_VALUE;

        // Tüm durakları dolaşarak en küçük mesafeyi bulan döngü
        for (Durak d : durakMap.values()) {
            double distance = haversineTaxiDistance(userLat, userLon, d.getLat(), d.getLon());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = d;
            }
        }
        return nearest;
    }
}
