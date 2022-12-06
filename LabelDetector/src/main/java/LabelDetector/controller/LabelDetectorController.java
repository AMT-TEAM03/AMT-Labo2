package LabelDetector.controller;


import LabelDetector.CloudProvider.AWS.AwsLabelDetectorHelper;
import LabelDetector.CloudProvider.AWS.JSON.AwsPatternDetected;
import LabelDetector.CloudProvider.AWS.JSON.AwsTimeTaken;
import LabelDetector.CloudProvider.AWS.JSON.IAwsJsonResponse;
import LabelDetector.utils.ErrorResponse;
import LabelDetector.utils.IResponse;
import LabelDetector.utils.SuccessResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
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

    @PutMapping(value = "/confidence", produces = MediaType.APPLICATION_JSON_VALUE)
    public IResponse SetConfidence(
            @RequestParam(value="confidence", defaultValue="90") int confidenceLvl
    ){
        try{
            detector.SetConfidenceThreshold(confidenceLvl);
            return new SuccessResponse<>("Success");
        }catch(Exception e){
            return new ErrorResponse(e.getMessage());
        }
    }

    @GetMapping(value = "/confidence", produces = MediaType.APPLICATION_JSON_VALUE)
    public IResponse GetConfidence(){
        try{
            return new SuccessResponse<>(detector.GetConfidenceThreshold());
        }catch (Exception e){
            return new ErrorResponse(e.getMessage());
        }
    }

    @PutMapping(value = "/max_pattern", produces = MediaType.APPLICATION_JSON_VALUE)
    public IResponse SetMaxPattern(
            @RequestParam(value="maxPattern", defaultValue="90") int maxPattern
    ){
        try{
            detector.SetMaxPattern(maxPattern);
            return new SuccessResponse<>("Success");
        }catch(Exception e){
            return new ErrorResponse(e.getMessage());
        }
    }

    @GetMapping(value = "/max_pattern", produces = MediaType.APPLICATION_JSON_VALUE)
    public IResponse GetMaxPattern(){
        try{
            return new SuccessResponse<>(detector.GetMaxPattern());
        }catch (Exception e){
            return new ErrorResponse(e.getMessage());
        }
    }

    @GetMapping(value = "/execute", produces = MediaType.APPLICATION_JSON_VALUE)
    public IResponse GetLabels(
            @RequestParam(value="imageUrlString", defaultValue="None") String imageUrlString,
            @RequestParam(value="maxPattern", defaultValue= "10") int maxPattern,
            @RequestParam(value="minConfidence", defaultValue="90") int minConfidence
    ){
        try {
            URL imageUrl = new URL(imageUrlString);
            detector.SetMaxPattern(maxPattern);
            detector.SetConfidenceThreshold(minConfidence);
            List<IAwsJsonResponse> response = detector.Execute(imageUrl);
            if(response == null){
                return new ErrorResponse("Label detection failed");
            }
            return new SuccessResponse<>(response);
        }catch(Exception e){
            return new ErrorResponse(e.getMessage());
        }
    }
}
