import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteFinder {
    private Map<String, Durak> durakMap;
    private double minCost = Double.MAX_VALUE;
    private List<String> bestPath = new ArrayList<>();

    /**
     * Constructor, Durak listesi alır, durakMap oluşturur.
     */
    public RouteFinder(List<Durak> durakList) {
        durakMap = new HashMap<>();
        for (Durak d : durakList) {
            durakMap.put(d.getId(), d);
        }
    }

    public Durak getDurakById(String id) {
        return durakMap.get(id);
    }

    /**
     * Belirtilen startId ve endId arasındaki
     * en düşük ücretli rotayı bulur ve ekrana yazdırır.
     */
    public void findMinCostRoute(String startId, String endId) {
        minCost = Double.MAX_VALUE;
        bestPath.clear();

        // Şu anki rota path'ini tutacak liste
        List<String> currentPath = new ArrayList<>();
        currentPath.add(startId);

        // DFS ile en ucuz rotayı aramaya başla
        dfs(startId, endId, 0.0, currentPath);

        if (bestPath.isEmpty()) {
            System.out.println("Rota bulunamadı!");
        } else {
            System.out.println("En düşük ücret: " + minCost);
            System.out.println("Rota: " + bestPath);
        }
    }

    /**
     * DFS (Depth-First Search) ile startId'den endId'ye kadar
     * olası tüm yolları dolaşır ve en ucuz olanı bulur.
     */
    private void dfs(String currentId, String endId, double currentCost, List<String> currentPath) {
        // Eğer hedef durağa ulaştıysak, mevcut maliyetin minCost'tan düşük olup olmadığına bakarız.
        if (currentId.equals(endId)) {
            if (currentCost < minCost) {
                minCost = currentCost;
                bestPath = new ArrayList<>(currentPath);
            }
            return;
        }

        // Geçerli durağı al
        Durak currentDurak = durakMap.get(currentId);
        if (currentDurak == null) {
            // Geçersiz bir durak ID'si
            return;
        }

        // 1) Durağın nextStops listesini dolaş
        if (currentDurak.getNextStops() != null) {
            for (NextStop ns : currentDurak.getNextStops()) {
                String nextId = ns.getStopId();
                double nextCost = currentCost + ns.getUcret(); // Ücret ekle

                // Aynı rotada tekrar aynı durağa gitmemek için kontrol
                if (!currentPath.contains(nextId)) {
                    currentPath.add(nextId);
                    dfs(nextId, endId, nextCost, currentPath);
                    // Geri adım
                    currentPath.remove(currentPath.size() - 1);
                }
            }
        }

        // 2) Transfer varsa, onu da ayrı bir bağlantı olarak değerlendir
        Transfer tf = currentDurak.getTransfer();
        if (tf != null) {
            String transferId = tf.getTransferStopId();
            double transferCost = currentCost + tf.getTransferUcret();

            if (!currentPath.contains(transferId)) {
                currentPath.add(transferId);
                dfs(transferId, endId, transferCost, currentPath);
                currentPath.remove(currentPath.size() - 1);
            }
        }
    }

    // Getter'lar (opsiyonel)
    public double getMinCost() {
        return minCost;
    }

    public List<String> getBestPath() {
        return bestPath;
    }

    /**
     * İki Durak arasındaki mesafeyi hesaplayan haversineDistance.
     */
    public static double haversineDistance(Durak d1, Durak d2) {
        if (d1 == null || d2 == null) {
            throw new IllegalArgumentException("D1 veya D2 null olamaz.");
        }
        return haversineTaxiDistance(d1.getLat(), d1.getLon(), d2.getLat(), d2.getLon());
    }

    /**
     * Kullanıcı ve durak koordinatlarını (4 double) alarak haversine mesafesini hesaplar.
     */
    public static double haversineTaxiDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Dünya yarıçapı (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }


    /**
     * Kullanıcı konumunu (userLat, userLon) ve belirli bir durak nesnesinin koordinatlarını kullanarak taksi ücretini hesaplar.
     */
    public double calculateTaxiCost(double userLat, double userLon, Durak durak, Taxi taxi) {
        // Kullanıcı ile durak arasındaki mesafeyi haversineTaxiDistance kullanarak hesapla
        double distanceKm = haversineTaxiDistance(userLat, userLon, durak.getLat(), durak.getLon());
        double cost = taxi.getOpeningFee() + (distanceKm * taxi.getCostPerKm());
        return cost;
    }

    /**
     * Kullanıcının konumu (userLat, userLon) ile tüm duraklar arasında en yakın olanı bulur.
     */
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
