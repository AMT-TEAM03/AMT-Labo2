import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.List;

import CloudProvider.AWS.AwsCloudClient;

public class Main {
    public static void main(String[] args) throws IOException {
        // Encode an image in a base64 like string
        // image path declaration
        String imgPath = "./src/main/resources/coucou.jpg";
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
        // Create an object
        URL url = client.CreateObject("coucou3", dataRow);
        System.out.println("New object accessible at " + url);
        // Detect pattern in image
        List<String> labels = client.Execute("coucou3", null);
        // List pattern detected
        for (String res : labels) {
            System.out.println(res);
        }
        // Delete image
        client.DeleteObject("coucou3");
    }
}