package CloudProvider.AWS;

import CloudProvider.ICloudClient;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;

public class AwsCloudClient implements ICloudClient {
    // Singleton
    private static AwsCloudClient INSTANCE = null;
    // Private attributes
    private AwsDataObjectHelper objectHelper;
    private AwsLabelDetectorHelper labelHelper;

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

    public void Close() {
        objectHelper.Close();
        labelHelper.Close();
        INSTANCE = null;
    }
    
}
