public class Coordinates {
    private double userLatGirilen;
    private double userLonGirilen;
    private double userLatGuncel;
    private double userLonGuncel;
    public Coordinates() {
    }

    public Coordinates(double userLatGirilen, double userLonGirilen) {
        this.userLatGirilen = userLatGirilen;
        this.userLonGirilen = userLonGirilen;
    }

    public double getUserLatGirilen() {
        return userLatGirilen;
    }

    public void setUserLatGirilen(double userLatGirilen) {
        this.userLatGirilen = userLatGirilen;
    }

    public double getUserLonGirilen() {
        return userLonGirilen;
    }

    public void setUserLonGirilen(double userLonGirilen) {
        this.userLonGirilen = userLonGirilen;
    }

    public double getUserLatGuncel() {
        return userLatGuncel;
    }

    public void setUserLatGuncel(double userLatGuncel) {
        this.userLatGuncel = userLatGuncel;
    }

    public double getUserLonGuncel() {
        return userLonGuncel;
    }

    public void setUserLonGuncel(double userLonGuncel) {
        this.userLonGuncel = userLonGuncel;
    }
}
