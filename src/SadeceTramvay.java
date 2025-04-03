import java.util.*;

public class SadeceTramvay implements TransportStrategy  {
    private Map<String, Durak> durakMap;
    public SadeceTramvay(List<Durak> durakList) {
        durakMap = new HashMap<>();
        for (Durak d : durakList) {
            durakMap.put(d.getId(), d);
        }
    }
    public List<String> getOnlyTramRoute(String from, String to) {
        List<String> tramRoute = new ArrayList<>();
        if (!durakMap.containsKey(from) || !durakMap.containsKey(to)) {
            return tramRoute;
        }
        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(new ArrayList<>(Arrays.asList(from)));
        visited.add(from);
        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String lastStop = path.get(path.size() - 1);
            if (lastStop.equals(to)) {
                tramRoute = new ArrayList<>(path); // Rotayı kaydet
                return tramRoute; // Rotayı döndür
            }
            Durak currentDurak = durakMap.get(lastStop);
            if (currentDurak == null || currentDurak.getNextStops() == null) continue;
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
        return tramRoute;
    }
    public double calculateTotalCost(List<String> path, String userType) {
        if (path == null || path.isEmpty()) {
            System.out.println("❌ Rota boş, işlem yapılamaz!");
            return 0.0;
        }
        double totalCost = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Durak currentDurak = durakMap.get(path.get(i));
            if (currentDurak == null || currentDurak.getNextStops() == null) {
                System.out.println("❌ Geçerli durak bulunamadı veya sonraki duraklar mevcut değil: " + path.get(i));
                continue;
            }
            for (NextStop nextStop : currentDurak.getNextStops()) {
                if (nextStop.getStopId().equals(path.get(i + 1))) {
                    totalCost += nextStop.getUcret();
                    break;
                }
            }
        }
        String currentStop = path.get(path.size() - 1);
        Durak currentDurak = durakMap.get(currentStop);
        if (currentDurak != null && currentDurak.getTransfer() != null) {
            Transfer transfer = currentDurak.getTransfer();
            String transferStopId = transfer.getTransferStopId();
            if (path.contains(transferStopId)) {
                totalCost += transfer.getTransferUcret();
            }
        }
        if (userType != null) {
            if (userType.equals("Ogrenci")) {
                totalCost *= 0.8; // Öğrencilere %20 indirim
            } else if (userType.equals("Yasli")) {
                totalCost *= 0.7; // Yaşlılara %30 indirim
            }
        }
        return totalCost;
    }

    public int calculateTotalTime(List<String> path) {
        int totalTime = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String currentStopId = path.get(i);
            String nextStopId = path.get(i + 1);
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
    public List<String> findRoute(String start, String destination) {
        return getOnlyTramRoute(start, destination);
    }

}
