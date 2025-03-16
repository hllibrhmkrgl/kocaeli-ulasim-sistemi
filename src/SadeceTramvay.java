import java.util.*;

public class SadeceTramvay implements TransportStrategy  {
    private Map<String, Durak> durakMap;

    // Durakları haritaya ekliyoruz
    public SadeceTramvay(List<Durak> durakList) {
        durakMap = new HashMap<>();
        for (Durak d : durakList) {
            durakMap.put(d.getId(), d);
        }
    }

    // Sadece tramvay rotasını döndüren fonksiyon
    public List<String> getOnlyTramRoute(String from, String to) {
        List<String> tramRoute = new ArrayList<>(); // Yolu tutacak liste

        // Durakların var olup olmadığını kontrol et
        if (!durakMap.containsKey(from) || !durakMap.containsKey(to)) {
            return tramRoute; // Hatalı durak ID'si durumunda boş liste döndür
        }

        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(new ArrayList<>(Arrays.asList(from))); // SingletonList yerine değiştirilebilir liste
        visited.add(from);

        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String lastStop = path.get(path.size() - 1);

            // Hedef durağa ulaşıldıysa rotayı döndür
            if (lastStop.equals(to)) {
                tramRoute = new ArrayList<>(path); // Rotayı kaydet
                return tramRoute; // Rotayı döndür
            }

            Durak currentDurak = durakMap.get(lastStop);
            if (currentDurak == null || currentDurak.getNextStops() == null) continue; // null kontrolü

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

        return tramRoute; // Eğer rota bulunamazsa boş liste döndür
    }

    // Tramvay rotası için toplam ücreti hesaplayan fonksiyon
    public double calculateTotalCost(List<String> path, String userType) {
        if (path == null || path.isEmpty()) {
            System.out.println("❌ Rota boş, işlem yapılamaz!");
            return 0.0;
        }
        double totalCost = 0.0;
        // Path'teki her durak için geçişleri kontrol ediyoruz
        for (int i = 0; i < path.size() - 1; i++) {
            Durak currentDurak = durakMap.get(path.get(i));
            // Null kontrolü yapıyoruz
            if (currentDurak == null || currentDurak.getNextStops() == null) {
                System.out.println("❌ Geçerli durak bulunamadı veya sonraki duraklar mevcut değil: " + path.get(i));
                continue;
            }
            for (NextStop nextStop : currentDurak.getNextStops()) {
                if (nextStop.getStopId().equals(path.get(i + 1))) {
                    // Toplam ücreti ekliyoruz
                    totalCost += nextStop.getUcret();
                    break; // İlgili geçişi bulduktan sonra döngüden çıkıyoruz
                }
            }
        }

        // Son durakta transfer var mı? Transfer ücreti eklenmeli.
        String currentStop = path.get(path.size() - 1);
        Durak currentDurak = durakMap.get(currentStop);

        if (currentDurak != null && currentDurak.getTransfer() != null) {
            Transfer transfer = currentDurak.getTransfer();
            String transferStopId = transfer.getTransferStopId();

            // Eğer transfer durak, rotadaki bir durakla eşleşiyorsa, transfer ücreti ekle
            if (path.contains(transferStopId)) {
                totalCost += transfer.getTransferUcret();
            }
        }

        // Kullanıcı tipine göre indirimleri uyguluyoruz
        if (userType != null) {
            if (userType.equals("Ogrenci")) {
                totalCost *= 0.8; // Öğrencilere %20 indirim
            } else if (userType.equals("Yasli")) {
                totalCost *= 0.7; // Yaşlılara %30 indirim
            }
        }

        // Toplam ücreti döndürüyoruz
        return totalCost;
    }



    // Tramvay rotası için toplam süreyi hesaplayan fonksiyon
    public int calculateTotalTime(List<String> path) {
        int totalTime = 0;
        // Path'teki her durak için geçişleri kontrol ediyoruz
        for (int i = 0; i < path.size() - 1; i++) {
            String currentStopId = path.get(i);
            String nextStopId = path.get(i + 1);
            Durak currentDurak = durakMap.get(currentStopId);
            Durak nextDurak = durakMap.get(nextStopId);
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
    public List<String> findRoute(String start, String destination) {
        return getOnlyTramRoute(start, destination);
    }

}
