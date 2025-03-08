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
                UserLocationHandler locationHandler = new UserLocationHandler(root.getDuraklar(), root.getTaxi());
                Durak nearestDurak = locationHandler.findNearestDurak(userLat, userLon);
                double enYakinDurakMesafe = locationHandler.getDistanceToDurak(userLat, userLon, nearestDurak);
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
                System.out.println("En yakın durağa olan taksi ücreti: " +
                        String.format("%.2f TL",
                                routeFinder.calculateTaxiCost(userLat, userLon, nearestDurak, taxiInfo)));
                MainFrame frame = new MainFrame(
                        nearestDurak,
                        root,
                        locationHandler,
                        routeFinder,
                        nearestDurak,
                        userLat,
                        userLon,
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
