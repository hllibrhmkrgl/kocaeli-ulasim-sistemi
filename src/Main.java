import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CoordinateInputFrame();
        });
    }
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
                TravelChoiceFrame choiceFrame = new TravelChoiceFrame(
                        nearestDurak,
                        enYakinDurakMesafe,
                        taksiUcreti,
                        (String secim) -> {
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
                        }
                );
                choiceFrame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
