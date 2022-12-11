package ObjectManager;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ObjectManager.CloudProvider.AWS.AwsDataObjectHelper;

class AWSDataObjectHelperTests {
    static AwsDataObjectHelper _awsClient;
    static String _base64Img;
    static final String[] OBJECT_KEY_LIST = {
            "testing123",
            "testing1234",
            "testing12345",
            "testingNotCreated"
        };

    private static void cleanup() throws Exception {
        for (String i : OBJECT_KEY_LIST) {
            if (_awsClient.DoesObjectExists(i)) {
                _awsClient.DeleteObject(i);
            }
        }
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        // Instantiate singleton instance
        _awsClient = new AwsDataObjectHelper();
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
    void beforeEach() throws Exception {
        _awsClient.CreateObject(OBJECT_KEY_LIST[0], java.util.Base64.getDecoder().decode(_base64Img));
    }

    @AfterEach
    // TODOR REVIEW To avoid exception (self generated ;) test before deleting
    // RES > Check if object exist before deleting
    void afterEach() throws Exception {
        if (_awsClient.DoesObjectExists(OBJECT_KEY_LIST[0])) {
            _awsClient.DeleteObject(OBJECT_KEY_LIST[0]);
        }
    }

    @AfterAll
    // TODOR REVIEW To avoid exception (self generated ;) test before closing
    // RES > Explanations as to why there is no test before closin in the README
    // Test and Generation chapter
    static void tearDown() throws Exception {
        // cleanup of all data object used for test still not deleted
        cleanup();
        _awsClient.Close();
    }

    @Test
    void testCreateObject_Created() throws Exception {
        _awsClient.CreateObject(OBJECT_KEY_LIST[1], java.util.Base64.getDecoder().decode(_base64Img));
        assertTrue(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[1]));
    }

    @Test
    void testDoesObjectExists_Exist() throws Exception {
        assertTrue(_awsClient.DoesObjectExists("testing123"));
    }

    @Test
    void testDoesObjectExists_NotExist() throws Exception {
        assertFalse(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[3]));
    }

    @Test
    void testListObjects_ContainElem() throws Exception {
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
    void testGetObject_SameAsCreated() throws Exception {

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
    void testDeleteObject_IsDeleted() throws Exception {
        // Delete object
        _awsClient.DeleteObject("testing123");
        assertFalse(_awsClient.DoesObjectExists("testing123"));
        ;
    }

}