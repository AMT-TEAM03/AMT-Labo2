package LabelDetector.CloudProvider.AWS.JSON;

public class AwsTimeTaken implements IAwsJsonResponse{
    public long time;

    public AwsTimeTaken(long time){
        this.time = time;
    }
}
