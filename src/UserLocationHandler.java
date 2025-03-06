import java.util.ArrayList;

public class UserLocationHandler {
    private RouteFinder routeFinder;
    private Taxi taxiInfo;

    public UserLocationHandler(ArrayList<Durak> durakList, Taxi taxiInfo) {
        this.routeFinder = new RouteFinder(durakList);
        this.taxiInfo = taxiInfo;
    }

    public Durak findNearestDurak(double userLat, double userLon) {
        return routeFinder.findNearestDurak(userLat, userLon);
    }

    public double calculateTaxiCost(double userLat, double userLon, Durak nearestDurak) {
        return routeFinder.calculateTaxiCost(userLat, userLon, nearestDurak, taxiInfo);
    }

    public double getDistanceToDurak(double userLat, double userLon, Durak nearestDurak) {
        return routeFinder.haversineTaxiDistance(userLat, userLon, nearestDurak.getLat(), nearestDurak.getLon());
    }


}
