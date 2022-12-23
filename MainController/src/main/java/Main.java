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


public class Main {
    public static void main(String[] args) throws IOException {

        String urlLabel = args[0];
        String urlObject = args[1];

        // Création de l'objet

        Map<String, Object> imageConfig = new HashMap<>();
        imageConfig.put("name", "mainTest.jpg");
        imageConfig.put("image", Files.readAllBytes(new File("./src/main/resources/imageVille.jpg").toPath()));
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(imageConfig);

        HttpResponse<JsonNode> requestPost = Unirest.post(urlObject + "/object")
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .asJson();

        System.out.println(requestPost.getBody());

        // Récupération de l'objet

        System.out.println("GET URL OBJECT : ");
        HttpResponse<JsonNode> responseGetURL = Unirest.get(urlObject + "/object/url")
                .queryString("name", "mainTest.jpg")
                .asJson();
        System.out.println(responseGetURL.getBody());

        // Analyse de l'objet avec rekognition

        Map<String, Object> analyzeConfig = new HashMap<>();
        analyzeConfig.put("imageUrl", responseGetURL.getBody().getObject().getString("data"));
        analyzeConfig.put("maxPattern", 10);
        analyzeConfig.put("minConfidence", 90);
        String jsonBodyAnalyze = objectMapper.writeValueAsString(analyzeConfig);

        HttpResponse<JsonNode> analyzeResult = Unirest.post(urlLabel + "/analyze")
                .header("Content-Type", "application/json")
                .body(jsonBodyAnalyze)
                .asJson();

        System.out.println(analyzeResult.getBody());

        // Sauvegarde des résultats dans S3

        Path tempFile = Files.createTempFile("mainTest_Result", ".json");
        try {
            Files.writeString(tempFile, responseGetURL.getBody().getObject().getString("data"));
        } catch (IOException e) {
            Files.delete(tempFile);
            throw e;
        }

        Map<String, Object> resultConfig = new HashMap<>();
        resultConfig.put("name", "mainTest_Result.jpg");
        resultConfig.put("image", Files.readAllBytes(tempFile));
        String jsonCacheResult = objectMapper.writeValueAsString(resultConfig);

        HttpResponse<JsonNode> resultCacheResult = Unirest.post(urlObject + "/object")
                .header("Content-Type", "application/json")
                .body(jsonCacheResult)
                .asJson();

        System.out.println(resultCacheResult.getBody());

        // Suppression de l'image et du résultat dans S3

        Map<String, Object> deletePict = new HashMap<>();
        deletePict.put("name", "mainTest.jpg");
        deletePict.put("image", "");
        String jsonBodyDelete = objectMapper.writeValueAsString(deletePict);

        HttpResponse<JsonNode> resultSuppPict = Unirest.delete(urlObject + "/object")
                .header("Content-Type", "application/json")
                .body(jsonBodyDelete)
                .asJson();

        Map<String, Object> deleteResult = new HashMap<>();
        deleteResult.put("name", "mainTest_Result.jpg");
        deleteResult.put("image", "");
        jsonBodyDelete = objectMapper.writeValueAsString(deleteResult);

        HttpResponse<JsonNode> resultSuppResult = Unirest.delete(urlObject + "/object")
                .header("Content-Type", "application/json")
                .body(jsonBodyDelete)
                .asJson();

        System.out.println(resultSuppPict.getBody());
        System.out.println(resultSuppResult.getBody());

    }
}