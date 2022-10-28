package CloudProvider.AWS;

import CloudProvider.ILabelDetector;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.rekognition.RekognitionClient;

public class AwsLabelDetectorHelper implements ILabelDetector {
    private RekognitionClient rekClient;

    public AwsLabelDetectorHelper(ProfileCredentialsProvider profile){
        rekClient = RekognitionClient.builder()
        .credentialsProvider(profile)
        .build();
    }

    public String Execute(String imageUri, int[] params){
        return "";
    }
    
    public void Close(){
        rekClient.close();
    }
}
