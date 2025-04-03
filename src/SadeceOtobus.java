import java.util.*;

public class SadeceOtobus implements TransportStrategy{
    private Map<String, Durak> durakMap;
    public SadeceOtobus(List<Durak> durakList) {
        durakMap = new HashMap<>();
        for (Durak d : durakList) {
            durakMap.put(d.getId(), d);
        }
    }
    public List<String> getOnlyBusRoute(String from, String to) {
        List<String> busRoute = new ArrayList<>();
        if (!durakMap.containsKey(from) || !durakMap.containsKey(to)) {
            System.out.println("❌ Hatalı durak ID'si!");
            return busRoute;
        }
        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(Collections.singletonList(from));
        visited.add(from);
        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String lastStop = path.get(path.size() - 1);
            if (lastStop.equals(to)) {
                return new ArrayList<>(path);
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
        return busRoute;
    }

    @Override
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
            boolean foundNextStop = false;
            for (NextStop nextStop : currentDurak.getNextStops()) {
                if (nextStop.getStopId().equals(path.get(i + 1))) {
                    totalCost += nextStop.getUcret();
                    foundNextStop = true;
                    break;
                }
            }
            if (!foundNextStop) {
                System.out.println("❌ İlgili geçiş bulunamadı: " + currentDurak.getId() + " -> " + path.get(i + 1));
            }
        }
        String lastStop = path.get(path.size() - 1);
        Durak currentDurak = durakMap.get(lastStop);
        if (currentDurak != null && currentDurak.getTransfer() != null) {
            Transfer transfer = currentDurak.getTransfer();
            String transferStopId = transfer.getTransferStopId();
            if (path.contains(transferStopId)) {
                totalCost += transfer.getTransferUcret();
            }
        }
        for (int i = 0; i < path.size() - 1; i++) {
            currentDurak = durakMap.get(path.get(i));
            if (currentDurak == null || currentDurak.getNextStops() == null) {
                continue;
            }
            if (currentDurak.getTransfer() != null) {
                Transfer transfer = currentDurak.getTransfer();
                String transferStopId = transfer.getTransferStopId();
                if (path.contains(transferStopId)) {
                    totalCost += transfer.getTransferUcret();
                }
            }
        }
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
            } else if (isTransfer) {
                totalTime += transfer.getTransferSure();
            }
        }
        return totalTime;
    }
    @Override
    public List<String> findRoute(String start, String destination) {
        return getOnlyBusRoute(start, destination);
    }



}
