package MainController;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

import java.io.IOException;
import java.io.InputStream;


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
        String imagePath = "./src/main/resources/imageVille.jpg";
        String resultUri = imageUri + "_Result";

        API api = new API(urlObjectApi, urlLabelApi);

        // Scenario 1 Nothing Exist

        // Given
        // Remove the bucket and all of the files in it
        api.PrepareScenario(1);

        // When
        api.CreateObject(imageUri, imagePath);
        String imageUrl = api.GetUrl(imageUri);
        HttpResponse<JsonNode> response = api.AnalyzeObject(imageUrl, imageUri);
        assert(response.getBody().getObject().getBoolean("success"));

        
        // Then
        assert(api.DoesObjectExist(imageUri));
        assert(api.DoesObjectExist(resultUri));

        System.out.println("Scenario 1, Nothing Exist Passed!");
        
        // Scenario 2 Only Bucket Exist

        // Given
        api.DeleteObject(imageUri);
        assert(!api.DoesObjectExist(imageUri));
        assert(!api.DoesObjectExist(resultUri));

        // When
        api.CreateObject(imageUri, imagePath);
        imageUrl = api.GetUrl(imageUri);
        response = api.AnalyzeObject(imageUrl, imageUri);
        assert(response.getBody().getObject().getBoolean("success"));

        
        // Then
        assert(api.DoesObjectExist(imageUri));
        assert(api.DoesObjectExist(resultUri));

        System.out.println("Scenario 2, Only Bucket Exist Passed!");

        // Scenario 3 Everything Exist

        // Given
        assert(api.DoesObjectExist(imageUri));
        assert(api.DoesObjectExist(resultUri));

        // When
        api.CreateObject(imageUri, imagePath);
        imageUrl = api.GetUrl(imageUri);
        response = api.AnalyzeObject(imageUrl, imageUri);
        assert(response.getBody().getObject().getBoolean("success"));

        
        // Then
        assert(api.DoesObjectExist(imageUri));
        assert(api.DoesObjectExist(resultUri));

        System.out.println("Scenario 3, Everything Exist Passed!");
    }
}