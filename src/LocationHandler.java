public interface LocationHandler {
    Durak findNearestDurak(double userLat, double userLon);
    double calculateTaxiCost(double userLat, double userLon, Durak durak);
    double getDistanceToDurak(double userLat, double userLon, Durak nearestDurak);
}
