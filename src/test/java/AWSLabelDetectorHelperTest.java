import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
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

class AWSLabelDetectorHelperTests {

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
    // TODOR REVIEW To avoid exception (self generated ;) test before deleting
    // RES > Check if object exist before deleting
    void afterEach() {
        if (_awsClient.DoesObjectExists("testing123")) {
            _awsClient.DeleteObject("testing123");
        }
    }

    @AfterAll
    // TODOR REVIEW To avoid exception (self generated ;) test before closing
    // RES > Explanations as to why there is no test before closin in the README
    // Test and Generation chapter
    static void tearDown() {
        _awsClient.Close();
    }

    @Test
    void testCaching_True() {
        // TODO JEREMIE:
        // This is dangerous as it'll clear the logging in the production container every time we run the tests...
        // We'll loose the information about the past transactions.
        // As we're testing the caching mechanism and not the logging one
        // and as the logging mechanism will be ported somewhere else
        // We don't need it. But let's keep it here for now :)
        _awsClient.ResetLogging();
        List<AwsPatternDetected> result = _awsClient.Execute("testing123", null);

        assertTrue(_awsClient.DoesObjectExists("testing123_result"));

        // Check Caching
        List<AwsPatternDetected> cachedResult = _awsClient.Execute("testing123", null);
        // TODO JEREMIE same remark as previously
        assertTrue(_awsClient.DoesObjectExists("logs"));
        assertEquals(result.size(), cachedResult.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).name, cachedResult.get(i).name);
            assertEquals(result.get(i).confidence, cachedResult.get(i).confidence);
        }
    }

    // TODO JEREMIE same remark as previously
    @Test
    void testTransactionLog() {
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
