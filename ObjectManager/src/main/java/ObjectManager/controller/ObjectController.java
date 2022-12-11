package ObjectManager.controller;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ObjectManager.CloudProvider.AWS.AwsDataObjectHelper;
import ObjectManager.utils.ErrorResponse;
import ObjectManager.utils.IResponse;
import ObjectManager.utils.SuccessResponse;

@RestController
@RequestMapping("/v1")
public class ObjectController {
    private AwsDataObjectHelper objectHelper;

    public ObjectController(){
        objectHelper = new AwsDataObjectHelper();
    }

    @PostMapping(value="/object", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponse> CreateObject(
        @RequestParam(value="name", defaultValue="None") String name,
        @RequestParam(value="image", defaultValue="None") String objectBase64
    ){
        if(name == "None" || objectBase64 == "None"){
            return new ResponseEntity<>(new ErrorResponse("Invalid arguments."), HttpStatus.BAD_REQUEST);
        }
        try{
            if(!objectHelper.DoesBucketExists()){
                objectHelper.CreateBucket();
            }
            objectHelper.CreateObject(name, objectBase64.getBytes());
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new SuccessResponse<>("Image Created."), HttpStatus.OK);
    }

    @GetMapping(value="/object/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponse> DownloadObject(
        @PathVariable(value="name") String name
    ){
        try{
            InputStream object = objectHelper.GetObject(name);
            return new ResponseEntity<>(new SuccessResponse<>(object), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value="/object/{name}")
    public ResponseEntity<IResponse> DeleteObject(
        @PathVariable(value="name") String name
    ){
        try{
            if(objectHelper.DoesObjectExists(name)){   
                objectHelper.DeleteObject(name);
            }
            return new ResponseEntity<>(new SuccessResponse<>("success"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value="/objects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponse> ListObjects(){
        try{
            List<String> response = objectHelper.ListObjects();
            if(response == null){
                return new ResponseEntity<>(new ErrorResponse("Empty bucket."), HttpStatus.EXPECTATION_FAILED);
            }
            return new ResponseEntity<>(new SuccessResponse<>(response), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value="/object/publish")
    public ResponseEntity<IResponse> GetObjectUrl(
        @RequestParam(value="name", defaultValue="None") String name,
        @RequestParam(value="expiration-time", defaultValue="90") int expirationTime
    ){
        if(name == "None"){
            return new ResponseEntity<>(new ErrorResponse("Invalid arguments."), HttpStatus.BAD_REQUEST);
        }
        try{
            URL objectUrl = objectHelper.GetUrl(name, expirationTime);
            return new ResponseEntity<>(new SuccessResponse<>(objectUrl.toString()), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value="/object/exists")
    public ResponseEntity<IResponse> DoesObjectExists(
        @RequestParam(value="name", defaultValue="None") String name
    ){
        if(name == "None"){
            return new ResponseEntity<>(new ErrorResponse("Invalid arguments."), HttpStatus.BAD_REQUEST);
        }
        try{
            if(objectHelper.DoesObjectExists(name)){
                return new ResponseEntity<>(new SuccessResponse<>(true), HttpStatus.OK);
            }
            return new ResponseEntity<>(new SuccessResponse<>(false), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
