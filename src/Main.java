import com.google.gson.Gson;
import com.google.gson.internal.bind.util.ISO8601Utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // Json Dosyasını Okuma
        String jsonPath = "veriseti.json";
        Root root = JsonReader.readJson(jsonPath);
        Scanner scanner = new Scanner(System.in);
        // Kullanıcının girdiği enlemlere göre en yakın durağı bulma
        double userLat = 40.76;
        double userLon = 30;
                                    // Konum için
        UserLocationHandler locationHandler = new UserLocationHandler(root.getDuraklar(), root.getTaxi());
        Durak nearestDurak = locationHandler.findNearestDurak(userLat, userLon);
        double enYakinDurakMesafe = locationHandler.getDistanceToDurak(userLat, userLon, nearestDurak);
        // Taksi ücreti bilgisi
        Taxi taxiInfo = root.getTaxi();
        // RouteFinder örneği oluştur
        RouteFinder routeFinder = new RouteFinder(root.getDuraklar());
        // Kullanıcı başlangıç bilgileri :
        System.out.println("‼️‼️‼️Bulunduğun durak sana en yakın olan "+nearestDurak.getId()+" olarak belirlenmiştir‼️‼️‼️");
        System.out.println("👉 " + nearestDurak.getId() + " ➡️ " +
                String.format("%.1f km", enYakinDurakMesafe) + " Uzaklıkta (Yürüme🚶‍♂️)");
        boolean taksiCagir = false;
        if(enYakinDurakMesafe > 3){
            System.out.println("️️ - - En yakın durağa olan mesafeniz 3 km den büyük olduğu için taksi çağırılıyor️️");
            taksiCagir = true;
        }
        // Son durakta bulunma Kontrolü
        if(nearestDurak.getNextStops().isEmpty()){
            System.out.println("!!! Bulunduğunuz durak son durak bu duraktan işlem yapamazsınız !!!");
            return;
        }
        System.out.println("En yakın durağa olan taksi ücreti : " +
                String.format("%.2f TL",
                        routeFinder.calculateTaxiCost(userLat, userLon, nearestDurak, taxiInfo)));
        RouteService routeService = new RouteService(root.getDuraklar());
        /*                                İLK DURAK BULUNDUKTAN SONRAKİ İŞLEMLER                                                     */
        System.out.println("-Gitmek istediğiniz durağın ismini yazınız .");
        String hedefDurakisim = scanner.nextLine();
        double hedefDurakMesafe ;
        Durak hedefDurak = null;
        boolean durakVarMi = false;
        for (Durak durak : root.getDuraklar()) {
            if (durak.getId().equalsIgnoreCase(hedefDurakisim)) {
                durakVarMi = true;
                hedefDurak = durak;
                break;
            }
        }
        if(!durakVarMi){
            System.out.println("Hata! Girdiğiniz durak listede bulunmuyor.");
            return;
        }
        System.out.println("Lütfen yapmak istediğiniz işlemi girin. \n"+
                "1.Gitmek İstediğim durağa olan en kısa yol\n"+
                "2.Otobüss Duraklarının ismine bakma.\n"+
                "3.Tramvay Duraklarının ismine bakma."
        );
        int islem = scanner.nextInt();
        switch (islem) {
            case 1:
                routeService.findAndPrintRoute(nearestDurak.getId(), hedefDurak.getId());
                break;
            case 2:
                routeFinder.getAllBus();
                break;
            case 3 :
                routeFinder.getAllTram();
                break;
            default:
                System.out.println("Hatalı Numara");
        }
    }
}
