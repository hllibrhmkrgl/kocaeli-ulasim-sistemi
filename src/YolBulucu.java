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

    public List<String> findFastestPath(String startId, String endId) {
        // Her bir durak için en kısa süreyi tutacak map.
        minTime = new HashMap<>();
        // Bir önceki durağı tutarak, yol sonunda geriye dönük path oluşturacağız.
        previousStop = new HashMap<>();

        // Başlangıçta her durağın süresini “çok büyük” bir değere (∞) ayarlıyoruz
        for (String stopId : durakHaritasi.keySet()) {
            minTime.put(stopId, Integer.MAX_VALUE);
        }
        // Başlangıç noktasının süresi 0
        minTime.put(startId, 0);

        // PriorityQueue, en küçük "minTime" değeri olan durağı öncelikli alacak
        PriorityQueue<String> queue =
                new PriorityQueue<>(Comparator.comparingInt(minTime::get));
        queue.add(startId);

        while (!queue.isEmpty()) {
            String currentStop = queue.poll();

            // Hedef durağa ulaşmışsak döngüden çıkabiliriz
            if (currentStop.equals(endId)) {
                break;
            }

            // Mevcut durağa ait bilgileri alalım
            Durak currentDurak = durakHaritasi.get(currentStop);
            if (currentDurak == null) continue; // Güvenlik amacıyla null kontrolü

            // 1) "nextStops" içindeki duraklara bakarak yeni süre hesaplama
            for (NextStop next : currentDurak.getNextStops()) {
                // Mevcut durağa kadar gelen süre + şu anki bağlantının süresi
                int newTime = minTime.get(currentStop) + next.getSure();

                // Eğer hesapladığımız yeni süre, kayıtlardaki süreden daha kısaysa güncelle
                if (newTime < minTime.get(next.getStopId())) {
                    minTime.put(next.getStopId(), newTime);
                    previousStop.put(next.getStopId(), currentStop);
                    // PriorityQueue'ya ekleyerek sonraki adımda değerlendirilecek
                    queue.add(next.getStopId());
                }
            }

            // 2) Transfer varsa onu da dahil et
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

        // En hızlı yolu (en kısa süre) geriye dönük olarak path listesine ekle
        List<String> path = new ArrayList<>();
        String current = endId;
        while (current != null) {
            path.add(current);
            current = previousStop.get(current);
        }
        // Tersten eklediğimiz için path'i düzeltelim
        Collections.reverse(path);

        return path;
    }

    public double calculateTotalCost(List<String> path, String userType) {
        // Null veya boş rota kontrolü
        if (path == null || path.isEmpty()) {
            System.out.println("❌ Rota boş, işlem yapılamaz!");
            return 0.0;
        }

        double totalCost = 0.0;

        // Path'teki her durak için geçiş ücretlerini kontrol ediyoruz
        for (int i = 0; i < path.size() - 1; i++) {
            Durak currentDurak = durakHaritasi.get(path.get(i));

            // Null kontrolü yapıyoruz
            if (currentDurak == null || currentDurak.getNextStops() == null) {
                System.out.println("❌ Geçerli durak bulunamadı veya sonraki duraklar mevcut değil: " + path.get(i));
                continue; // Eğer geçerli durak yoksa veya sonraki duraklar yoksa bir sonraki durak için devam et
            }

            // Geçişi kontrol ediyoruz
            boolean foundNextStop = false;
            for (NextStop nextStop : currentDurak.getNextStops()) {
                if (nextStop.getStopId().equals(path.get(i + 1))) {
                    // Toplam ücreti ekliyoruz
                    totalCost += nextStop.getUcret();
                    foundNextStop = true;
                    break; // İlgili geçişi bulduktan sonra döngüden çıkıyoruz
                }
            }

            if (!foundNextStop) {
                System.out.println("❌ Duraklar arasında geçiş bulunamadı: " + path.get(i) + " -> " + path.get(i + 1));
            }
        }

        // Son durakta transfer var mı? Transfer ücreti eklenmeli.
        String currentStop = path.get(path.size() - 1); // Son durak
        Durak currentDurak = durakHaritasi.get(currentStop);

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
