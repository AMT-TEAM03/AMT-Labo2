package CloudProvider.AWS;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import CloudProvider.IDataObject;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;

public class AwsDataObjectHelper implements IDataObject{
    private S3Client s3Client;

    public AwsDataObjectHelper(){
        s3Client = S3Client.builder()
                .build();
    }

    public URL CreateObject(String objectKey, byte[] content){
        String bucketUrl = AwsCloudClient.getInstance().GetBucketUrl();
        S3Presigner presigner = AwsCloudClient.getInstance().GetPresigner();
        if(bucketUrl == null){
            throw new Error("Bucket URL not set...");
        }
        if(presigner == null){
            throw new Error("No Presigner to generate URL...");
        }
        if(DoesObjectExists(objectKey)){
            throw new Error("File already exists in the bucket...");
        }
        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketUrl)
                    .key(objectKey)
                    .build();
            PutObjectResponse response = s3Client.putObject(putOb, RequestBody.fromBytes(content));
            // Generate URL valid for 60 minutes
            // Create a GetObjectRequest to be pre-signed
            GetObjectRequest getObjectRequest =
                    GetObjectRequest.builder()
                                    .bucket(bucketUrl)
                                    .key(objectKey)
                                    .build();

            // Create a GetObjectPresignRequest to specify the signature duration
            GetObjectPresignRequest getObjectPresignRequest =
                GetObjectPresignRequest.builder()
                                        .signatureDuration(Duration.ofMinutes(60))
                                        .getObjectRequest(getObjectRequest)
                                        .build();

            // Generate the presigned request
            PresignedGetObjectRequest presignedGetObjectRequest =
                presigner.presignGetObject(getObjectPresignRequest);
        
            // Log the presigned URL, for example.
            return presignedGetObjectRequest.url();
        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void DeleteObject(String objectKey){
        AwsCloudClient client = AwsCloudClient.getInstance();
        String bucketUrl = client.GetBucketUrl();
        if(bucketUrl == null){
            throw new Error("Bucket URL not set...");
        }
        DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                        .bucket(bucketUrl)
                        .key(objectKey)
                        .build();
        s3Client.deleteObject(delReq);
        // Remove the label detection cache if exists 
        if(client.DoesObjectExists(objectKey + "_result")){
            delReq = DeleteObjectRequest.builder()
                        .bucket(bucketUrl)
                        .key(objectKey + "_result")
                        .build();
            s3Client.deleteObject(delReq);
        }
    }

    public InputStream GetObject(String objectKey){
        String bucketUrl = AwsCloudClient.getInstance().GetBucketUrl();
        if(bucketUrl == null){
            throw new Error("Bucket URL not set...");
        }
        GetObjectRequest request = GetObjectRequest.builder()
                                    .bucket(bucketUrl)
                                    .key(objectKey)
                                    .build();
        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
        return response;
    }

    public List<S3Object> ListObjects(){
        String bucketUrl = AwsCloudClient.getInstance().GetBucketUrl();
        if(bucketUrl == null){
            throw new Error("Bucket URL not set...");
        }
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketUrl)
                    .build();

            ListObjectsResponse res = s3Client.listObjects(listObjects);
            return res.contents();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            return null;
        }
    }
    
    public boolean DoesObjectExists(String  objectKey){
        String bucketUrl = AwsCloudClient.getInstance().GetBucketUrl();
        if(bucketUrl == null){
            throw new Error("Bucket URL not set...");
        }
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketUrl)
                .key(objectKey)
                .build(); 
        try {
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    public void CreateBucket(String bucketName){
        try {
            S3Waiter s3Waiter = s3Client.waiter();
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.createBucket(bucketRequest);
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            // Wait until the bucket is created and print out the response.
            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
            waiterResponse.matched().response().ifPresent(System.out::println);
            System.out.println(bucketName + " is ready");
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    public void DeleteBucket(String bucketName){
        // To delete a bucket, all the objects in the bucket must be deleted first.
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();
        ListObjectsV2Response listObjectsV2Response;

        do {
            listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
            for (S3Object s3Object : listObjectsV2Response.contents()) {
                DeleteObjectRequest request = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Object.key())
                        .build();
                s3Client.deleteObject(request);
            }
        } while (listObjectsV2Response.isTruncated());
        // Then we delete the empty bucket
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                .bucket(bucketName)
                .build();

        s3Client.deleteBucket(deleteBucketRequest);
        s3Client.close();

    }

    public List<String> ListBuckets(){
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = s3Client.listBuckets(listBucketsRequest);
        List<String> result = new ArrayList<String>();
        listBucketsResponse.buckets().stream().forEach(x -> result.add(x.name()));
        return result;
    }

    public void Close(){
        s3Client.close();
    }
}
 