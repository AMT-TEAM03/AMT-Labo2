package LabelDetector.CloudProvider.AWS;

import LabelDetector.CloudProvider.AWS.JSON.AwsPatternDetected;
import LabelDetector.CloudProvider.AWS.JSON.AwsReckognitionResult;
import LabelDetector.CloudProvider.ILabelDetector;
import com.google.gson.Gson;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.utils.IoUtils;

import java.io.*;
import java.net.URL;
import java.util.*;

public class AwsLabelDetectorHelper implements ILabelDetector<AwsPatternDetected> {
    private RekognitionClient rekClient;
    private float confidence_threshold = 90;
    private int maxPattern = 10;

    public AwsLabelDetectorHelper(){
        rekClient = RekognitionClient.builder().build();
    }

    public void SetConfidenceThreshold(float confidence){
        this.confidence_threshold = confidence;
    }

    public float GetConfidenceThreshold(){
        return this.confidence_threshold;
    }

    public void SetMaxPattern(int maxPattern){
        this.maxPattern = maxPattern;
    }

    public int GetMaxPattern(){
        return this.maxPattern;
    }

    public AwsReckognitionResult Execute(URL imageUrl) throws IllegalArgumentException, IOException {
        AwsReckognitionResult result = new AwsReckognitionResult();
        List<AwsPatternDetected> listPattern = new ArrayList<>();
        Gson g = new Gson();
        // Detect the labels
        List<Label> labels = new ArrayList<>();
        try {
            InputStream is = imageUrl.openStream();

            DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                    .image(Image.builder().bytes(SdkBytes.fromByteArray(IoUtils.toByteArray(is))).build())
                    .maxLabels(GetMaxPattern()).minConfidence(GetConfidenceThreshold())
                    .build();
            // Compute time for loggin
            long startTime = System.currentTimeMillis();
            DetectLabelsResponse labelsResponse = rekClient.detectLabels(detectLabelsRequest);
            long endTime = System.currentTimeMillis();
            long time = (endTime - startTime);

            result.setTime(time);

            // Extract labels from response
            labels = labelsResponse.labels();
        } catch (RekognitionException e) {
            throw new IllegalArgumentException("Reckognition has encountered an issue : " + e.getMessage());
        } catch (IOException e) {
            throw new IOException("URL not recognized" + e.getMessage());
        }
        // Parse patternList
        for (Label label : labels) {
            listPattern.add(new AwsPatternDetected(label.name(), label.confidence()));
        }
        result.setPatternDetected(listPattern);
        return result;
    }

}
