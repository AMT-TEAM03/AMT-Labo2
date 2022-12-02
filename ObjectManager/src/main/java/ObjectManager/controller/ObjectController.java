package ObjectManager.controller;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.springframework.http.MediaType;
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

    @GetMapping(value="/object/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public IResponse DownloadObject(
        @PathVariable(value="name") String name
    ){
        try{
            InputStream object = objectHelper.GetObject(name);
            return new SuccessResponse<>(object);
        }catch(Exception e){
            return new ErrorResponse(e.getMessage());
        }
    }

    @DeleteMapping(value="/object/{name}")
    public IResponse DeleteObject(
        @PathVariable(value="name") String name
    ){
        try{
            objectHelper.DeleteObject(name);
            return new SuccessResponse<>("success");
        }catch(Exception e){
            return new ErrorResponse(e.getMessage());
        }
    }

    @GetMapping(value="/objects", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(value="/object/{name}/url")
    public IResponse GetObjectUrl(
        @PathVariable(value="name") String name
    ){
        try{
            URL objectUrl = objectHelper.GetUrl(name);
            return new SuccessResponse<>(objectUrl.toString());
        }catch(Exception e){
            return new ErrorResponse(e.getMessage());
        }
    }
}
