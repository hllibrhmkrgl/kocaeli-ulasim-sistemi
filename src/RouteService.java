import java.util.ArrayList;

public class RouteService {
    private RouteFinder routeFinder;

    public RouteService(ArrayList<Durak> durakList) {
        this.routeFinder = new RouteFinder(durakList);
    }

    public void findAndPrintRoute(String startDurak, String endDurak) {
        System.out.println("YOL = " + startDurak + " ➡️ " + endDurak);
        routeFinder.findMinCostRoute(startDurak, endDurak);
        routeFinder.printRouteDetails(startDurak, endDurak + "\n");
    }
}
