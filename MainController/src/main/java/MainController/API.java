package MainController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        HttpResponse<JsonNode> responseGetURL = Unirest.get(urlObjectApi + "/object/url")
                .queryString("name", uri)
                .asJson();
        return responseGetURL.getBody().getObject().getString("data");
    }

    public HttpResponse<JsonNode> AnalyzeObject(String imageUrl, String imageUri) throws IOException {
        HttpResponse<JsonNode> analyzeResult = Unirest.get(urlLabelApi + "/analyze")
                .queryString("imageUrl", imageUrl)
                .asJson();

        assert(analyzeResult.getBody().getObject().getBoolean("success"));

        // Upload result for caching
        Path tempFile = Files.createTempFile(imageUri, "_Result");
        try {
            Files.writeString(tempFile, analyzeResult.getBody().getObject().toString());
        } catch (IOException e) {
            Files.delete(tempFile);
            throw e;
        }

        CreateObject(imageUri + "_Result", tempFile.toString());

        return analyzeResult;
    }

    public void DeleteObject(String uri) throws JsonProcessingException {
        Map<String, Object> deletePict = new HashMap<>();
        deletePict.put("name", uri);
        String jsonBodyDelete = objectMapper.writeValueAsString(deletePict);

        HttpResponse<JsonNode> resultSuppPict = Unirest.delete(urlObjectApi + "/object")
                .header("Content-Type", "application/json")
                .body(jsonBodyDelete)
                .asJson();

        Map<String, Object> deleteResult = new HashMap<>();
        deleteResult.put("name", uri + "_Result");
        jsonBodyDelete = objectMapper.writeValueAsString(deleteResult);

        HttpResponse<JsonNode> resultSuppResult = Unirest.delete(urlObjectApi + "/object")
                .header("Content-Type", "application/json")
                .body(jsonBodyDelete)
                .asJson();

        assert(resultSuppPict.getBody().getObject().getBoolean("success"));
        assert(resultSuppResult.getBody().getObject().getBoolean("success"));
    }

    public boolean DoesObjectExist(String uri){
        HttpResponse<JsonNode> response = Unirest.get(urlObjectApi + "/object/exists")
                .queryString("name", uri)
                .asJson();
        return response.getBody().getObject().getString("data") == "true";
    }

    public void PrepareScenario(int idScenario) throws JsonProcessingException{
        Map<String, Object> bodyRequest = new HashMap<>();
        bodyRequest.put("name", Integer.toString(idScenario));
        String jsonBody = objectMapper.writeValueAsString(bodyRequest);
        HttpResponse<JsonNode> response = Unirest.post(urlObjectApi + "/prepare-for-scenario")
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .asJson();
        assert(response.getBody().getObject().getBoolean("data"));
    }
}
