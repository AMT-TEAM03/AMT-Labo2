package ObjectManager;

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

import ObjectManager.CloudProvider.AWS.AwsDataObjectHelper;
import ObjectManager.utils.SuccessResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class ObjectControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();
    static AwsDataObjectHelper _awsClient;

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
        _awsClient = new AwsDataObjectHelper();
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

    @Test
    public void shouldReturnListIObjects() throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/objects")).andReturn();
        assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());
        SuccessResponse<List<String>> response = mapper.readValue(result.getResponse().getContentAsByteArray(),
                SuccessResponse.class);
        assertTrue(response.getData().contains(OBJECT_KEY_LIST[0]));
    }

    // DoesObjectExist_RootObjectExists_Exists
    @Test
    public void DoesObjectExist_RootObjectExists_Exists() throws Exception{
        MvcResult result = mockMvc.perform(get("/v1/object/exists?name=" + OBJECT_KEY_LIST[0])).andReturn();
        assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());
        SuccessResponse<Boolean> response = mapper.readValue(result.getResponse().getContentAsByteArray(), 
                SuccessResponse.class);
        assertTrue(response.getData());
    }

    // DoesObjectExist_RootObjectDoesntExist_DoesntExist
    // @Test
    // public void DoesObjectExists_RootObjectDoesntExists_DoesntExist(){
    //     _awsClient.DeleteBucket();
    //     MvcResult result = mockMvc.perform(get("/v1/object/exists?name=" + OBJECT_KEY_LIST[0])).andReturn();
    //     assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());
    //     SuccessResponse<Boolean> response = mapper.readValue(result.getResponse().getContentAsByteArray(),
    //             SuccessResponse.class);
    //     assertTrue(response.getData());
    // }
    // DoesObjectExist_RootObjectAndObjectExist_Exists
    @Test
    public void DoesObjectExist_RootObjectAndObjectExist_Exists() throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/object/exists?name=" + OBJECT_KEY_LIST[0])).andReturn();
        assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());
        SuccessResponse<Boolean> response = mapper.readValue(result.getResponse().getContentAsByteArray(),
                SuccessResponse.class);
        assertTrue(response.getData());
    }
    // DoesObjectExist_RootObjectExistObjectDoesntExist_DoesntExist
    // UploadObject_RootObjectExistsNewObject_Uploaded
    // UploadObject_RootObjectExistsObjectAlreadyExists_ThrowException
    // UploadObject_RootObjectDoesntExist_Uploaded
    // DownloadObject_ObjectExists_Downloaded
    // DownloadObject_ObjectDoesntExist_ThrowException
    // PublishObject_ObjectExists_Published
    // PublishObject_ObjectDoesntExist_ThrowException
    // RemoveObject_SingleObjectExists_Removed
    // RemoveObject_SingleObjectDoesntExist_ThrowException
    // RemoveObject_FolderObjectExistWithoutRecursiveOption_ThrowException
    // RemoveObject_FolderObjectExistWithRecursiveOption_Removed
    // RemoveObject_RootObjectNotEmptyWithoutRecursiveOption_ThrowException
    // RemoveObject_RootObjectNotEmptyWithRecursiveOption_Removed
    // RemoveObject_ObjectNotExists_ThrowException

    // @Test
    // public void shouldCreateObject() throws Exception {
    //     mockMvc.perform(post("/v1/object")).andDo(print()).andExpect(status().isOk());
    //             // .andExpect(content().string(containsString("Hello, World")));
    // }
    // @Test
    // public void shouldDeleteObject() throws Exception {
    //     mockMvc.perform(delete("/v1/object")).andDo(print()).andExpect(status().isOk());
    //             // .andExpect(content().string(containsString("Hello, World")));
    // }
}