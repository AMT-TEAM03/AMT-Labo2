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
        _awsClient.DeleteObject(OBJECT_KEY_LIST[1]);
    }

    @Test
    void testDoesObjectExists_Exist() throws Exception {
        assertTrue(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[0]));
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
            if (object.equals(OBJECT_KEY_LIST[0])) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void testGetObject_SameAsCreated() throws Exception {

        String objectContent = "coucou tout le monde!";
        _awsClient.CreateObject(OBJECT_KEY_LIST[2], objectContent.getBytes());
        InputStream objectStream = _awsClient.GetObject(OBJECT_KEY_LIST[2]);
        _awsClient.DeleteObject(OBJECT_KEY_LIST[2]);
        String result = new BufferedReader(new InputStreamReader(objectStream))
                .lines().collect(Collectors.joining("\n"));
        objectStream.close();
        assertEquals(objectContent, new String(result));
    }

    @Test
    void testDeleteObject_IsDeleted() throws Exception {
        // Delete object
        _awsClient.DeleteObject(OBJECT_KEY_LIST[0]);
        assertFalse(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[0]));
        ;
    }

    @Test
    void testDoesObjectExist_RootObjectDoesntExist_DoesntExist() throws Exception
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
    void testUploadObject_RootObjectExistsNewObject_Uploaded() throws Exception
        {
            //given
            assertTrue(_awsClient.DoesBucketExists());
            assertFalse(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[1]));
            //when
            _awsClient.CreateObject(OBJECT_KEY_LIST[1], java.util.Base64.getDecoder().decode(_base64Img));
            //then
            assertTrue(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[1]));
            _awsClient.DeleteObject(OBJECT_KEY_LIST[1]);
        }

    @Test
    void testUploadObject_RootObjectExistsObjectAlreadyExists_ThrowException() throws Exception
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
    void testUploadObject_RootObjectDoesntExist_Uploaded() throws Exception
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
    void testDownloadObject_ObjectExists_Downloaded() throws Exception
        {
            //given
            assertTrue(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[0]));
            //when
            byte[] imgBytes = _awsClient.GetObject(OBJECT_KEY_LIST[0]).readAllBytes();
            //then
            assertArrayEquals(java.util.Base64.getDecoder().decode(_base64Img), imgBytes);
        }

    @Test
    void DownloadObject_ObjectDoesntExist_ThrowException() throws Exception
        {
            //given
            assertFalse(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[1]));
            //when
            Exception thrown = assertThrows(Exception.class, () -> {_awsClient.GetObject(OBJECT_KEY_LIST[1]);});
            //then
            assertEquals("The specified key does not exist.", thrown.getMessage().substring(0, 33));
        }

    @Test
    void PublishObject_ObjectExists_Published() throws Exception
        {
            //given
            _awsClient.DoesObjectExists(OBJECT_KEY_LIST[0]);
            //when
            URL imgUrl = _awsClient.GetUrl(OBJECT_KEY_LIST[0]);
            //then
            assertEquals("https://", imgUrl.toString().substring(0, 8));
        }

    @Test
    void PublishObject_ObjectDoesntExist_ThrowException() throws Exception
        {
            //given
            assertFalse(_awsClient.DoesObjectExists(OBJECT_KEY_LIST[1]));
            //when
            Exception thrown = assertThrows(Exception.class, () -> {System.out.println(_awsClient.GetUrl(OBJECT_KEY_LIST[1]));});
            //then
            System.out.println(thrown.getMessage());
            assertEquals("whatever", thrown.getMessage());
        }

    // @Test
    // void RemoveObject_SingleObjectExists_Removed()
    //     {
    //         //given

    //         //when

    //         //then
    //     }

    // @Test
    // void RemoveObject_SingleObjectDoesntExist_ThrowException()
    //     {
    //         //given

    //         //when

    //         //then
    //     }

    // @Test
    // void RemoveObject_FolderObjectExistWithoutRecursiveOption_ThrowException()
    //     {
    //         //given

    //         //when

    //         //then
    //     }

    // @Test
    // void RemoveObject_FolderObjectExistWithRecursiveOption_Removed()
    //     {
    //         //given

    //         //when

    //         //then
    //     }

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

    // @Test
    // void RemoveObject_ObjectNotExists_ThrowException(){
    //         //given

    //         //when

    //         //then
    //     }
}