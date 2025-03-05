public class Taxi {
    private double openingFee;
    private double costPerKm;

    public double getOpeningFee() {
        return openingFee;
    }

    public void setOpeningFee(double openingFee) {
        this.openingFee = openingFee;
    }

    public double getCostPerKm() {
        return costPerKm;
    }

    public void setCostPerKm(double costPerKm) {
        this.costPerKm = costPerKm;
    }

    @Override
    public String toString() {
        return "Taxi{" +
                "openingFee=" + openingFee +
                ", costPerKm=" + costPerKm +
                '}';
    }
}
