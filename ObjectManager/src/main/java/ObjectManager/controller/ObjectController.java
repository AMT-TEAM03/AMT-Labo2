package ObjectManager.controller;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ObjectManager.controller.request.ObjectRequest;
import ObjectManager.dto.ObjectDTO;
import ObjectManager.dto.mapper.ObjectDTOMapper;
import ObjectManager.service.ObjectService;
import ObjectManager.utils.ErrorResponse;
import ObjectManager.utils.IResponse;
import ObjectManager.utils.SuccessResponse;

@RestController
@RequestMapping("/v1")
public class ObjectController {
    @Autowired
    private ObjectService service;

    @PostMapping(value="/object", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponse> CreateObject(
        @RequestBody ObjectRequest request
    ){
        ObjectDTO dto = new ObjectDTOMapper().mapToModel(request);
        if(dto.getName() == null || dto.getImage() == null){
            return new ResponseEntity<>(new ErrorResponse("Invalid arguments."), HttpStatus.BAD_REQUEST);
        }
        try{
            service.CreateObject(dto);
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new SuccessResponse<>("Image Created."), HttpStatus.OK);
    }

    @GetMapping(value="/object", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponse> DownloadObject(
        @RequestBody ObjectRequest request
    ){
        try{
            ObjectDTO dto = new ObjectDTOMapper().mapToModel(request);
            InputStream object = service.DownloadObject(dto);
            return new ResponseEntity<>(new SuccessResponse<>(object), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value="/object")
    public ResponseEntity<IResponse> DeleteObject(
        @RequestBody ObjectRequest request
    ){
        try{
            ObjectDTO dto = new ObjectDTOMapper().mapToModel(request);
            if(service.DoesObjectExists(dto)){   
                service.DeleteObject(dto);
            }
            return new ResponseEntity<>(new SuccessResponse<>("success"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value="/objects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponse> ListObjects(){
        try{
            List<String> response = service.ListObjects(new ObjectDTO());
            if(response == null){
                return new ResponseEntity<>(new ErrorResponse("Empty bucket."), HttpStatus.EXPECTATION_FAILED);
            }
            return new ResponseEntity<>(new SuccessResponse<>(response), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value="/object/url")
    public ResponseEntity<IResponse> GetObjectUrl(
        @RequestBody ObjectRequest request
    ){
        ObjectDTO dto = new ObjectDTOMapper().mapToModel(request);
        if(dto.getName() == null){
            return new ResponseEntity<>(new ErrorResponse("Invalid arguments."), HttpStatus.BAD_REQUEST);
        }
        try{
            URL objectUrl = service.GetObjectUrl(dto);
            return new ResponseEntity<>(new SuccessResponse<>(objectUrl.toString()), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value="/object/exists")
    public ResponseEntity<IResponse> DoesObjectExists(
        @RequestBody ObjectRequest request
    ){
        ObjectDTO dto = new ObjectDTOMapper().mapToModel(request);
        if(dto.getName() == null){
            return new ResponseEntity<>(new ErrorResponse("Invalid arguments."), HttpStatus.BAD_REQUEST);
        }
        try{
            if(service.DoesObjectExists(dto)){
                return new ResponseEntity<>(new SuccessResponse<>(true), HttpStatus.OK);
            }
            return new ResponseEntity<>(new SuccessResponse<>(false), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
