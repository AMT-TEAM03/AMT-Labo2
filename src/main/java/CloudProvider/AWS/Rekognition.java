package CloudProvider.AWS;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.util.ArrayList;
import java.util.List;

public final class Rekognition {
    // Singleton
    private static Rekognition INSTANCE;
    private RekognitionClient rekClient;

    private Rekognition() {
        this.rekClient = RekognitionClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

        public static Rekognition getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Rekognition();
        }

        return INSTANCE;
    }

    public void close(){
        rekClient.close();
    }

    // RETURN 10 PREMIERS ELEMENTS QUI SONT AU DESSUS DE 90%
    public List<String> getLabelsfromImage(String bucket, String image) {
        List<Label> labels;
        try {
            S3Object s3Object = S3Object.builder()
                    .bucket(bucket)
                    .name(image)
                    .build() ;

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
//                System.out.println(label.name() + ": " + label.confidence().toString());
        }
        return result;
    }
}
