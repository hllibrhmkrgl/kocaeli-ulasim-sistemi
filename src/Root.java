import java.util.ArrayList;

public class Root {
    private String city;
    private Taxi taxi;
    private ArrayList<Durak> duraklar = new ArrayList<>();

    // Getter & Setter
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Taxi getTaxi() {
        return taxi;
    }

    public void setTaxi(Taxi taxi) {
        this.taxi = taxi;
    }

    public ArrayList<Durak> getDuraklar() {
        return duraklar;
    }

    public void setDuraklar(ArrayList<Durak> duraklar) {
        this.duraklar = duraklar;
    }
}
