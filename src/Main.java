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
                coordinates.setUserLatGuncel(nearestDurak.getLat());
                coordinates.setUserLonGuncel(nearestDurak.getLon());
                double enYakinDurakMesafe = locationHandler.getDistanceToDurak(coordinates.getUserLatGirilen(), coordinates.getUserLonGirilen(), nearestDurak);
                Taxi taxiInfo = root.getTaxi();
                RouteFinder routeFinder = new RouteFinder(root.getDuraklar());
                YolBulucu yolBulucu = new YolBulucu(root.getDuraklar());
                Yazdırma yazdirma = new Yazdırma(root.getDuraklar());
                SadeceOtobus sadeceOtobus = new SadeceOtobus(root.getDuraklar());
                SadeceTramvay sadeceTramvay = new SadeceTramvay(root.getDuraklar());
                System.out.println("En yakın durak: " + nearestDurak.getId() +
                        " (" + String.format("%.1f km", enYakinDurakMesafe) + ")");
                if (enYakinDurakMesafe > 3) {
                    System.out.println("Mesafe > 3 km, taksi çağırılıyor...");
                }
                double mesafe = routeFinder.haversineDistance(coordinates.getUserLatGirilen(), coordinates.getUserLonGirilen(), nearestDurak.getLat(), nearestDurak.getLon());
                System.out.println("En yakın durağa olan taksi ücreti: " +
                        String.format("%.2f TL",
                                taxi.calculateTaxiCost(mesafe, taxiInfo)));
                MainFrame frame = new MainFrame(
                        nearestDurak,
                        root,
                        locationHandler,
                        routeFinder,
                        nearestDurak,
                        coordinates,
                        taxiInfo,
                        yolBulucu,
                        yazdirma,
                        sadeceOtobus,
                        sadeceTramvay
                );
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
