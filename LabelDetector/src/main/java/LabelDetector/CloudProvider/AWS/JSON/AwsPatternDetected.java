package LabelDetector.CloudProvider.AWS.JSON;

public class AwsPatternDetected {
    public String name;
    public float confidence;

    public AwsPatternDetected(String name, float confidence){
        this.name = name;
        this.confidence = confidence;
    }
}
