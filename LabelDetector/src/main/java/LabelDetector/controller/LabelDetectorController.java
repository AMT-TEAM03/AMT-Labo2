package LabelDetector.controller;


import LabelDetector.CloudProvider.AWS.AwsLabelDetectorHelper;
import LabelDetector.CloudProvider.AWS.JSON.AwsJsonInterface;
import LabelDetector.CloudProvider.AWS.JSON.AwsPatternDetected;
import LabelDetector.CloudProvider.AWS.JSON.AwsTimeTaken;
import LabelDetector.utils.ErrorResponse;
import LabelDetector.utils.IResponse;
import LabelDetector.utils.SuccessResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class LabelDetectorController {

    AwsLabelDetectorHelper detector;

    public LabelDetectorController() {
        this.detector = new AwsLabelDetectorHelper();
    }

    @PutMapping(value = "/confidence/set/{confidenceVal}", produces = MediaType.APPLICATION_JSON_VALUE)
    public IResponse SetConfidence(@PathVariable(value="confidenceVal") Integer confidenceVal){
        try{
            detector.SetConfidenceThreshold(confidenceVal);
            return new SuccessResponse<>("Success");
        }catch(Exception e){
            return new ErrorResponse(e.getMessage());
        }
    }

    @GetMapping(value = "/execute/{img}", produces = MediaType.APPLICATION_JSON_VALUE)
    public IResponse GetLabels(@PathVariable(value="img") String img){
        try {
            List<AwsJsonInterface> response = detector.Execute(img);
            if(response == null){
                return new ErrorResponse("Label detection failed");
            }
            return new SuccessResponse<>(detector.Execute(img));
        }catch(Exception e){
            return new ErrorResponse(e.getMessage());
        }
    }
}
