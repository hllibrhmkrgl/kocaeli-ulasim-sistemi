import java.util.*;

public class RouteFinder implements PathFinder {
    private Map<String, Durak> durakMap;

    public RouteFinder(List<Durak> durakList) {
        durakMap = new HashMap<>();
        for (Durak d : durakList) {
            durakMap.put(d.getId(), d);
        }
    }

    @Override
    public List<String> findRoute(String start, String destination) {
        List<String> path = new ArrayList<>();

        // Eğer başlangıç ve bitiş noktası aynıysa direkt döndür
        if (start.equals(destination)) {
            path.add(start);
            return path;
        }

        // En kısa yolu bulmak için BFS algoritması
        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(Collections.singletonList(start));
        visited.add(start);

        while (!queue.isEmpty()) {
            List<String> currentPath = queue.poll();
            String lastStop = currentPath.get(currentPath.size() - 1);

            if (lastStop.equals(destination)) {
                return currentPath; // Hedef durağa ulaşıldı
            }

            Durak currentDurak = durakMap.get(lastStop);
            if (currentDurak == null || currentDurak.getNextStops() == null) continue;

            for (NextStop next : currentDurak.getNextStops()) {
                String nextStopId = next.getStopId();
                if (!visited.contains(nextStopId)) {
                    List<String> newPath = new ArrayList<>(currentPath);
                    newPath.add(nextStopId);
                    queue.add(newPath);
                    visited.add(nextStopId);
                }
            }
        }

        return path; // Eğer rota bulunamazsa boş liste döndür
    }

    @Override
    public double calculateTotalCost(List<String> path, String userType) {
        double totalCost = 0.0;
        for (String stopId : path) {
            Durak durak = durakMap.get(stopId);
            if (durak != null && durak.getNextStops() != null) {
                for (NextStop nextStop : durak.getNextStops()) {
                    totalCost += nextStop.getUcret();
                }
            }
        }
        // Kullanıcı tipine göre indirim uygula
        if (userType != null) {
            if (userType.equals("Ogrenci")) {
                totalCost *= 0.8; // %20 indirim
            } else if (userType.equals("Yasli")) {
                totalCost *= 0.7; // %30 indirim
            }
        }
        return totalCost;
    }

    @Override
    public int calculateTotalTime(List<String> path) {
        int totalTime = 0;
        for (String stopId : path) {
            Durak durak = durakMap.get(stopId);
            if (durak != null && durak.getNextStops() != null) {
                for (NextStop nextStop : durak.getNextStops()) {
                    totalTime += nextStop.getSure();
                }
            }
        }
        return totalTime;
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
                        if (nextDurak != null && nextDurak.getType().equals("tram")) {
                            tramNextStops.add(nextDurak.getId());
                        }
                    }
                }
                if (tramNextStops.isEmpty()) {
                    sb.append("Son Durak\n");
                } else {
                    sb.append(String.join(", ", tramNextStops)).append("\n");
                }
            }
        }
        return sb.toString();
    }

    public double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Dünya yarıçapı (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

}
