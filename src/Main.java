import com.google.gson.Gson;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // JSON dosyasını oku
        String jsonPath = "veriseti.json";
        String jsonString = new String(Files.readAllBytes(Paths.get(jsonPath)));
        Scanner scanner = new Scanner(System.in);
        // Kullanıcının girdiği enlemlere göre en yakın durağı bulma
        double userLat = 40.765;
        double userLon = 29.950;

        // Taksi çağırmak için variable tanımla
        boolean taksiCagir = false;
        boolean durakVarMi = false;
        // Gson ile parse et
        Gson gson = new Gson();
        Root root = gson.fromJson(jsonString, Root.class);

        // Durak listesi al
        ArrayList<Durak> durakList = root.getDuraklar();

        // Taksi ücreti bilgisi
        Taxi taxiInfo = root.getTaxi();

        // RouteFinder örneği oluştur
        RouteFinder routeFinder = new RouteFinder(durakList);

        // En yakın durağı bul
        Durak nearestDurak = routeFinder.findNearestDurak(userLat, userLon);
        System.out.println("Bulunduğun durak sana en yakın olan "+nearestDurak.getId()+" olarak belirlenmiştir!!!!");
        System.out.println("Gitmek istediğiniz durağın ismini yazınız .");
        String hedefDurakisim = scanner.nextLine();
        Durak hedefDurak = null;
        // İstediğimiz durak geçerli bi durak mı onu ara
        for (Durak durak : durakList) {
            if (durak.getId().equalsIgnoreCase(hedefDurakisim)) {
                durakVarMi = true;
                hedefDurak = durak;
                break;
            }
        }
        // Durak var mı yok mu belirle var ise yolları kullanıcıya sun
        if (durakVarMi) {
            System.out.println("YOL = "+nearestDurak.getId() + " ➡️ "+hedefDurakisim);
            routeFinder.findMinCostRoute(nearestDurak.getId(),hedefDurak.getId());
        } else {
            System.out.println("Hata! Girdiğiniz durak listede bulunmuyor.");
        }

        // Bu aşadaki yer örnek kullanıcıya bulunan en yakın durağı bulur ve mesafesini ve tutucak ücreti söyler
        /*
        System.out.println("*****");
        System.out.println("Bulunduğunuz Konum : "+userLat+" "+userLon);
        System.out.println("Size olan en yakın durak : "+nearestDurak.getName()+" ve mesafe "+routeFinder.haversineTaxiDistance(userLat,userLon,nearestDurak.getLat(),nearestDurak.getLon())+"km");
        System.out.println("Ve taksi ücreti : "+routeFinder.calculateTaxiCost(userLat, userLon, nearestDurak, taxiInfo));
        System.out.println("Taksi çağırmak için 1 yazınız .");
        String cevap = scanner.nextLine();
        if(cevap.equals("1")){
            System.out.println("Taksi Çağırılıyor...");
            taksiCagir = true;
            userLat = nearestDurak.getLat();
            userLon = nearestDurak.getLon();
        }
        System.out.println(nearestDurak.getName()+" Durağına ulaşıldı "+"Şuanki konumunuz "+userLat+" "+userLon);
        */
    }
}
