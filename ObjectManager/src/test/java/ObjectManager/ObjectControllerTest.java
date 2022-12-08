package ObjectManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Base64;

@SpringBootTest
@AutoConfigureMockMvc
class ObjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    static String _base64Img;
    static final String[] OBJECT_KEY_LIST = {
        "testing123.png",
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

    @Test
    public void shouldReturnListIObjects() throws Exception {
        mockMvc.perform(get("/v1/objects")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString(OBJECT_KEY_LIST[0])));
    }

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