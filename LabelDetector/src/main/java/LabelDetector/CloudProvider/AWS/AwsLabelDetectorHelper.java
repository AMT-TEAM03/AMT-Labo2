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

    public AwsLabelDetectorHelper(){
        rekClient = RekognitionClient.builder().build();
    }

    public AwsReckognitionResult Analyze(URL imageUrl, int maxPattern, float confidence_threshold) throws IllegalArgumentException, IOException {
        AwsReckognitionResult result = new AwsReckognitionResult();
        List<AwsPatternDetected> listPattern = new ArrayList<>();
        Gson g = new Gson();
        // Detect the labels
        List<Label> labels = new ArrayList<>();
        try {
            InputStream is = imageUrl.openStream();

            DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                    .image(Image.builder().bytes(SdkBytes.fromByteArray(IoUtils.toByteArray(is))).build())
                    .maxLabels(maxPattern).minConfidence(confidence_threshold)
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

    public AwsReckognitionResult Analyze(URL imageUrl, int maxPattern) throws IllegalArgumentException, IOException{
        float confidence_threshold = 90;
        return Analyze(imageUrl, maxPattern, confidence_threshold);
    }

    public AwsReckognitionResult Analyze(URL imageUrl, float confidence_threshold) throws IllegalArgumentException, IOException{
        int maxPattern = 10;
        return Analyze(imageUrl, maxPattern, confidence_threshold);
    }

    public AwsReckognitionResult Analyze(URL imageUrl) throws IllegalArgumentException, IOException{
        int maxPattern = 10;
        float confidence_threshold = 90;
        return Analyze(imageUrl, maxPattern, confidence_threshold);
    }
}
