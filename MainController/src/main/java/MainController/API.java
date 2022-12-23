package MainController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class API {

    private String urlObjectApi;
    private String urlLabelApi;
    private ObjectMapper objectMapper = new ObjectMapper();

    public API(String urlObjectApi, String urlLabelApi){
        this.urlObjectApi = urlObjectApi;
        this.urlLabelApi = urlLabelApi;
    }

    public HttpResponse<JsonNode> CreateObject(String uri, String filePath) throws IOException{
        Map<String, Object> imageConfig = new HashMap<>();
        imageConfig.put("name", uri);
        imageConfig.put("image", Files.readAllBytes(new File(filePath).toPath()));
        String jsonBody = objectMapper.writeValueAsString(imageConfig);

        HttpResponse<JsonNode> requestPost = Unirest.post(urlObjectApi + "/object")
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .asJson();
        return requestPost;
    }

    public String GetUrl(String uri) {
        System.out.println("GET URL OBJECT : ");
        HttpResponse<JsonNode> responseGetURL = Unirest.get(urlObjectApi + "/object/url")
                .queryString("name", uri)
                .asJson();
        return responseGetURL.getBody().getObject().getString("data");
    }

    public HttpResponse<JsonNode> AnalyzeObject(String imageUrl) throws JsonProcessingException {
        HttpResponse<JsonNode> analyzeResult = Unirest.get(urlLabelApi + "/analyze")
                .queryString("imageUrl", imageUrl)
                .asJson();

        return analyzeResult;
    }

    public void DeleteObject(String uri) throws JsonProcessingException {
        Map<String, Object> deletePict = new HashMap<>();
        deletePict.put("name", "mainTest.jpg");
        deletePict.put("image", "");
        String jsonBodyDelete = objectMapper.writeValueAsString(deletePict);

        HttpResponse<JsonNode> resultSuppPict = Unirest.delete(urlObjectApi + "/object")
                .header("Content-Type", "application/json")
                .body(jsonBodyDelete)
                .asJson();

        Map<String, Object> deleteResult = new HashMap<>();
        deleteResult.put("name", "mainTest_Result.jpg");
        deleteResult.put("image", "");
        jsonBodyDelete = objectMapper.writeValueAsString(deleteResult);

        HttpResponse<JsonNode> resultSuppResult = Unirest.delete(urlObjectApi + "/object")
                .header("Content-Type", "application/json")
                .body(jsonBodyDelete)
                .asJson();

        System.out.println(resultSuppPict.getBody());
        System.out.println(resultSuppResult.getBody());
    }
}
