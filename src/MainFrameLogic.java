import javax.swing.*;
import java.util.List;

public class MainFrameLogic {
    private Root root;
    private UserLocationHandler locationHandler;
    private RouteFinder routeFinder;
    private YolBulucu yolBulucu;
    private Yazdırma yazdirma;
    private Coordinates coordinates;
    private SadeceOtobus sadeceOtobus;
    private SadeceTramvay sadeceTramvay;
    private Durak nearestDurak;
    private Taxi taxiInfo;
    private Taxi taxi;
    // "Kullanıcı tipi" de bu mantık sınıfında takip edilecek
    private String userType = "Normal";

    public MainFrameLogic(Root root,
                          UserLocationHandler locationHandler,
                          RouteFinder routeFinder,
                          Durak nearestDurak,
                          Coordinates coordinates,
                          Taxi taxiInfo,
                          YolBulucu yolBulucu,
                          Yazdırma yazdirma,
                          SadeceOtobus sadeceOtobus,
                          SadeceTramvay sadeceTramvay) {

        this.root = root;
        this.locationHandler = locationHandler;
        this.routeFinder = routeFinder;
        this.nearestDurak = nearestDurak;
        this.coordinates = coordinates;
        this.taxiInfo = taxiInfo;
        this.yolBulucu = yolBulucu;
        this.yazdirma = yazdirma;
        this.sadeceOtobus = sadeceOtobus;
        this.sadeceTramvay = sadeceTramvay;
    }

    // Kullanıcı tipini güncelleyen metot
    public void setUserType(String userType) {
        this.userType = userType;
    }

    // Kullanıcı tipini döndüren metot
    public String getUserType() {
        return this.userType;
    }

    // Switch-case içindeki tüm iş mantığını buraya taşıyoruz
    public String handleOperation(int secilenIslem, String hedefDurakIsmi, JTextArea outputArea) {
        StringBuilder output = new StringBuilder();

        switch (secilenIslem) {
            case 2:
                output.append("2. Otobüs Duraklarının ismine bakma\n");
                String busInfo = routeFinder.getAllBusInfo();
                output.append(busInfo).append("\n");
                JOptionPane.showMessageDialog(null, "İşlem Başarılı");
                break;

            case 3:
                output.append("3. Tramvay Duraklarının ismine bakma\n");
                String tramInfo = routeFinder.getAllTramInfo();
                output.append(tramInfo).append("\n");
                JOptionPane.showMessageDialog(null, "İşlem Başarılı");
                break;

            case 1:
            case 4:
            case 5:
            case 6:
                // Hedef durağı bulma
                Durak hedefDurak = null;
                boolean durakVarMi = false;
                for (Durak d : root.getDuraklar()) {
                    if (d.getId().equalsIgnoreCase(hedefDurakIsmi)) {
                        hedefDurak = d;
                        durakVarMi = true;
                        break;
                    }
                }
                if (!durakVarMi) {
                    JOptionPane.showMessageDialog(null,
                            "Hata! Girdiğiniz durak listede bulunmuyor.");
                    output.append("Hata! Girdiğiniz durak listede bulunmuyor.\n");
                    return output.toString();
                }

                if (secilenIslem == 1) {
                    output.append("1. Gitmek İstediğim durağa olan en kısa yol\n");
                    List<String> Path = yolBulucu.findCheapestPath(nearestDurak.getId(), hedefDurak.getId());
                    double cost = yolBulucu.calculateTotalCost(Path, userType);
                    output.append(yazdirma.printRouteDetailsInfo(Path, userType, cost));
                }
                else if (secilenIslem == 4) {
                    output.append("4. Sadece Otobüs ile gitmek için yol (TRANSFERSİZ)\n");
                    List<String> Path = sadeceOtobus.getOnlyBusRoute(nearestDurak.getId(), hedefDurak.getId());
                    double cost = sadeceOtobus.calculateTotalCost(Path, userType);
                    output.append(yazdirma.printRouteDetailsInfo(Path, userType, cost));
                }
                else if (secilenIslem == 5) {
                    output.append("5. Sadece tramvay ile gitmek için yol (TRANSFERSİZ)\n");
                    List<String> Path = sadeceTramvay.getOnlyTramRoute(nearestDurak.getId(), hedefDurak.getId());
                    double cost = yolBulucu.calculateTotalCost(Path, userType);
                    output.append(yazdirma.printRouteDetailsInfo(Path, userType, cost));
                }
                else if (secilenIslem == 6) {
                    output.append("6. Konumdan Durağa Taksi Ücreti.\n");
                    double mesafe = routeFinder.haversineDistance(coordinates.getUserLatGuncel(),coordinates.getUserLonGuncel(),hedefDurak.getLat(),hedefDurak.getLon());
                    double taxiCost = taxi.calculateTaxiCost(
                            mesafe,
                            taxiInfo
                    );
                    output.append(yazdirma.TaxiDetails(nearestDurak.getId(), hedefDurak.getId(), taxiCost,mesafe));
                }

                output.append("\nİşlem tamamlandı.\n");
                JOptionPane.showMessageDialog(null, "İşlem tamamlandı.");
                break;

            default:
                output.append("Hatalı seçim\n");
                JOptionPane.showMessageDialog(null, "Hatalı seçim yaptınız.");
        }

        return output.toString();
    }
}
