package CloudProvider.AWS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import CloudProvider.IDataObject;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.core.sync.RequestBody;

public class AwsDataObjectHelper implements IDataObject{
    private S3Client s3Client;

    public AwsDataObjectHelper(ProfileCredentialsProvider profile){
        s3Client = S3Client.builder()
                .credentialsProvider(profile)
                .build();
    }

    public void CreateObject(String objectKey, String objectPath){
        String bucketUrl = AwsCloudClient.getInstance().GetBucketUrl();
        if(bucketUrl == null){
            throw new Error("Bucket URL not set...");
        }
        try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("x-amz-meta-myVal", "test");
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketUrl)
                    .key(objectKey)
                    .metadata(metadata)
                    .build();

            PutObjectResponse response = s3Client.putObject(putOb, RequestBody.fromBytes(getObjectFile(objectPath)));
            System.out.println(response.eTag());
        } catch (S3Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // public void CreateBucket(String bucket){
    //     ????? We don't have permission....
    // }

    public List<String> listBuckets(){
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = s3Client.listBuckets(listBucketsRequest);
        List<String> result = new ArrayList<String>();
        listBucketsResponse.buckets().stream().forEach(x -> result.add(x.name()));
        return result;
    }

    public void Close(){
        s3Client.close();
    }

    private static byte[] getObjectFile(String filePath) {
        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesArray;
    }
}
 