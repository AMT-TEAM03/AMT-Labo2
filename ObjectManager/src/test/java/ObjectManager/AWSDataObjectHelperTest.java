package ObjectManager;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import ObjectManager.CloudProvider.AWS.AwsDataObjectHelper;

class AWSDataObjectHelperTests {
    static AwsDataObjectHelper _awsClient;
    static String _base64Img;
    static boolean includeBucketTests = false;
    static final String[] OBJECT_KEY_LIST = {
            "test/testing123",
            "test/testing1234",
            "test/testing12345",
            "test/testingNotCreated"
        };

    private static void cleanup() throws Exception {
        for (String i : OBJECT_KEY_LIST) {
            if (_awsClient.DoesObjectExists(i)) {
                _awsClient.DeleteObject(i, false);
            }
        }
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        // Parse config file
        try {
            String propertiesPath = Paths.get(
                    AWSDataObjectHelperTests.class.getClassLoader().getResource("application.properties").toURI()).toFile()
                    .getAbsolutePath();
            InputStream input = new FileInputStream(propertiesPath);
            Properties prop = new Properties();
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            includeBucketTests = prop.getProperty("bucket.tests.include").equals("true") ? true : false;
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            _awsClient.DeleteObject(OBJECT_KEY_LIST[0], false);
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
    void listObjects_ContainElem() throws Exception {
        List<String> result = _awsClient.ListObjects();
        boolean found = false;
        for (String object : result) {
            if (object.equals(OBJECT_KEY_LIST[0])) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void doesObjectExist_RootObjectDoesntExist_DoesntExist() throws Exception
        {
            //given
            Assumptions.assumeTrue(includeBucketTests);
            //when
            _awsClient.DeleteBucket();
            assertFalse(_awsClient.DoesBucketExists());
            //then
            assertFalse(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[0]));
        }


    @Test
    void uploadObject_RootObjectExistsNewObject_Uploaded() throws Exception
        {
            //given
            assertTrue(_awsClient.DoesBucketExists());
            assertFalse(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[1]));
            //when
            _awsClient.CreateObject(OBJECT_KEY_LIST[1], java.util.Base64.getDecoder().decode(_base64Img));
            //then
            assertTrue(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[1]));
            _awsClient.DeleteObject(OBJECT_KEY_LIST[1], false);
        }

    @Test
    void uploadObject_RootObjectExistsObjectAlreadyExists_ThrowException() throws Exception
        {
            //given
            assertTrue(_awsClient.DoesBucketExists());
            assertTrue(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[0]));
            //when
            Exception thrown = assertThrows(Exception.class, () -> {_awsClient.CreateObject(OBJECT_KEY_LIST[0], java.util.Base64.getDecoder().decode(_base64Img));});
            //then
            assertEquals("File already exists in the bucket...", thrown.getMessage());
        }

    @Test
    void uploadObject_RootObjectDoesntExist_Uploaded() throws Exception
        {
            //given
            Assumptions.assumeTrue(includeBucketTests);
            _awsClient.DeleteBucket();
            assertFalse(_awsClient.DoesBucketExists());
            assertFalse(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[0]));
            //when
            _awsClient.CreateObject(OBJECT_KEY_LIST[0], java.util.Base64.getDecoder().decode(_base64Img));
            //then
            assertTrue(_awsClient.DoesBucketExists());
            assertTrue(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[0]));
        }

    @Test
    void downloadObject_ObjectExists_Downloaded() throws Exception
        {
            //given
            assertTrue(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[0]));
            //when
            byte[] imgBytes = _awsClient.GetObject(OBJECT_KEY_LIST[0]).readAllBytes();
            //then
            assertArrayEquals(java.util.Base64.getDecoder().decode(_base64Img), imgBytes);
        }

    @Test
    void downloadObject_ObjectDoesntExist_ThrowException() throws Exception
        {
            //given
            assertFalse(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[1]));
            //when
            Exception thrown = assertThrows(Exception.class, () -> {_awsClient.GetObject(OBJECT_KEY_LIST[1]);});
            //then
            assertEquals("The specified key does not exist.", thrown.getMessage().substring(0, 33));
        }

    @Test
    void publishObject_ObjectExists_Published() throws Exception
        {
            //given
            _awsClient.DoesObjectExists(OBJECT_KEY_LIST[0]);
            //when
            URL imgUrl = _awsClient.GetUrl(OBJECT_KEY_LIST[0]);
            //then
            assertEquals("https://", imgUrl.toString().substring(0, 8));
        }

    @Test
    void publishObject_ObjectDoesntExist_ThrowException() throws Exception
        {
            //given
            assertFalse(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[1]));
            //when
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {System.out.println(_awsClient.GetUrl(OBJECT_KEY_LIST[1]));});
            //then
            assertEquals("Object not found...", thrown.getMessage());
        }

    @Test
    void removeObject_SingleObjectExists_Removed() throws Exception
        {
            //given
            assertTrue(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[0]));
            //when
            _awsClient.DeleteObject(OBJECT_KEY_LIST[0], false);
            //then
            assertFalse(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[0]));
        }

    @Test
    void removeObject_SingleObjectDoesntExist_ThrowException() throws Exception
        {
            //given
            assertFalse(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[1]));
            //when
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {_awsClient.DeleteObject(OBJECT_KEY_LIST[1], false);});
            //then
            assertEquals("Object not found...", thrown.getMessage());
        }

    @Test
    void removeObject_FolderObjectExistWithoutRecursiveOption_ThrowException() throws Exception
        {
            //given
            String folder = "test/";
            assertTrue(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[0]));
            //when
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {_awsClient.DeleteObject(folder, false);});
            //then
            assertEquals("Object not found...", thrown.getMessage());
        }

    @Test
    void removeObject_FolderObjectExistWithRecursiveOption_Removed() throws Exception
        {
            //given
            String folder = "test/";
            _awsClient.CreateObject(OBJECT_KEY_LIST[1], java.util.Base64.getDecoder().decode(_base64Img));
            assertTrue(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[0]));
            assertTrue(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[1]));
            //when
            _awsClient.DeleteObject(folder, true);
            //then
            for(String objectKey : OBJECT_KEY_LIST){
                assertFalse(_awsClient.DoesObjectExists(objectKey));
            }
        }

    // @Test
    // void RemoveObject_RootObjectNotEmptyWithoutRecursiveOption_ThrowException()
    //     {
    //         //given

    //         //when

    //         //then
    //     }

    // @Test
    // void RemoveObject_RootObjectNotEmptyWithRecursiveOption_Removed()
    //     {
    //         //given

    //         //when

    //         //then
    //     }
}