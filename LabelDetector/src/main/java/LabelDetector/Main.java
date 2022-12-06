package LabelDetector;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//import CloudProvider.AWS.JSON.AwsPatternDetected;
import LabelDetector.CloudProvider.AWS.AwsLabelDetectorHelper;
import LabelDetector.CloudProvider.AWS.JSON.IAwsJsonResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(Main.class, args);
/*
        AwsLabelDetectorHelper tmp = new AwsLabelDetectorHelper();

        URL tmp2 = new URL("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg");

        List<IAwsJsonResponse> tmp3 = new ArrayList<>();

        tmp3 = tmp.Execute(tmp2);

        System.out.println(tmp3);*/

    }
}