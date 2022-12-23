package ObjectManager.CloudProvider.AWS;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.naming.OperationNotSupportedException;

import ObjectManager.CloudProvider.IDataObject;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
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
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
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
    private String bucketUrl;
    private S3Presigner presigner;

    public AwsDataObjectHelper(){
        s3Client = S3Client.builder()
                .build();
        this.presigner = S3Presigner.create();
        try {
            String propertiesPath = Paths.get(
                    getClass().getClassLoader().getResource("application.properties").toURI()).toFile()
                    .getAbsolutePath();
            InputStream input = new FileInputStream(propertiesPath);
            Properties prop = new Properties();
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            this.bucketUrl = prop.getProperty("bucket.url");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void CreateObject(String objectKey, byte[] content) throws Exception{
        String bucketUrl = this.bucketUrl;
        if(bucketUrl == null){
            throw new Exception("Bucket URL not set...");
        }
        if(!DoesBucketExists()){
            CreateBucket();
        }
        if(DoesObjectExists(objectKey)){
            throw new Exception("File already exists in the bucket...");
        }
        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketUrl)
                    .key(objectKey)
                    .build();
            s3Client.putObject(putOb, RequestBody.fromBytes(content));
        } catch (S3Exception e) {
            throw new Exception("S3 Client refused the request : " + e.getMessage());
        }
    }

    public URL GetUrl(String objectKey) throws Exception{
        String bucketUrl = this.bucketUrl;
        S3Presigner presigner = this.presigner;
        if (bucketUrl == null) {
            throw new Exception("Bucket URL not set...");
        }
        if (presigner == null) {
            throw new Exception("No Presigner to generate URL...");
        }
        if(!DoesObjectExists(objectKey)){
            throw new IllegalArgumentException("Object not found...");
        }
        // Generate URL valid for 60 minutes
        // Create a GetObjectRequest to be pre-signed
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketUrl)
                .key(objectKey)
                .build();
        // Create a GetObjectPresignRequest to specify the signature duration
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(getObjectRequest)
                .build();

        // Generate the presigned request
        PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);

        // Log the presigned URL, for example.
        return presignedGetObjectRequest.url();
    }

    public void DeleteObject(String objectKey, boolean recursive) throws Exception{
        String bucketUrl = this.bucketUrl;
        if(bucketUrl == null){
            throw new Exception("Bucket URL not set...");
        }
        if(!recursive){
            if (!DoesObjectExists(objectKey)) {
                throw new IllegalArgumentException("Object not found...");
            }
            DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                    .bucket(bucketUrl)
                    .key(objectKey)
                    .build();
            s3Client.deleteObject(delReq);
            // Remove the label detection cache if exists
            if (this.DoesObjectExists(objectKey + "_result")) {
                delReq = DeleteObjectRequest.builder()
                        .bucket(bucketUrl)
                        .key(objectKey + "_result")
                        .build();
                s3Client.deleteObject(delReq);
            }
        }else{
            // Request to find recursively the objects
            ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                .bucket(this.bucketUrl)
                .prefix(objectKey)
                .build();
            // Objects to delete
            ListObjectsResponse objectsResponse = s3Client.listObjects(listObjectsRequest);
            while (true) {
                ArrayList<ObjectIdentifier> objects = new ArrayList<>();
                for (Iterator<?> iterator = objectsResponse.contents().iterator(); iterator.hasNext();) {
                    S3Object s3Object = (S3Object) iterator.next();
                    objects.add(
                            ObjectIdentifier.builder()
                                    .key(s3Object.key())
                                    .build());
                }
                
                s3Client.deleteObjects(
                        DeleteObjectsRequest.builder()
                                .bucket(this.bucketUrl)
                                .delete(
                                        Delete.builder()
                                                .objects(objects)
                                                .build())
                                .build());

                if (objectsResponse.isTruncated()) {
                    objectsResponse = s3Client.listObjects(listObjectsRequest);
                    continue;
                }
                break;
            };
        }
    }

    public InputStream GetObject(String objectKey) throws Exception{
        String bucketUrl = this.bucketUrl;
        if(bucketUrl == null){
            throw new Exception("Bucket URL not set...");
        }
        GetObjectRequest request = GetObjectRequest.builder()
                                    .bucket(bucketUrl)
                                    .key(objectKey)
                                    .build();
        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
        return response;
    }

    public List<String> ListObjects() throws Exception{
        String bucketUrl = this.bucketUrl;
        if(bucketUrl == null){
            throw new Exception("Bucket URL not set...");
        }
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketUrl)
                    .build();

            ListObjectsResponse res = s3Client.listObjects(listObjects);
            return res.contents().stream().map(elem -> elem.key()).collect(Collectors.toList());
        } catch (S3Exception e) {
            throw new Exception("S3 Client refused the request : " + e.getMessage());
        }
    }
    
    public boolean DoesObjectExists(String  objectKey) throws Exception{
        String bucketUrl = this.bucketUrl;
        if(bucketUrl == null){
            throw new Exception("Bucket URL not set...");
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

    public boolean DoesBucketExists() throws Exception{
        if(this.bucketUrl == null){
            throw new Exception("Bucket URL not set...");
        }
        HeadBucketRequest headerBucketRequest = HeadBucketRequest.builder()
            .bucket(this.bucketUrl)
            .build();
        try{
            s3Client.headBucket(headerBucketRequest);
            return true;
        }catch(NoSuchBucketException e){
            return false;
        }
    }

    public void CreateBucket() throws Exception{
        try {
            S3Waiter s3Waiter = s3Client.waiter();
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(this.bucketUrl)
                    .build();

            s3Client.createBucket(bucketRequest);
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                    .bucket(this.bucketUrl)
                    .build();

            // Wait until the bucket is created and print out the response.
            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
            waiterResponse.matched().response().ifPresent(System.out::println);
        } catch (S3Exception e) {
            throw new Exception("S3 Client refused the request : " + e.getMessage());
        }
    }

    public void DeleteBucket(boolean recursive) throws OperationNotSupportedException{
        // To delete a bucket, all the objects in the bucket must be deleted first.
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(this.bucketUrl)
                .build();
        ListObjectsV2Response listObjectsV2Response;

        do {
            listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
            if(listObjectsV2Response.contents().size() > 0 && !recursive){
                throw new OperationNotSupportedException("Bucket is not empty...");
            }
            for (S3Object s3Object : listObjectsV2Response.contents()) {
                DeleteObjectRequest request = DeleteObjectRequest.builder()
                        .bucket(this.bucketUrl)
                        .key(s3Object.key())
                        .build();
                s3Client.deleteObject(request);
            }
        } while (listObjectsV2Response.isTruncated());
        // Then we delete the empty bucket
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                .bucket(this.bucketUrl)
                .build();

        s3Client.deleteBucket(deleteBucketRequest);
    }

    public List<String> ListBuckets(){
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = s3Client.listBuckets(listBucketsRequest);
        List<String> result = new ArrayList<String>();
        listBucketsResponse.buckets().stream().forEach(x -> result.add(x.name()));
        return result;
    }

    public void ResetLoggig() throws Exception{
        this.DeleteObject("logs", false);
    }

    public void Close(){
        s3Client.close();
    }
}
 