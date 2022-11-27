import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import CloudProvider.AWS.AwsCloudClient;
import CloudProvider.AWS.JSON.AwsLogEntry;
import CloudProvider.AWS.JSON.AwsPatternDetected;
//TODOR REVIEW this test class shouldn't have any reference to aws sdk
// RES Removed import and convert list<S3Object> in List<String>
// import software.amazon.awssdk.services.s3.model.S3Object;

//TODOR REVIEW split this test class to get a test class for the bucket, and an another one for labeldetection
// RES > Created 2 test class and splitted test between them
//TODOR REVIEW refactor the whole class in BDD style !
// RES > changed test to look more like BDD

class AWSDataObjectHelperTests{
    static AwsCloudClient _awsClient;
    static String _base64Img;
    static final String[] OBJECT_KEY_LIST = {"testing123",
                                           "testing1234",
                                           "testing12345",
                                           "testingNotCreated"};

    private static void cleanup(){
        for(String i : OBJECT_KEY_LIST){
            if(_awsClient.DoesObjectExists(i)){
                _awsClient.DeleteObject(i);
            }
        }
    }

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

        // cleanup of possible old test DataObject
        cleanup();
    }

    @BeforeEach
    void beforeEach() {
        _awsClient.CreateObject(OBJECT_KEY_LIST[0], java.util.Base64.getDecoder().decode(_base64Img));
    }

    @AfterEach
        //TODOR REVIEW To avoid exception (self generated ;) test before deleting
        // RES > Check if object exist before deleting
    void afterEach(){
        if(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[0])){
            _awsClient.DeleteObject(OBJECT_KEY_LIST[0]);
        }
    }

    @AfterAll
    //TODOR REVIEW To avoid exception (self generated ;) test before closing
    // RES > Explanations as to why there is no test before closin in the README Test and Generation chapter
    static void tearDown(){
        // cleanup of all data object used for test still not deleted
        cleanup();
        _awsClient.Close();
    }


    @Test
    void testCreateObject_Created() {
        URL url = _awsClient.CreateObject(OBJECT_KEY_LIST[1], java.util.Base64.getDecoder().decode(_base64Img));
        assertNotNull(url);
        assertTrue(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[1]));
    }

    @Test
    void testDoesObjectExists_Exist() {
        assertTrue(_awsClient.DoesObjectExists("testing123"));
    }

    @Test
    void testDoesObjectExists_NotExist() {
        assertFalse(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[3]));
    }

    @Test
    void testListObjects_ContainElem() {
        List<String> result = _awsClient.ListObjects();
        boolean found = false;
        for (String object : result) {
            if (object.equals("testing123")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void testGetObject_SameAsCreated() throws IOException {

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
    void testDeleteObject_IsDeleted() {
        // Delete object
        _awsClient.DeleteObject("testing123");
        assertFalse(_awsClient.DoesObjectExists("testing123"));;
    }

}

class AWSLabelDetectorHelperTests{

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
    void beforeEach() {
        _awsClient.CreateObject("testing123", java.util.Base64.getDecoder().decode(_base64Img));
    }

    @AfterEach
        //TODOR REVIEW To avoid exception (self generated ;) test before deleting
        // RES > Check if object exist before deleting
    void afterEach(){
        if(_awsClient.DoesObjectExists("testing123")){
            _awsClient.DeleteObject("testing123");
        }
    }

    @AfterAll
    //TODOR REVIEW To avoid exception (self generated ;) test before closing
    // RES > Explanations as to why there is no test before closin in the README Test and Generation chapter
    static void tearDown(){
        _awsClient.Close();
    }

    @Test
    void testCaching_True(){
        _awsClient.ResetLogging();
        List<AwsPatternDetected> result = _awsClient.Execute("testing123", null);

        // Check Caching
        List<AwsPatternDetected> cachedResult = _awsClient.Execute("testing123", null);

        assertTrue(_awsClient.DoesObjectExists("testing123_result"));
        assertTrue(_awsClient.DoesObjectExists("logs"));
        assertEquals(result.size(), cachedResult.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).name, cachedResult.get(i).name);
            assertEquals(result.get(i).confidence, cachedResult.get(i).confidence);
        }
    }


    @Test
    void testTransactionLog(){
        InputStream logStream = _awsClient.GetObject("logs");
        String logString = new BufferedReader(new InputStreamReader(logStream))
                .lines().collect(Collectors.joining("\n"));

        // JSonArray representing the cached result
        Gson g = new Gson();
        JsonArray cacheResult = JsonParser.parseString(logString).getAsJsonArray();
        Type cacheType = new TypeToken<List<AwsLogEntry>>() {
        }.getType();
        List<AwsLogEntry> logs = g.fromJson(cacheResult, cacheType);

        boolean found = false;
        for (AwsLogEntry log : logs) {
            if (log.fileTreatedKey.equals("testing123")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
}
