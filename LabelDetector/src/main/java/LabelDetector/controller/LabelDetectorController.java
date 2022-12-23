package LabelDetector.controller;

import LabelDetector.CloudProvider.AWS.JSON.AwsReckognitionResult;
import LabelDetector.controller.request.LabelRequest;
import LabelDetector.dto.LabelDTO;
import LabelDetector.dto.mapper.LabelDTOMapper;
import LabelDetector.service.LabelDetectorService;
import LabelDetector.utils.ErrorResponse;
import LabelDetector.utils.IResponse;
import LabelDetector.utils.SuccessResponse;

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

    @PostMapping(value = "/analyze", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponse> GetLabels(
            @RequestBody LabelRequest request
    ){
        try {
            LabelDTO dto = new LabelDTOMapper().mapToModel(request);
            AwsReckognitionResult response = service.Analyze(dto);
            return new ResponseEntity<>(new SuccessResponse<>(response), HttpStatus.OK);
        }catch(MalformedURLException e){
            return new ResponseEntity<>(new ErrorResponse("Malformed URL" + e.getMessage()), HttpStatus.BAD_REQUEST);
        }catch (IllegalArgumentException | IOException e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
