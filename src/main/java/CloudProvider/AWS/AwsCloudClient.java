package CloudProvider.AWS;

import java.util.List;
import java.util.Map;

import CloudProvider.ICloudClient;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;

public class AwsCloudClient implements ICloudClient {
    // Singleton
    private static AwsCloudClient INSTANCE = null;
    // Private attributes
    private AwsDataObjectHelper objectHelper;
    private AwsLabelDetectorHelper labelHelper;
    private String bucketUrl = null;

    private AwsCloudClient(){
        ProfileCredentialsProvider profile = ProfileCredentialsProvider.create();
        objectHelper = new AwsDataObjectHelper(profile);
        labelHelper = new AwsLabelDetectorHelper(profile);
    }

    public static AwsCloudClient getInstance() {
        if(INSTANCE == null){
            INSTANCE = new AwsCloudClient();
        }
        return INSTANCE;
    }

    public String GetBucketUrl(){
        return this.bucketUrl;
    }

    public void SetBucketUrl(String bucketUrl){
        this.bucketUrl = bucketUrl;
    }

    public void Close() {
        objectHelper.Close();
        labelHelper.Close();
        INSTANCE = null;
    }

    public void CreateObject(String objectName, String objectPath){
        objectHelper.CreateObject(objectName, objectPath);
    }

    public List<String> Execute(String imageUri, Map<String, Object> params){
        return labelHelper.Execute(imageUri, params);
    }
}
