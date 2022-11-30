package ObjectManager.controller;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ObjectManager.CloudProvider.AWS.AwsDataObjectHelper;
import ObjectManager.utils.ErrorResponse;
import ObjectManager.utils.IResponse;
import ObjectManager.utils.SuccessResponse;

@RestController
public class ObjectController {
    private AwsDataObjectHelper objectHelper;

    public ObjectController(){
        objectHelper = new AwsDataObjectHelper();
    }

    @PostMapping(value="/v1/object", produces = MediaType.APPLICATION_JSON_VALUE)
    public IResponse CreateObject(
        @RequestParam(value="name", defaultValue="None") String name,
        @RequestParam(value="image", defaultValue="None") String objectBase64
    ){
        if(name == "None" || objectBase64 == "None"){
            return new ErrorResponse("Invalid arguments.");
        }
        try{
            URL imageUrl = objectHelper.CreateObject(name, objectBase64.getBytes());
            if (imageUrl == null) {
                return new ErrorResponse("Image creation failed.");
            }
        }catch(Exception e){
            return new ErrorResponse(e.getMessage());
        }
        return new SuccessResponse<>("Image Created.");
    }

    @GetMapping(value="/v1/object", produces = MediaType.APPLICATION_JSON_VALUE)
    public IResponse DownloadObject(
        @RequestParam(value="key", defaultValue = "None") String key
    ){
        try{
            InputStream object = objectHelper.GetObject(key);
            return new SuccessResponse<>(object);
        }catch(Exception e){
            return new ErrorResponse(e.getMessage());
        }
    }

    @GetMapping(value="/v1/objects", produces = MediaType.APPLICATION_JSON_VALUE)
    public IResponse ListObjects(){
        try{
            List<String> response = objectHelper.ListObjects();
            if(response == null){
                return new ErrorResponse("Empty bucket.");
            }
            return new SuccessResponse<>(response);
        }catch(Exception e){
            return new ErrorResponse(e.getMessage());
        }
    }
}
