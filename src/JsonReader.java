import com.google.gson.Gson;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonReader {
    public static Root readJson(String filePath) throws Exception {
        String jsonString = new String(Files.readAllBytes(Paths.get(filePath)));
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Root.class);
    }
}
