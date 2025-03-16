import java.util.List;

public interface TransportStrategy extends PathFinder {
    List<String> findRoute(String start, String destination);
    double calculateTotalCost(List<String> path, String userType);
}
