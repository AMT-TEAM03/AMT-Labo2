package CloudProvider.AWS;

import java.util.List;
import java.util.Map;

import CloudProvider.ICloudClient;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.s3.model.S3Object;

public class AwsCloudClient implements ICloudClient {
    // Singleton
    private static AwsCloudClient INSTANCE = null;
    // Private attributes
    private AwsDataObjectHelper objectHelper;
    private AwsLabelDetectorHelper labelHelper;
    private String bucketUrl;

    private AwsCloudClient(String bucketUrl){
        ProfileCredentialsProvider profile = ProfileCredentialsProvider.create();
        objectHelper = new AwsDataObjectHelper(profile);
        labelHelper = new AwsLabelDetectorHelper(profile);
        this.bucketUrl = bucketUrl;
    }

    public static AwsCloudClient getInstance(){
        if(INSTANCE == null){
            AwsCloudClient.getInstance("amt.team03.diduno.education");
        }
        return INSTANCE;
    }

    public static AwsCloudClient getInstance(String bucketUrl) {
        if(INSTANCE == null){
            INSTANCE = new AwsCloudClient(bucketUrl);
        }
        return INSTANCE;
    }

    public String GetBucketUrl(){
        return this.bucketUrl;
    }

    public void SetBucketUrl(String bucketUrl){
        this.bucketUrl = bucketUrl;
    }

    public List<String> ListBucket(){
        return objectHelper.ListBuckets();
    }

    public void CreateBucket(String bucketName){
        objectHelper.CreateBucket(bucketName);
    }

    public void DeleteBucket(String bucketName){
        objectHelper.DeleteBucket(bucketName);
    }

    public void CreateObject(String objectName, String base64Img){
        objectHelper.CreateObject(objectName, base64Img);
    }

    public void DeleteObject(String objectKey){
        objectHelper.DeleteObject(objectKey);
    }

    public List<S3Object> ListObjects(){
        return objectHelper.ListObjects();
    }

    public boolean DoesObjectExists(String objectKey){
        return objectHelper.DoesObjectExists(objectKey);
    }

    public List<String> Execute(String imageUri, Map<String, Object> params){
        return labelHelper.Execute(imageUri, params);
    }
    
    public void Close() {
        objectHelper.Close();
        labelHelper.Close();
        INSTANCE = null;
    }
}
