package LabelDetector.CloudProvider.AWS.JSON;

public class AwsPatternDetected implements AwsJsonInterface {
    public String name;
    public float confidence;

    public AwsPatternDetected(String name, float confidence){
        this.name = name;
        this.confidence = confidence;
    }
}
