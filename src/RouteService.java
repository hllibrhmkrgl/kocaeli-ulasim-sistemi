import java.util.ArrayList;

public class RouteService {
    private RouteFinder routeFinder;

    public RouteService(ArrayList<Durak> durakList) {
        this.routeFinder = new RouteFinder(durakList);
    }

    public String printRouteDetails(String startDurak, String endDurak) {
        StringBuilder sb = new StringBuilder();
        // Yol bilgisini ekleyelim
        sb.append("YOL = ").append(startDurak).append(" ➡️ ").append(endDurak).append("\n");
        // Rotayı hesapla ve detayları al
        String routeDetails = routeFinder.findMinCostRouteInfo(startDurak, endDurak);
        sb.append(routeDetails).append("\n");
        // Route detaylarını ekleyelim
        String additionalDetails = routeFinder.printRouteDetailsInfo(startDurak, endDurak,"busMin");
        sb.append(additionalDetails);
        return sb.toString();
    }
}
