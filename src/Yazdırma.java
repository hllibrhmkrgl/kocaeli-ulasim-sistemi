import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Yazdırma {
    private Map<String, Durak> durakMap;
    public Yazdırma(ArrayList<Durak> durakList) {
        durakMap = new HashMap<>();
        for (Durak d : durakList) {
            durakMap.put(d.getId(), d);
        }
    }
    public String printRouteDetailsInfo(List<String> path,String userType,Double Cost) {
        StringBuilder sb = new StringBuilder();
        if (path == null || path.isEmpty() || path.size() < 2) {
            sb.append("❌ Rota bulunamadı!");
            return sb.toString();
        }
        sb.append("\n📍 Rota Detayları:\n");
        double totalCost = 0;
        double totalTime = 0;
        int step = 1;
        for (int i = 0; i < path.size() - 1; i++) {
            String currentStopId = path.get(i);
            String nextStopId = path.get(i + 1);
            Durak currentDurak = durakMap.get(currentStopId);
            Durak nextDurak = durakMap.get(nextStopId);
            if (currentDurak == null || nextDurak == null) continue;
            NextStop selectedNextStop = null;
            if (currentDurak.getNextStops() != null) {
                for (NextStop ns : currentDurak.getNextStops()) {
                    if (ns.getStopId().equals(nextStopId)) {
                        selectedNextStop = ns;
                        break;
                    }
                }
            }
            Transfer transfer = currentDurak.getTransfer();
            boolean isTransfer = (transfer != null && transfer.getTransferStopId().equals(nextStopId));
            if (selectedNextStop != null) {
                totalCost += selectedNextStop.getUcret();
                totalTime += selectedNextStop.getSure();
                sb.append(step).append(". ").append(currentDurak.getId())
                        .append(" → ").append(nextDurak.getId())
                        .append(" (").append(getTransportIcon(currentDurak)).append(")\n");
                sb.append("📏 Mesafe: ")
                        .append(String.format("%.1f km", selectedNextStop.getMesafe())).append("\n");
                sb.append("⏳ Süre: ").append(selectedNextStop.getSure()).append(" dk\n");
                sb.append("💰 Ücret: ")
                        .append(String.format("%.2f TL", selectedNextStop.getUcret())).append("\n");
            }
            else if (isTransfer) {
                totalCost += transfer.getTransferUcret();
                totalTime += transfer.getTransferSure();
                sb.append(step).append(". ").append(currentDurak.getId())
                        .append(" → ").append(nextDurak.getId()).append(" (🔄 Transfer)\n");
                sb.append("⏳ Süre: ").append(transfer.getTransferSure()).append(" dk\n");
                sb.append("💰 Ücret: ")
                        .append(String.format("%.2f TL", transfer.getTransferUcret())).append("\n");
            }
            step++;
        }
        sb.append("\n✅ Toplam Ücret: ").append(String.format("%.2f TL", totalCost)).append(" 💰\n");
        sb.append("✅ "+userType+" için Toplam Ücret: ").append(String.format("%.2f TL", Cost)).append(" 💰\n");
        sb.append("✅ Toplam Süre: ").append(String.format("%.2f dk", totalTime)).append(" ⏳");
        return sb.toString();
    }
    private String getTransportIcon(Durak durak) {
        String durakAdi = durak.getId().toLowerCase();
        if (durakAdi.contains("bus")) return "🚌 Otobüs";
        if (durakAdi.contains("tram")) return "🚋 Tramvay";
        return "🚖 Taksi";
    }
    public String TaxiDetails(String startId, String endId, double cost, Double distance) {
        String formattedCost = String.format("%.2f", cost);
        String formattedDistance = String.format("%.2f", distance);
        String details = String.format("Başlangıç: %s\nBitiş: %s\nÜcret: %s TL\nMesafe: %s km",
                startId, endId, formattedCost, formattedDistance);
        return details;
    }

}
