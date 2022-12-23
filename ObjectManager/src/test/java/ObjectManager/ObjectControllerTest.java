package ObjectManager;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.ObjectMapper;

import ObjectManager.controller.request.ObjectRequest;
import ObjectManager.utils.SuccessResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(value = Lifecycle.PER_CLASS)
class ObjectControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    static byte[] _byteImg;
    static final String[] OBJECT_KEY_LIST = {
        "testing123.png",
        "testing1234.png"
    };

    private void cleanup() throws Exception {
        for (String i : OBJECT_KEY_LIST) {
            ObjectRequest objectRequest = new ObjectRequest()
                    .setName(i);
            mockMvc.perform(delete("/v1/object")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(objectRequest))
            ).andExpect(status().isOk());
        }
    }

    @BeforeAll
    void beforeAll() throws Exception {
        // read image from file
        File fileImg = new File(this.getClass().getClassLoader().getResource("test.jpg").getFile());
        assertTrue(fileImg.exists());
        _byteImg = new byte[(int) fileImg.length()];

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileImg);
            // read file into bytes[]
            fis.read(_byteImg);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        assert(_byteImg != null);
        // cleanup of possible old test DataObject
        cleanup();
    }

    @BeforeEach
    void beforeEach() throws Exception {
        assert(_byteImg != null);
        ObjectRequest objectRequest = new ObjectRequest()
                .setImage(_byteImg)
                .setName(OBJECT_KEY_LIST[0]);
        mockMvc.perform(post("/v1/object")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(objectRequest))
        ).andExpect(status().isOk());
    }

    @AfterEach
    void afterEach() throws Exception {
        cleanup();
    }

    @Test
    public void shouldReturnListIObjects() throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/objects")).andReturn();
        assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());
        SuccessResponse<List<String>> response = mapper.readValue(result.getResponse().getContentAsByteArray(),
                SuccessResponse.class);
        assertTrue(response.getData().contains(OBJECT_KEY_LIST[0]));
    }

    @Test
    public void DoesObjectExist_RootObjectExists_Exists() throws Exception{
        MvcResult result = mockMvc.perform(get("/v1/object/exists?name=" + OBJECT_KEY_LIST[0])).andReturn();
        assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());
        SuccessResponse<Boolean> response = mapper.readValue(result.getResponse().getContentAsByteArray(), 
                SuccessResponse.class);
        assertTrue(response.getData());
    }

    @Test
    public void DoesObjectExist_RootObjectAndObjectExist_Exists() throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/object/exists?name=" + OBJECT_KEY_LIST[0])).andReturn();
        assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());
        SuccessResponse<Boolean> response = mapper.readValue(result.getResponse().getContentAsByteArray(),
                SuccessResponse.class);
        assertTrue(response.getData());
    }

    private static String asJsonString(final ObjectRequest obj) {
        try {
            return new ObjectMapper().setBase64Variant(Base64Variants.getDefaultVariant()).writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}