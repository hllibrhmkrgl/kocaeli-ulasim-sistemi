import com.google.gson.Gson;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        // JSON dosyasını oku
        String jsonPath = "veriseti.json";
        String jsonString = new String(Files.readAllBytes(Paths.get(jsonPath)));

        // Gson ile parse et
        Gson gson = new Gson();
        Root root = gson.fromJson(jsonString, Root.class);

        // Durak listesi al
        List<Durak> durakList = root.getDuraklar();
        // Taksi ücreti bilgisi
        Taxi taxiInfo = root.getTaxi();

        // RouteFinder örneği oluştur
        RouteFinder routeFinder = new RouteFinder(durakList);

        // EĞER İSTEDİĞİN Bİ DURAĞA ERİŞMEK İSTİYORSAN
        System.out.println(routeFinder.getDurakById("bus_otogar"));
        // KULLLAN

        // Örnek: bus_otogar -> bus_symbolavm arasında en düşük ücretli rota
        routeFinder.findMinCostRoute("bus_otogar", "bus_symbolavm");

        // Kullanıcının girdiği enlemlere göre en yakın durağı bulma
        double userLat = 40.765;
        double userLon = 29.950;

        // En yakın durağı bul
        Durak nearestDurak = routeFinder.findNearestDurak(userLat, userLon);
        if (nearestDurak != null) {
            // Taksiyle kaç TL tutacağını hesapla
            double cost = routeFinder.calculateTaxiCost(userLat, userLon, nearestDurak, taxiInfo);
            System.out.println("Kullanıcı konumu: (" + userLat + ", " + userLon + ")");
            System.out.println("En yakın durak: " + nearestDurak.getName() + " (ID:" + nearestDurak.getId() + ")");
            System.out.println("Taksi Ücreti: " + cost);
        }
    }
}
