import java.util.*;

public class YolBulucu {
    private Map<String, Durak> durakHaritasi;
    private Map<String, Double> minCost;
    private Map<String, String> previousStop;
    private Map<String, Integer> minTime;

    public YolBulucu(List<Durak> durakList) {
        durakHaritasi = new HashMap<>();
        for (Durak d : durakList) {
            durakHaritasi.put(d.getId(), d);
        }
    }

    public List<String> findCheapestPath(String startId, String endId) {
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
            for (NextStop next : currentDurak.getNextStops()) {
                double newCost = minCost.get(currentStop) + next.getUcret();
                int newTime = minTime.get(currentStop) + next.getSure();
                if (newCost < minCost.get(next.getStopId()) || newTime < minTime.get(next.getStopId())) {
                    minCost.put(next.getStopId(), newCost);
                    minTime.put(next.getStopId(), newTime);
                    previousStop.put(next.getStopId(), currentStop);
                    queue.add(next.getStopId());
                }
            }
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

    public List<String> findFastestPath(String startId, String endId) {
        minTime = new HashMap<>();
        previousStop = new HashMap<>();
        for (String stopId : durakHaritasi.keySet()) {
            minTime.put(stopId, Integer.MAX_VALUE);
        }
        minTime.put(startId, 0);
        PriorityQueue<String> queue =
                new PriorityQueue<>(Comparator.comparingInt(minTime::get));
        queue.add(startId);
        while (!queue.isEmpty()) {
            String currentStop = queue.poll();
            if (currentStop.equals(endId)) {
                break;
            }
            Durak currentDurak = durakHaritasi.get(currentStop);
            if (currentDurak == null) continue;
            for (NextStop next : currentDurak.getNextStops()) {
                int newTime = minTime.get(currentStop) + next.getSure();

                if (newTime < minTime.get(next.getStopId())) {
                    minTime.put(next.getStopId(), newTime);
                    previousStop.put(next.getStopId(), currentStop);
                    queue.add(next.getStopId());
                }
            }
            Transfer transfer = currentDurak.getTransfer();
            if (transfer != null) {
                String transferStopId = transfer.getTransferStopId();
                int newTime = minTime.get(currentStop) + transfer.getTransferSure();

                if (newTime < minTime.get(transferStopId)) {
                    minTime.put(transferStopId, newTime);
                    previousStop.put(transferStopId, currentStop);
                    queue.add(transferStopId);
                }
            }
        }

        List<String> path = new ArrayList<>();
        String current = endId;
        while (current != null) {
            path.add(current);
            current = previousStop.get(current);
        }
        Collections.reverse(path);

        return path;
    }

    public double calculateTotalCost(List<String> path, String userType) {
        if (path == null || path.isEmpty()) {
            System.out.println("❌ Rota boş, işlem yapılamaz!");
            return 0.0;
        }

        double totalCost = 0.0;

        for (int i = 0; i < path.size() - 1; i++) {
            Durak currentDurak = durakHaritasi.get(path.get(i));

            if (currentDurak == null || currentDurak.getNextStops() == null) {
                System.out.println("❌ Geçerli durak bulunamadı veya sonraki duraklar mevcut değil: " + path.get(i));
                continue;
            }

            boolean foundNextStop = false;
            for (NextStop nextStop : currentDurak.getNextStops()) {
                if (nextStop.getStopId().equals(path.get(i + 1))) {
                    totalCost += nextStop.getUcret();
                    foundNextStop = true;
                    break;
                }
            }

            if (!foundNextStop) {
                System.out.println("❌ Duraklar arasında geçiş bulunamadı: " + path.get(i) + " -> " + path.get(i + 1));
            }
        }

        String currentStop = path.get(path.size() - 1);
        Durak currentDurak = durakHaritasi.get(currentStop);

        if (currentDurak != null && currentDurak.getTransfer() != null) {
            Transfer transfer = currentDurak.getTransfer();
            String transferStopId = transfer.getTransferStopId();

            if (path.contains(transferStopId)) {
                totalCost += transfer.getTransferUcret();
            }
        }

        if (userType != null) {
            if (userType.equals("Ogrenci")) {
                totalCost *= 0.8;
            } else if (userType.equals("Yasli")) {
                totalCost *= 0.7;
            }
        }

        return totalCost;
    }

    public int calculateTotalTime(List<String> path) {
        int totalTime = 0;

        for (int i = 0; i < path.size() - 1; i++) {
            String currentStopId = path.get(i);
            String nextStopId = path.get(i + 1);
            Durak currentDurak = durakHaritasi.get(currentStopId);
            Durak nextDurak = durakHaritasi.get(nextStopId);

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
            boolean isTransfer = (transfer != null && transfer.getTransferStopId().equals(nextStopId));

            if (selectedNextStop != null) {
                totalTime += selectedNextStop.getSure();
            }
            else if (isTransfer) {
                totalTime += transfer.getTransferSure();
            }
        }

        return totalTime;
    }

}
