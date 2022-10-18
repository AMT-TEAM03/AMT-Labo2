import CloudProvider.S3.Rekognition;
import CloudProvider.S3.S3;

import java.awt.*;
import java.util.List;

public class DetectLabelsS3 {

    public static void main(String[] args) {

        S3 s3 = S3.getInstance();
        Rekognition rekClient = Rekognition.getInstance();

        String bucket = "amt.team03.diduno.education";
        String image = "coucou"; // Key S3 of the image

        // If no img please upload one to your bucket
//        s3.uploadImgToBucket(bucket, "coucou", "./src/main/resources/coucou.jpg");
        List<String> labels_detected = rekClient.getLabelsfromImage(bucket, image);
        for(String label : labels_detected){
            System.out.println(label);
        }
        rekClient.close();
        s3.close();
    }
}