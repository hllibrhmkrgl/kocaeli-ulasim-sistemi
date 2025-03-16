public class ApplicationContext {
    private Root root;
    private LocationHandler locationHandler;
    private PathFinder pathFinder;
    private Coordinates coordinates;
    private Taxi taxiInfo;
    private Yazdırma yazdirma;
    private PathFinder sadeceOtobus;
    private PathFinder sadeceTramvay;

    public ApplicationContext(String jsonPath) {
        try {
            this.root = JsonReader.readJson(jsonPath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("JSON dosyası okunurken hata oluştu!");
        }

        this.locationHandler = (LocationHandler) new UserLocationHandler(root.getDuraklar(), root.getTaxi());
        this.pathFinder = new RouteFinder(root.getDuraklar());
        this.coordinates = new Coordinates();
        this.taxiInfo = root.getTaxi();
        this.yazdirma = new Yazdırma(root.getDuraklar());
        this.sadeceOtobus = new SadeceOtobus(root.getDuraklar());
        this.sadeceTramvay = new SadeceTramvay(root.getDuraklar());
    }

    public Root getRoot() { return root; }
    public LocationHandler getLocationHandler() { return locationHandler; }
    public PathFinder getPathFinder() { return pathFinder; }
    public Coordinates getCoordinates() { return coordinates; }
    public Taxi getTaxiInfo() { return taxiInfo; }
    public Yazdırma getYazdirma() { return yazdirma; }
    public PathFinder getSadeceOtobus() { return sadeceOtobus; }
    public PathFinder getSadeceTramvay() { return sadeceTramvay; }
}
