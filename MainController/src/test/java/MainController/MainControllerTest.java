package MainController;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import MainController.utils.SuccessResponse;
import MainController.utils.ErrorResponse;
import MainController.CloudProvider.AWS.JSON.AwsReckognitionResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.beans.Transient;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    static String _base64Img;
    static final String[] OBJECT_KEY_LIST = {
        "testing123.png",
        "testing1234.png"
    };

    private void cleanup() throws Exception {
        for (String i : OBJECT_KEY_LIST) {
            mockMvc.perform(delete("/v1/object/" + i)).andExpect(status().isOk());
        }
    }

    @BeforeTestClass
    void beforeAll() throws Exception {
        // Encode an image in a base64 like string
        // image path declaration
        String imgPath = "./src/main/resources/coucou.jpg";
        // read image from file
        FileInputStream stream = new FileInputStream(imgPath);
        // get byte array from image stream
        int bufLength = 2048;
        byte[] buffer = new byte[2048];
        byte[] data;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int readLength;
        while ((readLength = stream.read(buffer, 0, bufLength)) != -1) {
            out.write(buffer, 0, readLength);
        }
        data = out.toByteArray();
        _base64Img = Base64.getEncoder().withoutPadding().encodeToString(data);
        out.close();
        stream.close();
        // cleanup of possible old test DataObject
        cleanup();
    }

    @BeforeEach
    void beforeEach() throws Exception {
        mockMvc.perform(post("/v1/object")
            .param("name", OBJECT_KEY_LIST[0])
            .param("image", _base64Img)
        ).andExpect(status().isOk());
    }

    @AfterEach
    void afterEach() throws Exception {
        mockMvc.perform(delete("/v1/object/" + OBJECT_KEY_LIST[0]))
                .andExpect(status().isOk());
    }

    // UploadObject_ObjectDoesntExist_Success
    @Test
    public void UploadObject_ObjectDoesntExist_Success() throws Exception {
        mockMvc.perform(post("/v1/object")
            .param("name", OBJECT_KEY_LIST[1])
            .param("image", _base64Img)
        ).andExpect(status().isOk());
    }
    // UploadObject_ObjectExists_Fail
    @Test
    public void UploadObject_ObjectExists_Fail() throws Exception {
        MvcResult result = mockMvc.perform(post("/v1/object")
            .param("name", OBJECT_KEY_LIST[0])
            .param("image", _base64Img)
        ).andReturn();
        assertTrue(result.getResponse().getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value());
        ErrorResponse response = mapper.readValue(result.getResponse().getContentAsByteArray(),
                ErrorResponse.class);
        assertEquals(response.getError(), "File already exists in the bucket...");
    }
    // PublishObject_ObjectDoesntExist_Fail
    @Test
    public void PublishObject_ObjectDoesntExist_Fail() throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/object/publish")
            .param("name", OBJECT_KEY_LIST[1])
        ).andReturn();
        assertTrue(result.getResponse().getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value());
        ErrorResponse response = mapper.readValue(result.getResponse().getContentAsByteArray(),
                ErrorResponse.class);
        System.out.println(response.getError());
        assertEquals(response.getError(), "no such key");
    }
    // PublishObject_ObjectExists_Success
    @Test
    public void PublishObject_ObjectExists_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/object/publish")
            .param("name", OBJECT_KEY_LIST[0])
        ).andReturn();
        assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());
        SuccessResponse<String> response = mapper.readValue(result.getResponse().getContentAsByteArray(),
                SuccessResponse.class);
        assertTrue(response.getData().contains("https://"));
    }
    // Analyze_UrlNotValid_Fail
    @Test
    public void Analyze_UrlNotValid_Fail() throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/analyze")
            .param("imageUrlString", "http://lol.lol")
            .param("maxPattern", "10")
            .param("minConfidence", "90")
        ).andReturn();
        assertTrue(result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value());
        ErrorResponse response = mapper.readValue(result.getResponse().getContentAsByteArray(),
                ErrorResponse.class);
        System.out.println(response.getError());
        assertEquals(response.getError(), "whatever it returns...");
    }
    // Analyze_UrlValid_Success
    @Test
    public void Analyze_UrlValid_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/analyze")
            .param("imageUrlString", "https://upload.wikimedia.org/wikipedia/commons/3/32/Googleplex_HQ_%28cropped%29.jpg")
            .param("maxPattern", "10")
            .param("minConfidence", "90")
        ).andReturn();
        assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());
        SuccessResponse<AwsReckognitionResult> response = mapper.readValue(result.getResponse().getContentAsByteArray(),
                SuccessResponse.class);
        response.getData();
    }
}