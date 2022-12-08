package LabelDetector.CloudProvider.AWS.JSON;

public class AwsLogEntry {
    public long duration;
    public String fileTreatedKey;
    
    public AwsLogEntry(String fileTreatedKey, long duration){
        this.fileTreatedKey = fileTreatedKey;
        this.duration = duration;
    }
}
