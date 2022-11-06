package CloudProvider.AWS;

import java.net.URL;
import java.util.List;
import java.util.Map;

import CloudProvider.ICloudClient;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

public class AwsCloudClient implements ICloudClient {
    // Singleton
    private static AwsCloudClient INSTANCE = null;
    // Private attributes
    // Presigner to generate object URLs.
    private S3Presigner presigner;
    private AwsDataObjectHelper objectHelper;
    private AwsLabelDetectorHelper labelHelper;
    private String bucketUrl;

    private AwsCloudClient(String bucketUrl){
        objectHelper = new AwsDataObjectHelper();
        labelHelper = new AwsLabelDetectorHelper();
        this.presigner = S3Presigner.create();
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

    public S3Presigner GetPresigner(){
        return this.presigner;
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

    public URL CreateObject(String objectName, String base64Img){
        return objectHelper.CreateObject(objectName, base64Img);
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
        presigner.close();
        INSTANCE = null;
    }
}
