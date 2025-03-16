import java.util.List;

public interface PathFinder {
    List<String> findRoute(String start, String destination);
    double calculateTotalCost(List<String> path, String userType);
    int calculateTotalTime(List<String> path);
}
