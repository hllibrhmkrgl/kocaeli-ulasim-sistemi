import java.util.*;

public class YolBulucu {
    private Map<String, Durak> durakHaritasi;
    private Map<String, Double> minCost;
    private Map<String, String> previousStop;
    private Map<String, Integer> minTime; // En kısa süreyi tutmak için ekliyoruz

    public YolBulucu(List<Durak> durakList) {
        durakHaritasi = new HashMap<>();
        for (Durak d : durakList) {
            durakHaritasi.put(d.getId(), d);
        }
    }

    // En ucuz yolu bulma (Dijkstra algoritması)
    public List<String> findCheapestPath(String startId, String endId) {
        // Başlangıç duraklarını başlatıyoruz
        minCost = new HashMap<>();
        previousStop = new HashMap<>();
        minTime = new HashMap<>(); // Toplam süreyi tutmak için
        for (String stopId : durakHaritasi.keySet()) {
            minCost.put(stopId, Double.MAX_VALUE);
            minTime.put(stopId, Integer.MAX_VALUE); // Başlangıçta her zaman en yüksek süre
        }
        minCost.put(startId, 0.0);
        minTime.put(startId, 0);
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingDouble(minCost::get));
        queue.add(startId);
        while (!queue.isEmpty()) {
            String currentStop = queue.poll();
            if (currentStop.equals(endId)) break;
            Durak currentDurak = durakHaritasi.get(currentStop);
            // Duraklar arasındaki geçişleri kontrol ediyoruz
            for (NextStop next : currentDurak.getNextStops()) {
                double newCost = minCost.get(currentStop) + next.getUcret();
                int newTime = minTime.get(currentStop) + next.getSure();
                // Daha ucuz bir yol bulduysak, minCost ve minTime güncelleniyor
                if (newCost < minCost.get(next.getStopId()) || newTime < minTime.get(next.getStopId())) {
                    minCost.put(next.getStopId(), newCost);
                    minTime.put(next.getStopId(), newTime);
                    previousStop.put(next.getStopId(), currentStop);
                    queue.add(next.getStopId());
                }
            }
            // Transferler varsa, onları da hesaplıyoruz
            Transfer transfer = currentDurak.getTransfer();
            if (transfer != null) {
                String transferStopId = transfer.getTransferStopId();
                double transferCost = transfer.getTransferUcret();
                int transferTime = transfer.getTransferSure();
                double newCost = minCost.get(currentStop) + transferCost;
                int newTime = minTime.get(currentStop) + transferTime;
                if (newCost < minCost.get(transferStopId) || newTime < minTime.get(transferStopId)) {
                    minCost.put(transferStopId, newCost);
                    minTime.put(transferStopId, newTime);
                    previousStop.put(transferStopId, currentStop);
                    queue.add(transferStopId);
                }
            }
        }
        // En ucuz yolu geri almak
        List<String> path = new ArrayList<>();
        String currentStop = endId;
        while (currentStop != null) {
            path.add(currentStop);
            currentStop = previousStop.get(currentStop);
        }
        Collections.reverse(path);
        return path;
    }

    public double calculateTotalCost(List<String> path, String userType) {
        double totalCost = 0.0;
        // Path'teki her durak için geçişleri kontrol ediyoruz
        for (int i = 0; i < path.size() - 1; i++) {
            Durak currentDurak = durakHaritasi.get(path.get(i));
            for (NextStop nextStop : currentDurak.getNextStops()) {
                if (nextStop.getStopId().equals(path.get(i + 1))) {
                    // Toplam ücreti ekliyoruz
                    totalCost += nextStop.getUcret();
                    break;
                }
            }
        }
        // Transfer ücretlerini ekliyoruz
        String currentStop = path.get(path.size() - 1);
        Durak currentDurak = durakHaritasi.get(currentStop);
        Transfer transfer = currentDurak.getTransfer();
        if (transfer != null) {
            totalCost += transfer.getTransferUcret();
        }
        // Kullanıcı tipine göre indirimleri uyguluyoruz
        if (userType.equals("student")) {
            totalCost *= 0.8; // Öğrencilere %20 indirim
        } else if (userType.equals("elderly")) {
            totalCost *= 0.7; // Yaşlılara %30 indirim
        }
        // Toplam ücreti döndürüyoruz
        return totalCost;
    }
    // Toplam süreyi hesaplama (Transfer süreleri dahil)
    public int calculateTotalTime(List<String> path) {
        int totalTime = 0;

        // Path'teki her durak için geçişleri kontrol ediyoruz
        for (int i = 0; i < path.size() - 1; i++) {
            String currentStopId = path.get(i);
            String nextStopId = path.get(i + 1);
            Durak currentDurak = durakHaritasi.get(currentStopId);
            Durak nextDurak = durakHaritasi.get(nextStopId);

            if (currentDurak == null || nextDurak == null) continue;

            NextStop selectedNextStop = null;
            // Geçişleri kontrol ediyoruz
            if (currentDurak.getNextStops() != null) {
                for (NextStop ns : currentDurak.getNextStops()) {
                    if (ns.getStopId().equals(nextStopId)) {
                        selectedNextStop = ns;
                        break;
                    }
                }
            }

            Transfer transfer = currentDurak.getTransfer();
            boolean isTransfer = (transfer != null && transfer.getTransferStopId().equals(nextStopId));

            // Normal geçişin süresi
            if (selectedNextStop != null) {
                totalTime += selectedNextStop.getSure();
            }
            // Transfer geçişinin süresi
            else if (isTransfer) {
                totalTime += transfer.getTransferSure();
            }
        }

        return totalTime; // Toplam süreyi döndürüyoruz
    }




}
