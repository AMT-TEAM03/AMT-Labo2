package MainController.CloudProvider.AWS.JSON;

import java.util.List;

public class AwsReckognitionResult{
    public long time = 0;
    public List<AwsPatternDetected> patternDetected;

    public AwsReckognitionResult(){}
    public AwsReckognitionResult(long time, List<AwsPatternDetected> patternDetected){
        this.time = time;
        this.patternDetected = patternDetected;
    }

    public List<AwsPatternDetected> getPatternDetected() {
        return patternDetected;
    }

    public long getTime() {
        return time;
    }

    public void setPatternDetected(List<AwsPatternDetected> patternDetected) {
        this.patternDetected = patternDetected;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
