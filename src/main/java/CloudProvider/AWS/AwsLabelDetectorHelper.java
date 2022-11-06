package CloudProvider.AWS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import CloudProvider.ILabelDetector;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.S3Object;

public class AwsLabelDetectorHelper implements ILabelDetector {
    private RekognitionClient rekClient;
    private int CONFIDENCE_THRESHOLD = 90;
    private int MAX_PATTERN = 10;

    public AwsLabelDetectorHelper(){
        rekClient = RekognitionClient.builder()
        .build();
    }

    public List<AwsPatternDetected> Execute(String imageKey, Map<String, Object> params){
        AwsCloudClient client = AwsCloudClient.getInstance();
        String bucketUrl = client.GetBucketUrl();
        if(bucketUrl == null){
            throw new Error("Bucket URL not set...");
        }
        String resultKey = imageKey + "_result";
        List<AwsPatternDetected> result = new ArrayList<AwsPatternDetected>();
        Gson g = new Gson();
        if(client.DoesObjectExists(resultKey)){
            // Get result from cache
            InputStream objectStream = client.GetObject(resultKey);
            String resultString = new BufferedReader(new InputStreamReader(objectStream))
            .lines().collect(Collectors.joining("\n"));
            try {
                objectStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // JSonArray representing the cached result
            JsonArray cacheResult = JsonParser .parseString(resultString).getAsJsonArray();
            Type cacheType = new TypeToken<List<AwsPatternDetected>>(){}.getType();
            result = g.fromJson(cacheResult, cacheType);
        }else{
            // Detect the labels
            List<Label> labels;
            try {
                S3Object s3Object = S3Object.builder()
                        .bucket(bucketUrl)
                        .name(imageKey)
                        .build();
    
                Image myImage = Image.builder()
                        .s3Object(s3Object)
                        .build();
    
                DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                        .image(myImage)
                        .maxLabels(10)
                        .build();
    
                DetectLabelsResponse labelsResponse = rekClient.detectLabels(detectLabelsRequest);
                labels = labelsResponse.labels();
    
            } catch (RekognitionException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                return null;
            }
            // Serialize result and upload it
            int patternDetected = 0;
            for(Label label: labels){
                if(label.confidence() >= CONFIDENCE_THRESHOLD){
                    result.add(new AwsPatternDetected(label.name(), label.confidence()));
                    patternDetected++;
                    if(patternDetected > MAX_PATTERN){
                        break;
                    }
                }
            }
            String json = g.toJson(result);
            // Upload it
            client.CreateObject(resultKey, json.getBytes());
        }
        
        return result;
    }
    
    public void Close(){
        rekClient.close();
    }
}
