package LabelDetector;

import LabelDetector.CloudProvider.AWS.AwsLabelDetectorHelper;
import LabelDetector.CloudProvider.AWS.JSON.AwsPatternDetected;
import LabelDetector.CloudProvider.AWS.JSON.AwsReckognitionResult;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class AWSLabelDetectorHelperTests {

    static URL _imageUrl, _wrongUrl;
    AwsLabelDetectorHelper helper = new AwsLabelDetectorHelper();

    @BeforeAll
    static void beforeAll() throws IOException {
        _imageUrl = new URL("https://www.camerahouse.com.au/blog/wp-content/uploads/2020/01/street-on-cloudy-day.jpg");
        _wrongUrl = new URL("https://www.google.ch");
    }

    @Test
    void Analyze_ParametersDefaultValues_ContentFromAwsRekognitionWithoutFilter() throws IOException {
        //given
        int defaultMaxLabels = helper.GetMaxPattern();
        float defaultMinConfidenceLevel = helper.GetConfidenceThreshold();
        //when
        AwsReckognitionResult reckognitionResult = helper.Execute(_imageUrl);
        //then
        assertEquals(10, defaultMaxLabels);
        assertEquals(90, defaultMinConfidenceLevel);
        assertTrue(reckognitionResult.getPatternDetected().size() <= 10);
        for(AwsPatternDetected i: reckognitionResult.getPatternDetected()){
            assertTrue(i.confidence > 90);
        }
    }

    @Test
    void Analyze_MaxLabelsEqual20_ContentFromAwsRekognitionFilterApplied() throws IOException {
        //given
        helper.SetMaxPattern(20);
        //when
        AwsReckognitionResult reckognitionResult = helper.Execute(_imageUrl);
        //then
        assertEquals(20, helper.GetMaxPattern());
        assertEquals(90, helper.GetConfidenceThreshold());
        assertTrue(reckognitionResult.getPatternDetected().size() <= 20 && reckognitionResult.getPatternDetected().size() >= 10);
    }

    @Test
    void Analyze_MinConfidenceLevelEqual70_ContentFromAwsRekognitionFilterApplied() throws IOException {
        //given
        helper.SetMaxPattern(20);
        helper.SetConfidenceThreshold(70);
        //when
        AwsReckognitionResult reckognitionResult = helper.Execute(_imageUrl);
        //then
        assertEquals(20, helper.GetMaxPattern());
        for(AwsPatternDetected i: reckognitionResult.getPatternDetected()){
            assertTrue(i.confidence > 70);
        }
    }

    @Test
    void Analyse_MaxLabel30AndConfidenceLevel50_ContentFromAwsRekognitionFilterApplied() throws IOException {
        //given
        helper.SetMaxPattern(30);
        helper.SetConfidenceThreshold(50);
        //when
        AwsReckognitionResult reckognitionResult = helper.Execute(_imageUrl);
        //then
        assertTrue(reckognitionResult.getPatternDetected().size() <= 30);
        for(AwsPatternDetected i: reckognitionResult.getPatternDetected()){
            assertTrue(i.confidence > 50);
        }
    }

    @Test
    void execute_PictureExist_ListReturnedWithTimeAndLabels() throws IOException {
        AwsReckognitionResult reckognitionResult = helper.Execute(_imageUrl);

        assertTrue(reckognitionResult.getTime() > 0);
        assertNotNull(reckognitionResult.getPatternDetected());
    }

    @Test
    void execute_PictureDoesntExist_ThrowRekognitionException(){
        assertThrows(IllegalArgumentException.class, ()->{helper.Execute(_wrongUrl);});
    }
}
