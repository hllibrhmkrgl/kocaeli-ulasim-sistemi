import java.util.*;

public class SadeceOtobus {
    private Map<String, Durak> durakMap;

    // Durakları haritaya ekliyoruz
    public SadeceOtobus(List<Durak> durakList) {
        durakMap = new HashMap<>();
        for (Durak d : durakList) {
            durakMap.put(d.getId(), d);
        }
    }

    // Sadece otobüs rotasını bulma
    public List<String> getOnlyBusRoute(String from, String to) {
        List<String> busRoute = new ArrayList<>(); // Yolu tutacak liste

        // Başlangıç ve varış duraklarının geçerli olup olmadığını kontrol et
        if (!durakMap.containsKey(from) || !durakMap.containsKey(to)) {
            System.out.println("❌ Hatalı durak ID'si!");
            return busRoute; // Hatalı durak ID'si durumu
        }

        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(Collections.singletonList(from));
        visited.add(from);

        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String lastStop = path.get(path.size() - 1);

            // Varış durak bulunduğunda rotayı döndür
            if (lastStop.equals(to)) {
                busRoute = new ArrayList<>(path); // Yolu kaydet
                return busRoute;
            }

            Durak currentDurak = durakMap.get(lastStop);
            if (currentDurak.getNextStops() != null) {
                for (NextStop ns : currentDurak.getNextStops()) {
                    Durak nextDurak = durakMap.get(ns.getStopId());

                    // Eğer durak otobüs türündeyse ve ziyaret edilmediyse
                    if (nextDurak != null && nextDurak.getType().equals("bus") && !visited.contains(nextDurak.getId())) {
                        List<String> newPath = new ArrayList<>(path);
                        newPath.add(nextDurak.getId());
                        queue.add(newPath);
                        visited.add(nextDurak.getId());
                    }
                }
            }
        }

        // Eğer rota bulunamazsa boş liste döndür
        System.out.println("❌ Belirtilen otobüs rotası bulunamadı.");
        return busRoute;
    }

    // Toplam ücreti hesaplama
    public double calculateTotalCost(List<String> path, String userType) {
        if (path == null || path.isEmpty()) {
            System.out.println("❌ Rota boş, işlem yapılamaz!");
            return 0.0;
        }
        double totalCost = 0.0;
        // Path'teki her durak için geçişleri kontrol ediyoruz
        for (int i = 0; i < path.size() - 1; i++) {
            // Mevcut durak
            Durak currentDurak = durakMap.get(path.get(i));
            // Null kontrolü yapıyoruz
            if (currentDurak == null || currentDurak.getNextStops() == null) {
                System.out.println("❌ Geçerli durak bulunamadı veya sonraki duraklar mevcut değil: " + path.get(i));
                continue;
            }
            // Her geçiş için ücret ekliyoruz
            boolean foundNextStop = false;
            for (NextStop nextStop : currentDurak.getNextStops()) {
                if (nextStop.getStopId().equals(path.get(i + 1))) {
                    // Toplam ücreti ekliyoruz
                    totalCost += nextStop.getUcret();
                    foundNextStop = true;
                    break;
                }
            }
            if (!foundNextStop) {
                System.out.println("❌ İlgili geçiş bulunamadı: " + currentDurak.getId() + " -> " + path.get(i + 1));
            }
        }
        // Son durakta transfer var mı? Transfer ücreti eklenmeli.
        String lastStop = path.get(path.size() - 1);
        Durak currentDurak = durakMap.get(lastStop);
        if (currentDurak != null && currentDurak.getTransfer() != null) {
            Transfer transfer = currentDurak.getTransfer();
            String transferStopId = transfer.getTransferStopId();
            // Eğer transfer durak, rotadaki bir durakla eşleşiyorsa, transfer ücreti ekle
            if (path.contains(transferStopId)) {
                totalCost += transfer.getTransferUcret();
            }
        }
        // **Aktarma duraklarında** transfer ücretini sadece geçiş yapıldığında ekliyoruz
        for (int i = 0; i < path.size() - 1; i++) {
            // Mevcut durak
            currentDurak = durakMap.get(path.get(i));
            // Null kontrolü yapıyoruz
            if (currentDurak == null || currentDurak.getNextStops() == null) {
                continue;
            }
            // Aktarma durakları için kontrol ekliyoruz
            if (currentDurak.getTransfer() != null) {
                Transfer transfer = currentDurak.getTransfer();
                String transferStopId = transfer.getTransferStopId();
                // Eğer şu anki durak, transfer noktasıysa ve rotada transfer yapılacak durak varsa
                if (path.contains(transferStopId)) {
                    totalCost += transfer.getTransferUcret();
                }
            }
        }
        // Kullanıcı tipine göre indirimleri uyguluyoruz
        if (userType != null) {
            if (userType.equals("student")) {
                totalCost *= 0.8; // Öğrencilere %20 indirim
            } else if (userType.equals("elderly")) {
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
}
