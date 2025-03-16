import java.util.List;

public class UserLocationHandler implements LocationHandler {
    private List<Durak> durakList;
    private Taxi taxi;

    public UserLocationHandler(List<Durak> durakList, Taxi taxi) {
        this.durakList = durakList;
        this.taxi = taxi;
    }

    public Durak findNearestDurak(double userLat, double userLon) {
        Durak nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Durak d : durakList) {
            double distance = LocationCalculator.haversineTaxiDistance(userLat, userLon, d.getLat(), d.getLon());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = d;
            }
        }
        return nearest;
    }
    @Override
    public double calculateTaxiCost(double userLat, double userLon, Durak durak) {
        double distanceKm = LocationCalculator.haversineTaxiDistance(userLat, userLon, durak.getLat(), durak.getLon());
        return taxi.getOpeningFee() + (distanceKm * taxi.getCostPerKm());
    }

    @Override
    public double getDistanceToDurak(double userLat, double userLon, Durak durak) {
        return LocationCalculator.haversineTaxiDistance(userLat, userLon, durak.getLat(), durak.getLon());
    }


}
