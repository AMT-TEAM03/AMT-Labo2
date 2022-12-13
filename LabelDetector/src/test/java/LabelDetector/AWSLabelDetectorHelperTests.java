package LabelDetector;

import LabelDetector.CloudProvider.AWS.AwsLabelDetectorHelper;
import LabelDetector.CloudProvider.AWS.JSON.AwsPatternDetected;
import LabelDetector.CloudProvider.AWS.JSON.AwsReckognitionResult;
import org.junit.jupiter.api.*;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class AWSLabelDetectorHelperTests {

    static URL _imageUrl;
    AwsLabelDetectorHelper helper = new AwsLabelDetectorHelper();

    @BeforeAll
    static void beforeAll() throws IOException {
        _imageUrl = new URL("https://www.camerahouse.com.au/blog/wp-content/uploads/2020/01/street-on-cloudy-day.jpg");
    }

    @Test
    void analyze_ParametersDefaultValues_ContentFromAwsRekognitionWithoutFilter() throws IOException {
        //given
        assertNotNull(ImageIO.read(_imageUrl)); //_imageUrl est une image
        //when
        AwsReckognitionResult reckognitionResult = helper.Analyze(_imageUrl);
        //then
        assertTrue(reckognitionResult.getPatternDetected().size() <= 10);
        for(AwsPatternDetected i: reckognitionResult.getPatternDetected()){
            assertTrue(i.confidence > 90);
        }
    }

    @Test
    void analyze_MaxLabelsEqual20_ContentFromAwsRekognitionFilterApplied() throws IOException {
        //given
        assertNotNull(ImageIO.read(_imageUrl)); //_imageUrl est une image
        //when
        AwsReckognitionResult reckognitionResult = helper.Analyze(_imageUrl);
        //then
        assertTrue(reckognitionResult.getPatternDetected().size() <= 20
                && reckognitionResult.getPatternDetected().size() >= 10);
    }

    @Test
    void analyze_MinConfidenceLevelEqual70_ContentFromAwsRekognitionFilterApplied() throws IOException {
        //given
        assertNotNull(ImageIO.read(_imageUrl)); //_imageUrl est une image
        //when
        AwsReckognitionResult reckognitionResult = helper.Analyze(_imageUrl);
        //then
        for(AwsPatternDetected i: reckognitionResult.getPatternDetected()){
            assertTrue(i.confidence > 70);
        }
    }

    @Test
    void analyse_MaxLabel30AndConfidenceLevel50_ContentFromAwsRekognitionFilterApplied() throws IOException {
        //given
        assertNotNull(ImageIO.read(_imageUrl)); //_imageUrl est une image
        //when
        AwsReckognitionResult reckognitionResult = helper.Analyze(_imageUrl);
        //then
        assertTrue(reckognitionResult.getPatternDetected().size() <= 30);
        for(AwsPatternDetected i: reckognitionResult.getPatternDetected()){
            assertTrue(i.confidence > 50);
        }
    }

    @Test
    void analyze_PictureExist_ListReturnedWithTimeAndLabels() throws IOException {
        //given
        assertNotNull(ImageIO.read(_imageUrl));
        //when
        AwsReckognitionResult reckognitionResult = helper.Analyze(_imageUrl);
        //then
        assertTrue(reckognitionResult.getTime() > 0);
        assertNotNull(reckognitionResult.getPatternDetected());
    }

    @Test
    void analyze_PictureDoesntExist_ThrowRekognitionException() throws MalformedURLException {
        //given
        URL _wrongUrl = new URL("https://www.google.ch");
        //when
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, ()->{helper.Analyze(_wrongUrl);});
        //then
        assertTrue(thrown.getMessage().contains("Reckognition has encountered an issue : "));
    }
}
