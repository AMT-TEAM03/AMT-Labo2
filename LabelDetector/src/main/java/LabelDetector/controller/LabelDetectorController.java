package LabelDetector.controller;

import LabelDetector.CloudProvider.AWS.AwsLabelDetectorHelper;
import LabelDetector.CloudProvider.AWS.JSON.AwsReckognitionResult;
import LabelDetector.controller.utils.ErrorResponse;
import LabelDetector.controller.utils.IResponse;
import LabelDetector.controller.utils.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;

@RestController
@RequestMapping("/v1")
public class LabelDetectorController {

    AwsLabelDetectorHelper detector;

    public LabelDetectorController() {
        this.detector = new AwsLabelDetectorHelper();
    }

    @PutMapping(value = "/confidence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponse> SetConfidence(
            @RequestParam(value="confidence", defaultValue="90") float confidenceLvl
    ){
        try{
            detector.SetConfidenceThreshold(confidenceLvl);
            return new ResponseEntity<>(new SuccessResponse<>("Success"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/confidence", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponse> GetConfidence(){
        try{
            return new ResponseEntity<>(new SuccessResponse<>(detector.GetConfidenceThreshold()), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/max_pattern", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponse> SetMaxPattern(
            @RequestParam(value="maxPattern", defaultValue="90") int maxPattern
    ){
        try{
            detector.SetMaxPattern(maxPattern);
            return new ResponseEntity<>(new SuccessResponse<>("Success"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/max_pattern", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponse> GetMaxPattern(){
        try{
            return new ResponseEntity<>(new SuccessResponse<>(detector.GetMaxPattern()), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/analyze", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponse> GetLabels(
            @RequestParam(value="imageUrlString", defaultValue="None") String imageUrlString,
            @RequestParam(value="maxPattern", defaultValue= "10") int maxPattern,
            @RequestParam(value="minConfidence", defaultValue="90") float minConfidence
    ){
        try {
            URL imageUrl = new URL(imageUrlString);
            detector.SetMaxPattern(maxPattern);
            detector.SetConfidenceThreshold(minConfidence);
            AwsReckognitionResult response = detector.Execute(imageUrl);
            return new ResponseEntity<>(new SuccessResponse<>(response), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
