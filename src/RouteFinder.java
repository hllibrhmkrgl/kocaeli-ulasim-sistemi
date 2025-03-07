import java.util.*;

public class RouteFinder {
    private Map<String, Durak> durakMap;
    private double minCost = Double.MAX_VALUE;
    private ArrayList<String> bestPath = new ArrayList<>();

    /**
     * Constructor, Durak listesi alır, durakMap oluşturur.
     */
    public RouteFinder(ArrayList<Durak> durakList) {
        durakMap = new HashMap<>();
        for (Durak d : durakList) {
            durakMap.put(d.getId(), d);
        }
    }
    public void getOnlyBusRoute(String from, String to) {
        if (!durakMap.containsKey(from) || !durakMap.containsKey(to)) {
            System.out.println("❌ Hatalı durak ID'si!");
            return;
        }
        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(Collections.singletonList(from));
        visited.add(from);
        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String lastStop = path.get(path.size() - 1);
            if (lastStop.equals(to)) {
                bestPath = new ArrayList<>(path); // En iyi rotayı kaydet
                printRouteDetails(from, to); // Detaylı rota çıktısını göster
                return;
            }
            Durak currentDurak = durakMap.get(lastStop);
            if (currentDurak.getNextStops() != null) {
                for (NextStop ns : currentDurak.getNextStops()) {
                    Durak nextDurak = durakMap.get(ns.getStopId());
                    if (nextDurak != null && nextDurak.getType().equals("bus") && !visited.contains(nextDurak.getId())) {
                        List<String> newPath = new ArrayList<>(path);
                        newPath.add(nextDurak.getId());
                        queue.add(newPath);
                        visited.add(nextDurak.getId());
                    }
                }
            }
        }
        System.out.println("❌ Belirtilen otobüs rotası bulunamadı.");
    }
    public String getOnlyTramRouteString(String from, String to) {
        StringBuilder sb = new StringBuilder();
        // Durakların var olup olmadığını kontrol et
        if (!durakMap.containsKey(from) || !durakMap.containsKey(to)) {
            return "❌ Hatalı durak ID'si!";
        }
        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(new ArrayList<>(Arrays.asList(from))); // SingletonList yerine değiştirilebilir liste
        visited.add(from);
        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String lastStop = path.get(path.size() - 1);
            // Hedef durağa ulaşıldıysa en iyi rota olarak kaydet ve detayları döndür
            if (lastStop.equals(to)) {
                bestPath = new ArrayList<>(path);
                sb.append(printRouteDetailsInfo(from, to));
                return sb.toString();
            }
            Durak currentDurak = durakMap.get(lastStop);
            if (currentDurak == null || currentDurak.getNextStops() == null) continue; // null kontrolü eklendi
            for (NextStop ns : currentDurak.getNextStops()) {
                Durak nextDurak = durakMap.get(ns.getStopId());
                if (nextDurak != null && "tram".equals(nextDurak.getType()) && !visited.contains(nextDurak.getId())) {
                    List<String> newPath = new ArrayList<>(path);
                    newPath.add(nextDurak.getId());
                    queue.add(newPath);
                    visited.add(nextDurak.getId());
                }
            }
        }
        return "❌ Belirtilen tramvay rotası bulunamadı.";
    }

    public String getOnlyBusRouteInfo(String from, String to) {
        StringBuilder sb = new StringBuilder();
        if (!durakMap.containsKey(from) || !durakMap.containsKey(to)) {
            sb.append("❌ Hatalı durak ID'si!");
            return sb.toString();
        }
        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(Collections.singletonList(from));
        visited.add(from);
        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String lastStop = path.get(path.size() - 1);
            if (lastStop.equals(to)) {
                bestPath = new ArrayList<>(path); // En iyi rotayı kaydet
                sb.append(printRouteDetailsInfo(from, to)); // Detaylı rota çıktısını al ve ekle
                return sb.toString();
            }
            Durak currentDurak = durakMap.get(lastStop);
            if (currentDurak.getNextStops() != null) {
                for (NextStop ns : currentDurak.getNextStops()) {
                    Durak nextDurak = durakMap.get(ns.getStopId());
                    if (nextDurak != null && nextDurak.getType().equals("bus") && !visited.contains(nextDurak.getId())) {
                        List<String> newPath = new ArrayList<>(path);
                        newPath.add(nextDurak.getId());
                        queue.add(newPath);
                        visited.add(nextDurak.getId());
                    }
                }
            }
        }

        sb.append("❌ Belirtilen otobüs rotası bulunamadı.");
        return sb.toString();
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
    public Durak getDurakById(String id) {
        return durakMap.get(id);
    }
    public void printRouteDetails(String startId, String endId) {
        // Eğer bestPath boşsa, önce en ucuz rotayı bul
        if (bestPath.isEmpty()) {
            findMinCostRoute(startId, endId);
        }
        // Hâlâ boşsa artık rota gerçekten yok demektir
        if (bestPath.isEmpty()) {
            System.out.println("❌ Rota bulunamadı!");
            return;
        }
        System.out.println("\n📍 Rota Detayları: ");
        double totalCost = 0;
        double totalTime = 0;
        int step = 1;
        for (int i = 0; i < bestPath.size() - 1; i++) {
            String currentStopId = bestPath.get(i);
            String nextStopId = bestPath.get(i + 1);
            Durak currentDurak = durakMap.get(currentStopId);
            Durak nextDurak = durakMap.get(nextStopId);
            if (currentDurak == null || nextDurak == null) continue;
            NextStop selectedNextStop = null;
            if (currentDurak.getNextStops() != null) {
                for (NextStop ns : currentDurak.getNextStops()) {
                    if (ns.getStopId().equals(nextStopId)) {
                        selectedNextStop = ns;
                        break;
                    }
                }
            }
            Transfer transfer = currentDurak.getTransfer();
            boolean isTransfer = (transfer != null &&
                    transfer.getTransferStopId().equals(nextStopId));
            // Normal geçiş
            if (selectedNextStop != null) {
                totalCost += selectedNextStop.getUcret();
                totalTime += selectedNextStop.getSure();
                System.out.println(step + ". " + currentDurak.getId() +
                        " → " + nextDurak.getId() +
                        " (" + getTransportIcon(currentDurak) + ")");
                System.out.println("📏 Mesafe: " +
                        String.format("%.1f km", selectedNextStop.getMesafe()));
                System.out.println("⏳ Süre: " + selectedNextStop.getSure() + " dk");
                System.out.println("💰 Ücret: " +
                        String.format("%.2f TL", selectedNextStop.getUcret()));
            }
            // Transfer geçişi
            else if (isTransfer) {
                totalCost += transfer.getTransferUcret();
                totalTime += transfer.getTransferSure();
                System.out.println(step + ". " + currentDurak.getId() +
                        " → " + nextDurak.getId() + " (🔄 Transfer)");
                System.out.println("⏳ Süre: " + transfer.getTransferSure() + " dk");
                System.out.println("💰 Ücret: " +
                        String.format("%.2f TL", transfer.getTransferUcret()));
            }
            step++;
        }
        System.out.println("\n✅ Toplam Ücret: " + String.format("%.2f TL", totalCost)+" 💰");
        System.out.println("\n✅ Toplam Süre: " + String.format("%.2f Dk", totalTime)+" ⏳");
    }
    public String printRouteDetailsInfo(String startId, String endId) {
        StringBuilder sb = new StringBuilder();
        // Eğer bestPath boşsa, önce en ucuz rotayı bul
        if (bestPath.isEmpty()) {
            findMinCostRoute(startId, endId);
        }
        // Hâlâ boşsa artık rota gerçekten yok demektir
        if (bestPath.isEmpty()) {
            sb.append("❌ Rota bulunamadı!");
            return sb.toString();
        }
        sb.append("\n📍 Rota Detayları:\n");
        double totalCost = 0;
        double totalTime = 0;
        int step = 1;
        for (int i = 0; i < bestPath.size() - 1; i++) {
            String currentStopId = bestPath.get(i);
            String nextStopId = bestPath.get(i + 1);
            Durak currentDurak = durakMap.get(currentStopId);
            Durak nextDurak = durakMap.get(nextStopId);
            if (currentDurak == null || nextDurak == null) continue;
            NextStop selectedNextStop = null;
            if (currentDurak.getNextStops() != null) {
                for (NextStop ns : currentDurak.getNextStops()) {
                    if (ns.getStopId().equals(nextStopId)) {
                        selectedNextStop = ns;
                        break;
                    }
                }
            }
            Transfer transfer = currentDurak.getTransfer();
            boolean isTransfer = (transfer != null &&
                    transfer.getTransferStopId().equals(nextStopId));
            // Normal geçiş
            if (selectedNextStop != null) {
                totalCost += selectedNextStop.getUcret();
                totalTime += selectedNextStop.getSure();
                sb.append(step).append(". ").append(currentDurak.getId())
                        .append(" → ").append(nextDurak.getId())
                        .append(" (").append(getTransportIcon(currentDurak)).append(")\n");
                sb.append("📏 Mesafe: ")
                        .append(String.format("%.1f km", selectedNextStop.getMesafe())).append("\n");
                sb.append("⏳ Süre: ").append(selectedNextStop.getSure()).append(" dk\n");
                sb.append("💰 Ücret: ")
                        .append(String.format("%.2f TL", selectedNextStop.getUcret())).append("\n");
            }
            // Transfer geçişi
            else if (isTransfer) {
                totalCost += transfer.getTransferUcret();
                totalTime += transfer.getTransferSure();
                sb.append(step).append(". ").append(currentDurak.getId())
                        .append(" → ").append(nextDurak.getId()).append(" (🔄 Transfer)\n");
                sb.append("⏳ Süre: ").append(transfer.getTransferSure()).append(" dk\n");
                sb.append("💰 Ücret: ")
                        .append(String.format("%.2f TL", transfer.getTransferUcret())).append("\n");
            }
            step++;
        }
        sb.append("\n✅ Toplam Ücret: ").append(String.format("%.2f TL", totalCost)).append(" 💰\n");
        sb.append("✅ Toplam Süre: ").append(String.format("%.2f dk", totalTime)).append(" ⏳");
        return sb.toString();
    }
    // Durak adına göre taşıma türünü belirleyip emoji döndüren metot
    private String getTransportIcon(Durak durak) {
        String durakAdi = durak.getId().toLowerCase();
        if (durakAdi.contains("bus")) return "🚌 Otobüs";
        if (durakAdi.contains("tram")) return "🚋 Tramvay";
        if (durakAdi.contains("metro")) return "🚇 Metro";
        if (durakAdi.contains("ferry")) return "⛴️ Feribot";
        return "🚖 Taksi";
    }
    /**
     * Belirtilen startId ve endId arasındaki
     * en düşük ücretli rotayı bulur ve ekrana yazdırır.
     */
    public void findMinCostRoute(String startId, String endId) {
        minCost = Double.MAX_VALUE;
        bestPath.clear();
        // Şu anki rota path'ini tutacak liste
        ArrayList<String> currentPath = new ArrayList<>();
        currentPath.add(startId);
        // DFS ile en ucuz rotayı aramaya başla
        dfs(startId, endId, 0.0, currentPath);
        if (bestPath.isEmpty()) {
            System.out.println("Rota bulunamadı!");
        } else {
            System.out.println("En düşük ücret: " + minCost+" TL 💵");
            System.out.println("Rota: " + bestPath+" 🛣️");
        }
    }
    public String findMinCostRouteInfo(String startId, String endId) {
        minCost = Double.MAX_VALUE;
        bestPath.clear();
        ArrayList<String> currentPath = new ArrayList<>();
        currentPath.add(startId);
        dfs(startId, endId, 0.0, currentPath);
        if (bestPath.isEmpty()) {
            return "Rota bulunamadı!";
        } else {
            return "En düşük ücret: " + minCost + " TL 💵\nRota: " + bestPath + " 🛣️";
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
    public double haversineDistance(Durak d1, Durak d2) {
        if (d1 == null || d2 == null) {
            throw new IllegalArgumentException("D1 veya D2 null olamaz.");
        }
        return haversineTaxiDistance(d1.getLat(), d1.getLon(), d2.getLat(), d2.getLon());
    }
    /**
     * Kullanıcı ve durak koordinatlarını (4 double) alarak haversine mesafesini hesaplar.
     */
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
