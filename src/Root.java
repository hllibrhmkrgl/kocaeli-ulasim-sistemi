public class Root {
    private String city;
    private Taxi taxi;
    private java.util.List<Durak> duraklar;

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

    public java.util.List<Durak> getDuraklar() {
        return duraklar;
    }

    public void setDuraklar(java.util.List<Durak> duraklar) {
        this.duraklar = duraklar;
    }
}
