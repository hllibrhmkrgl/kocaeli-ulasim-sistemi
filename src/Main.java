import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // 1) JSON Dosyasını Okuma
        String jsonPath = "veriseti.json";
        Root root = JsonReader.readJson(jsonPath);

        // 2) Kullanıcının bulunduğu konum
        double userLat = 40.76520;
        double userLon = 29.96190;

        // 3) Nesneleri oluştur
        UserLocationHandler locationHandler = new UserLocationHandler(root.getDuraklar(), root.getTaxi());
        Durak nearestDurak = locationHandler.findNearestDurak(userLat, userLon);
        double enYakinDurakMesafe = locationHandler.getDistanceToDurak(userLat, userLon, nearestDurak);

        Taxi taxiInfo = root.getTaxi();
        RouteFinder routeFinder = new RouteFinder(root.getDuraklar());
        RouteService routeService = new RouteService(root.getDuraklar());

        // Terminalde bazı başlangıç bilgilerini gösterelim
        System.out.println("En yakın durak: " + nearestDurak.getId() +
                " (" + String.format("%.1f km", enYakinDurakMesafe) + ")");

        // 3 km'den büyükse taksi çağır...
        if (enYakinDurakMesafe > 3) {
            System.out.println("Mesafe > 3 km, taksi çağırılıyor...");
        }

        System.out.println("En yakın durağa olan taksi ücreti: " +
                String.format("%.2f TL",
                        routeFinder.calculateTaxiCost(userLat, userLon, nearestDurak, taxiInfo)));

        // 4) Artık GUI'yi başlatıyoruz
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(
                    nearestDurak,
                    root,
                    locationHandler,
                    routeFinder,
                    routeService,
                    nearestDurak,
                    userLat,
                    userLon
            );
            frame.setVisible(true);
        });
    }
}
