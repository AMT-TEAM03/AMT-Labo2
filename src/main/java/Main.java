import CloudProvider.AWS.Rekognition;
import CloudProvider.AWS.S3;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        S3 s3 = S3.getInstance();
        Rekognition rekClient = Rekognition.getInstance();

        String bucket = "amt.team03.diduno.education";
        String image = "coucou"; // Key S3 of the image

        // If no img please upload one to your bucket
        s3.uploadImgToBucket(bucket, "coucou", "./src/main/resources/coucou.jpg");
        List<String> labels_detected = rekClient.getLabelsfromImage(bucket, image);
        for(String label : labels_detected){
            System.out.println(label);
        }
        rekClient.close();
        s3.close();
    }
}
