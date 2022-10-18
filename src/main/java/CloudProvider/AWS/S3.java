package CloudProvider.AWS;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class S3 {
    // Singleton
    private static S3 INSTANCE;

    private S3Client s3Client;

    private S3() {
        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        s3Client = S3Client.builder()
                .credentialsProvider(credentialsProvider)
                .build();
    }

    public static S3 getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new S3();
        }

        return INSTANCE;
    }

    public void close(){
        s3Client.close();
    }

    public List<String> listBuckets(){
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = s3Client.listBuckets(listBucketsRequest);
        List<String> result = new ArrayList<String>();
        listBucketsResponse.buckets().stream().forEach(x -> result.add(x.name()));
        return result;
    }

    public String uploadImgToBucket(String bucket, String imageKey, String imagePath){
        try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("x-amz-meta-myVal", "test");
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(imageKey)
                    .metadata(metadata)
                    .build();

            PutObjectResponse response = s3Client.putObject(putOb, RequestBody.fromBytes(getObjectFile(imagePath)));
            return response.eTag();
        } catch (S3Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
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
