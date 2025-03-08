import java.util.ArrayList;

public class RouteInfo {
    private ArrayList<String> path;  // Yolu tutacak (ArrayList kullanılıyor)
    private double totalCost;        // Toplam ücreti tutacak

    // Constructor (Yol ve toplam ücreti alır)
    public RouteInfo(ArrayList<String> path, double totalCost) {
        this.path = path;
        this.totalCost = totalCost;
    }

    // Yol bilgilerini döndüren getter metodu
    public ArrayList<String> getPath() {
        return path;
    }

    // Toplam ücreti döndüren getter metodu
    public double getTotalCost() {
        return totalCost;
    }

}
