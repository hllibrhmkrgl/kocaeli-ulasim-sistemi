import java.util.ArrayList;

public class RouteService {
    private RouteFinder routeFinder;

    public RouteService(ArrayList<Durak> durakList) {
        this.routeFinder = new RouteFinder(durakList);
    }

    public Object findAndPrintRoute(String startDurak, String endDurak) {
        System.out.println("YOL = " + startDurak + " ➡️ " + endDurak);
        routeFinder.findMinCostRoute(startDurak, endDurak);
        routeFinder.printRouteDetails(startDurak, endDurak,"busMin" + "\n");
        return null;
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
