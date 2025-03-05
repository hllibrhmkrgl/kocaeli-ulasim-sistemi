public class Transfer {
    private String transferStopId;
    private int transferSure;
    private double transferUcret;

    // Getter & Setter
    public String getTransferStopId() {
        return transferStopId;
    }

    public void setTransferStopId(String transferStopId) {
        this.transferStopId = transferStopId;
    }

    public int getTransferSure() {
        return transferSure;
    }

    public void setTransferSure(int transferSure) {
        this.transferSure = transferSure;
    }

    public double getTransferUcret() {
        return transferUcret;
    }

    public void setTransferUcret(double transferUcret) {
        this.transferUcret = transferUcret;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "transferStopId='" + transferStopId + '\'' +
                ", transferSure=" + transferSure +
                ", transferUcret=" + transferUcret +
                '}';
    }
}
