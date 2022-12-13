package LabelDetector.controller;

import LabelDetector.CloudProvider.AWS.AwsLabelDetectorHelper;
import LabelDetector.CloudProvider.AWS.JSON.AwsReckognitionResult;
import LabelDetector.CloudProvider.ILabelDetector;
import LabelDetector.utils.ErrorResponse;
import LabelDetector.utils.IResponse;
import LabelDetector.utils.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@RestController
@RequestMapping("/v1")
public class LabelDetectorController {

    ILabelDetector detector;

    public LabelDetectorController() {
        this.detector = new AwsLabelDetectorHelper();
    }

    @GetMapping(value = "/analyze", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponse> GetLabels(
            @RequestParam(value="imageUrlString", defaultValue="None") String imageUrlString,
            @RequestParam(value="maxPattern", defaultValue= "10") int maxPattern,
            @RequestParam(value="minConfidence", defaultValue="90") float minConfidence
    ){
        try {
            URL imageUrl = new URL(imageUrlString);
            AwsReckognitionResult response = detector.Analyze(imageUrl, maxPattern, minConfidence);
            return new ResponseEntity<>(new SuccessResponse<>(response), HttpStatus.OK);
        }catch(MalformedURLException e){
            return new ResponseEntity<>(new ErrorResponse("Malformed URL" + e.getMessage()), HttpStatus.BAD_REQUEST);
        }catch (IllegalArgumentException | IOException e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
