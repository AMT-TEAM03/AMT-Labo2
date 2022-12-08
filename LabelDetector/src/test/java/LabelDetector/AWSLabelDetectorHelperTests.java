package LabelDetector;

import LabelDetector.CloudProvider.AWS.AwsLabelDetectorHelper;
import LabelDetector.CloudProvider.AWS.JSON.AwsReckognitionResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;

import java.io.*;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class AWSLabelDetectorHelperTests {

    static URL _imageUrl, _wrongUrl;
    AwsLabelDetectorHelper helper = new AwsLabelDetectorHelper();

    @BeforeAll
    static void beforeAll() throws IOException {
        _imageUrl = new URL("https://upload.wikimedia.org/wikipedia/commons/3/32/Googleplex_HQ_%28cropped%29.jpg");
        _wrongUrl = new URL("https://www.google.ch");
    }

    @Test
    void execute_PictureExist_ListReturnedWithTimeAndLabels() throws IOException {
        AwsReckognitionResult reckognitionResult = helper.Execute(_imageUrl);

        assertTrue(reckognitionResult.getTime() > 0);
        assertNotNull(reckognitionResult.getPatternDetected());
    }

    @Test
    void execute_PictureExist_ListIsOfRightSize() throws IOException {
        AwsReckognitionResult reckognitionResult = helper.Execute(_imageUrl);

        assertEquals(3, reckognitionResult.getPatternDetected().size());
    }

    @Test
    void execute_PictureDoesntExist_ThrowRekognitionException(){
        assertThrows(IllegalArgumentException.class, ()->{helper.Execute(_wrongUrl);});
    }
}
