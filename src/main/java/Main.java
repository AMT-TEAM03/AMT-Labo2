import java.io.IOException;
import java.util.List;

import CloudProvider.AWS.AwsCloudClient;

public class Main {
    public static void main(String[] args) throws IOException {
    // Encode an image in a base64 like string

        // // image path declaration
        // String imgPath = "./src/main/resources/coucou.jpg";

        // // read image from file
        // FileInputStream stream = new FileInputStream(imgPath);

        // // get byte array from image stream
        // int bufLength = 2048;
        // byte[] buffer = new byte[2048];
        // byte[] data;

        // ByteArrayOutputStream out = new ByteArrayOutputStream();
        // int readLength;
        // while ((readLength = stream.read(buffer, 0, bufLength)) != -1) {
        //     out.write(buffer, 0, readLength);
        // }

        // data = out.toByteArray();
        // String dataRow = Base64.getEncoder().withoutPadding().encodeToString(data);
        // out.close();
        // stream.close();
        // System.out.println(dataRow);
        String imgPath = "./src/main/resources/coucou.jpg";
        AwsCloudClient client = AwsCloudClient.getInstance();
        client.SetBucketUrl("amt.team03.diduno.education");
        client.CreateObject("coucou1", imgPath);
        List<String> result = client.Execute("coucou1", null);
        for(String res : result){
            System.out.println(res);
        }
    }
}