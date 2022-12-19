import com.mashape.unirest.http.JsonNode;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        HttpCommObject tmp = new HttpCommObject("http://localhost:8787/v1/");

        Map<String, Object> param = new HashMap<>();
        param.put("imageUrlString", "https://upload.wikimedia.org/wikipedia/commons/3/32/Googleplex_HQ_%28cropped%29.jpg");

        JsonNode tmp2 = tmp.GetData("analyze", param);

        System.out.println(tmp2);
    }
}
