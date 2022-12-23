package LabelDetector.controller;

import LabelDetector.CloudProvider.AWS.JSON.AwsReckognitionResult;
import LabelDetector.controller.request.LabelRequest;
import LabelDetector.dto.LabelDTO;
import LabelDetector.dto.mapper.LabelDTOMapper;
import LabelDetector.service.LabelDetectorService;
import LabelDetector.utils.ErrorResponse;
import LabelDetector.utils.IResponse;
import LabelDetector.utils.SuccessResponse;

import org.springframework.asm.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequestMapping("/v1")
public class LabelDetectorController {
    @Autowired
    private LabelDetectorService service;

    @GetMapping(value = "/analyze", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponse> GetLabels(
        @RequestParam(value="imageUrl", defaultValue="None") String imageUrl,
        @RequestParam(value="maxPattern", defaultValue="10") int maxPattern,
        @RequestParam(value="minConfidence", defaultValue="90") float minConfidence
    ){
        try {
            LabelRequest tmp = new LabelRequest()
                .setImageUrl(imageUrl)
                .setMaxPattern(maxPattern)
                .setMinConfidence(minConfidence);
            LabelDTO dto = new LabelDTOMapper().mapToModel(tmp);
            AwsReckognitionResult response = service.Analyze(dto);
            return new ResponseEntity<>(new SuccessResponse<>(response), HttpStatus.OK);
        }catch(MalformedURLException e){
            return new ResponseEntity<>(new ErrorResponse("Malformed URL" + e.getMessage()), HttpStatus.BAD_REQUEST);
        }catch (IllegalArgumentException | IOException e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
