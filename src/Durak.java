import java.util.ArrayList;

public class Durak {
    private String id;
    private String name;
    private String type;
    private double lat;
    private double lon;
    private boolean sonDurak;
    private ArrayList<NextStop> nextStops = new ArrayList<>();
    private Transfer transfer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public boolean isSonDurak() {
        return sonDurak;
    }

    public void setSonDurak(boolean sonDurak) {
        this.sonDurak = sonDurak;
    }

    public ArrayList<NextStop> getNextStops() {
        return nextStops;
    }

    public void setNextStops(ArrayList<NextStop> nextStops) {
        this.nextStops = nextStops;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public void setTransfer(Transfer transfer) {
        this.transfer = transfer;
    }

    @Override
    public String toString() {
        return "Durak{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", sonDurak=" + sonDurak +
                ", nextStops=" + nextStops +
                ", transfer=" + transfer +
                '}';
    }
}
