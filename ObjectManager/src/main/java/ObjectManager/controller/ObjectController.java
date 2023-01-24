package ObjectManager.controller;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // TODO partout : le endpoint "/object" devrait etre au pluriel car il
    // représente une collection d'objets, la remarque est valable pour tout les
    // /object
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
        // TODO created devrait retourner un 201 created et non un 200
        return new ResponseEntity<>(new SuccessResponse<>("Image Created."), HttpStatus.OK);
    }

    @GetMapping(value="/object", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IResponse> DownloadObject(
            @RequestParam(value="name", defaultValue="None") String name
    ){
        ObjectRequest tmp = new ObjectRequest().setName(name);
        try{
            ObjectDTO dto = new ObjectDTOMapper().mapToModel(tmp);
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
            return new ResponseEntity<>(new SuccessResponse<>("delete success"), HttpStatus.OK);
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

    // TODO il vous manque un selecteur dans l'URL du endpoint, on peut discuter de
    public ResponseEntity<IResponse> GetObjectUrl(
            @RequestParam(value="name", defaultValue="None") String name
    ){
        ObjectRequest tmp = new ObjectRequest().setName(name);
        ObjectDTO dto = new ObjectDTOMapper().mapToModel(tmp);
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
            @RequestParam(value="name", defaultValue="None") String name
    ){
        ObjectRequest tmp = new ObjectRequest().setName(name);
        ObjectDTO dto = new ObjectDTOMapper().mapToModel(tmp);
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

    // TODO ça devrait pas être ici. Si vous avez besoin de code spécifique pour run
    // les tests vous devriez le mettre ailleur
    // ou AU MINIMUM le désactiver avec un Feature Flag (== bool dans les variables
    // d'env)
    public ResponseEntity<IResponse> PrepareTestScenario(
        @RequestBody ObjectRequest request
    ){
        try{
            ObjectDTO dto = new ObjectDTOMapper().mapToModel(request);
            service.PrepareTestScenario(dto);
            return new ResponseEntity<>(new SuccessResponse<>(true), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
