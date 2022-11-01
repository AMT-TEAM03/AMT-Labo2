package CloudProvider.AWS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import CloudProvider.ILabelDetector;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.S3Object;

public class AwsLabelDetectorHelper implements ILabelDetector {
    private RekognitionClient rekClient;

    public AwsLabelDetectorHelper(ProfileCredentialsProvider profile){
        rekClient = RekognitionClient.builder()
        .credentialsProvider(profile)
        .build();
    }

    public List<String> Execute(String imageKey, Map<String, Object> params){
        String bucketUrl = AwsCloudClient.getInstance().GetBucketUrl();
        if(bucketUrl == null){
            throw new Error("Bucket URL not set...");
        }
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
            System.out.println(e.getMessage());
            return null;
        }
        System.out.println("Detected labels for the given photo");
        List<String> result = new ArrayList<>();
        int label_found = 0;
        for (Label label: labels) {
            if(label.confidence() >= 90){
                result.add(label.name());
                label_found++;
                if(label_found > 10)
                    break;
            }
        }
        return result;
    }
    
    public void Close(){
        rekClient.close();
    }
}
