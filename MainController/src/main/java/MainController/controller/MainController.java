package MainController.controller;

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

import MainController.utils.ErrorResponse;
import MainController.utils.IResponse;
import MainController.utils.SuccessResponse;

@RestController
@RequestMapping("/v1")
public class MainController {
    private String ObjectManagerUrl;
    private String LabelDetectorUrl;
    public MainController(){
        ObjectManagerUrl = "http://localhost:9090";
        LabelDetectorUrl = "http://localhost:8787";
    }

    // TODO Endpoints after BDD
}
