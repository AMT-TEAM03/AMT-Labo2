package CloudProvider.AWS;

import CloudProvider.IDataObject;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;

public class AwsDataObjectHelper implements IDataObject{
    private S3Client s3Client;

    public AwsDataObjectHelper(ProfileCredentialsProvider profile){
        s3Client = S3Client.builder()
                .credentialsProvider(profile)
                .build();
    }

    public void Create(String objectName){

    }

    public void Close(){
        s3Client.close();
    }
}
