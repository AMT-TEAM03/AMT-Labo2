import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import CloudProvider.AWS.AwsCloudClient;
import CloudProvider.AWS.JSON.AwsPatternDetected;
import software.amazon.awssdk.services.s3.model.S3Object;

public class Main {
    public static void main(String[] args) throws IOException {
        // Encode an image in a base64 like string
        // image path declaration
        String imgPath = args[0];
        String imgKey = args[1];
        // read image from file
        FileInputStream stream = new FileInputStream(imgPath);
        // get byte array from image stream
        int bufLength = 2048;
        byte[] buffer = new byte[2048];
        byte[] data;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int readLength;
        while ((readLength = stream.read(buffer, 0, bufLength)) != -1) {
            out.write(buffer, 0, readLength);
        }
        data = out.toByteArray();
        String dataRow = Base64.getEncoder().withoutPadding().encodeToString(data);
        out.close();
        stream.close();

        // Test AwsCloudClient
        // Get Client instance
        AwsCloudClient client = AwsCloudClient.getInstance();

        // Optional, default value is "amt.team03.diduno.education"
        client.SetBucketUrl("amt.team03.diduno.education");

        // List all objects
        System.out.println("\n\nList objects : ");
        List<S3Object> objects = client.ListObjects();
        for(S3Object object : objects){
            System.out.println(object.key());
        }

        // Create an object
        URL url = client.CreateObject(imgKey, java.util.Base64.getDecoder().decode(dataRow));

        // List all objects after creating object
        System.out.println("\n\nList objects after creation : ");
        List<S3Object> objectsAfterCreate = client.ListObjects();
        for(S3Object object : objectsAfterCreate){
            System.out.println(object.key());
        }

        System.out.println("\n\nNew object accessible at " + url);

        // Detect pattern in image
        List<AwsPatternDetected> labels = client.Execute(imgKey, null);

        // List pattern detected
        System.out.println("\n\nPattern detected : ");
        for (AwsPatternDetected res : labels) {
            System.out.println(res.name + " -> " + res.confidence);
        }
        labels = client.Execute(imgKey, null);

        // List pattern detected
        System.out.println("\n\nPattern detected with cache : ");
        for (AwsPatternDetected res : labels) {
            System.out.println(res.name + " -> " + res.confidence);
        }

        // Delete image
        client.DeleteObject(imgKey);

        // List all objects after deleting object
        System.out.println("\n\nList objects after deletion : ");
        List<S3Object> objectsAfterDelete = client.ListObjects();
        for(S3Object object : objectsAfterDelete){
            System.out.println(object.key());
        }

        // Display the transaction logs
        System.out.println("\n\nTransactions for billing : ");
        InputStream logStream = client.GetObject("logs");
        System.out.println(new BufferedReader(new InputStreamReader(logStream))
                        .lines().collect(Collectors.joining("\n")));
    }
}