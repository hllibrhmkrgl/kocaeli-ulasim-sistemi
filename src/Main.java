import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CoordinateInputFrame();  // İlk önce koordinat alma ekranını açar
        });
    }

    // Kullanıcı koordinatları girdikten sonra çağrılacak metod:
    public static void startMainApplication(double userLat, double userLon) {
        SwingUtilities.invokeLater(() -> {
            try {
                String jsonPath = "veriseti.json";
                Root root = JsonReader.readJson(jsonPath);
                Taxi taxi = new Taxi();
                Coordinates coordinates = new Coordinates();
                coordinates.setUserLatGirilen(userLat);
                coordinates.setUserLonGirilen(userLon);
                UserLocationHandler locationHandler = new UserLocationHandler(root.getDuraklar(), root.getTaxi());
                Durak nearestDurak = locationHandler.findNearestDurak(coordinates.getUserLatGirilen(), coordinates.getUserLonGirilen());
                RouteFinder routeFinder = new RouteFinder(root.getDuraklar());
                YolBulucu yolBulucu = new YolBulucu(root.getDuraklar());
                coordinates.setUserLatGuncel(nearestDurak.getLat());
                coordinates.setUserLonGuncel(nearestDurak.getLon());
                double enYakinDurakMesafe = routeFinder.haversineDistance(coordinates.getUserLatGirilen(), coordinates.getUserLonGirilen(), nearestDurak.getLat(), nearestDurak.getLon());
                Taxi taxiInfo = root.getTaxi();
                Yazdırma yazdirma = new Yazdırma(root.getDuraklar());
                SadeceOtobus sadeceOtobus = new SadeceOtobus(root.getDuraklar());
                SadeceTramvay sadeceTramvay = new SadeceTramvay(root.getDuraklar());

                System.out.println("En yakın durak: " + nearestDurak.getId() +
                        " (" + String.format("%.1f km", enYakinDurakMesafe) + ")");

                if (enYakinDurakMesafe > 3) {
                    System.out.println("Mesafe > 3 km, taksi çağırılıyor...");
                }

                double mesafe = routeFinder.haversineDistance(coordinates.getUserLatGirilen(), coordinates.getUserLonGirilen(), nearestDurak.getLat(), nearestDurak.getLon());
                double taksiUcreti = taxi.calculateTaxiCost(mesafe,taxiInfo);
                System.out.println("En yakın durağa olan taksi ücreti: " +
                        String.format("%.2f TL", taxi.calculateTaxiCost(mesafe, taxiInfo)));
                // 3) TravelChoiceFrame (Taksi mi, Yürüyerek mi?) aç
                TravelChoiceFrame choiceFrame = new TravelChoiceFrame(
                        nearestDurak,
                        enYakinDurakMesafe,
                        taksiUcreti,
                        (String secim) -> {
                            MainFrame frame = new MainFrame(
                                    nearestDurak,       // 1
                                    root,               // 2
                                    locationHandler,    // 3
                                    routeFinder,        // 4
                                    nearestDurak,       // 5 (tekrar nearestDurak gönderiyorsunuz)
                                    coordinates,        // 6
                                    taxiInfo,           // 7
                                    yolBulucu,          // 8
                                    yazdirma,           // 9
                                    sadeceOtobus,       // 10
                                    sadeceTramvay       // 11
                            );
                            frame.setVisible(true);
                        }
                );
                choiceFrame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
