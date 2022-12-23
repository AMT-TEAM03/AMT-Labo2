package MainController;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import MainController.API;


public class Main {
    public static void main(String[] args) throws IOException {
        startIntegrationTests(System.in, args);
    }

    public static void startIntegrationTests(InputStream input, String[] args) throws IOException {
        // use input and args
        String urlLabelApi;
        String urlObjectApi;
        if(args.length < 2){
            urlLabelApi = "http://localhost:8787/v1";
            urlObjectApi = "http://localhost:9090/v1";
        }else{
            urlLabelApi = args[0];
            urlObjectApi = args[1];   
        }

        String imageUri = "mainTest.jpg";

        API api = new API(urlObjectApi, urlLabelApi);

        // Scenario 1 Nothing Exist
        
        System.out.println("Scenario 1, Nothing Exist Passed!");

        
        // Scenario 2 Only Bucket Exist

        System.out.println("Scenario 2, Only Bucket Exist Passed!");

        // Scenario 3 Everything Exist

        System.out.println("Scenario 3, Everything Exist Passed!");

        // Création de l'objet
        api.CreateObject(imageUri, "./src/main/resources/imageVille.jpg");
        
        // Récupération de l'url de l'objet
        String imageUrl = api.GetUrl(imageUri);

        // Analyse de l'objet avec rekognition
        HttpResponse<JsonNode> responseAnalyze = api.AnalyzeObject(imageUrl);

        // Sauvegarde des résultats dans S3
        Path tempFile = Files.createTempFile("mainTest_Result", ".json");
        try {
            Files.writeString(tempFile, responseAnalyze.getBody().getObject().getJSONObject("data").toString());
        } catch (IOException e) {
            Files.delete(tempFile);
            throw e;
        }

        api.CreateObject("mainTest_Result", tempFile.toString());

        // Suppression de l'image et du résultat dans S3
        api.DeleteObject(imageUri);
    }
}