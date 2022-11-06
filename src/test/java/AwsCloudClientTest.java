import static org.junit.jupiter.api.Assertions.*;

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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import CloudProvider.AWS.AwsCloudClient;
import CloudProvider.AWS.AwsPatternDetected;
import software.amazon.awssdk.services.s3.model.S3Object;

class AWSTest {

    static AwsCloudClient _awsClient;
    static String _base64Img;

    @BeforeAll
    static void beforeAll() throws IOException {
        // Instantiate singleton instance
        _awsClient = AwsCloudClient.getInstance();
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
        _base64Img = Base64.getEncoder().withoutPadding().encodeToString(data);
        out.close();
        stream.close();
    }

    @BeforeEach
    void beforeEach(){
        _awsClient.CreateObject("testing123", java.util.Base64.getDecoder().decode(_base64Img));
    }

    @Test
    void testCreateObject() {
        URL url = _awsClient.CreateObject("testing1234", java.util.Base64.getDecoder().decode(_base64Img));
        assertNotNull(url);
        // Expect this one to fail as already exist
        assertThrows(Error.class, () -> _awsClient.CreateObject("testing1234", java.util.Base64.getDecoder().decode(_base64Img)));
        _awsClient.DeleteObject("testing1234");
    }

    @Test 
    void testDoesObjectExists(){
        assertTrue(_awsClient.DoesObjectExists("testing123"));
    }

    @Test
    void testListObject(){
        List<S3Object> result = _awsClient.ListObjects();
        boolean found = false;
        for(S3Object object : result){
            if(object.key().equals("testing123")){
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void testGetObject() throws IOException{
        String objectContent = "coucou tout le monde!";
        _awsClient.CreateObject("testing12345", objectContent.getBytes());
        InputStream objectStream = _awsClient.GetObject("testing12345");
        _awsClient.DeleteObject("testing12345");
        String result = new BufferedReader(new InputStreamReader(objectStream))
            .lines().collect(Collectors.joining("\n"));
        objectStream.close();
        assertEquals(objectContent, new String(result));
    }

    @Test
    void testDeleteObject(){
        // Delete object
        _awsClient.DeleteObject("testing123");
        assertFalse(_awsClient.DoesObjectExists("testing123"));
        _awsClient.CreateObject("testing123", java.util.Base64.getDecoder().decode(_base64Img));
    }

    @Test
    void testDetection(){
        List<AwsPatternDetected> result = _awsClient.Execute("testing123", null);
        assertTrue(_awsClient.DoesObjectExists("testing123_result"));
        List<AwsPatternDetected> cachedResult = _awsClient.Execute("testing123", null);
        assertEquals(result.size(), cachedResult.size());
        for(int i = 0; i < result.size(); i++){
            assertEquals(result.get(i).name, cachedResult.get(i).name);
            assertEquals(result.get(i).confidence, cachedResult.get(i).confidence);
        }
    }

    @AfterEach
    void afterEach(){
        _awsClient.DeleteObject("testing123");
    }

    @AfterAll
    static void tearDown(){
        _awsClient.Close();
    }
}