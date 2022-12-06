package LabelDetector;


import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Base64;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LabelDetectorControllerTests {
    @Autowired
    private MockMvc mockMvc;

    static String _base64Img;

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
    }

    @Test
    public void shouldReturnListTimeAndPatterns() throws Exception {
        mockMvc.perform(get("/v1/execute").param("imgBase64", _base64Img))
                .andExpect(status().isOk());
    }


    @Test
    public void shouldNotReturnListTimeAndPatterns() throws Exception {
        mockMvc.perform(get("/v1/execute").param("imgBase64", "NotAnImage"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("\"error\":\"Label detection failed\"")));
    }

    @Test
    public void shouldReturnConfidence() throws Exception{
        mockMvc.perform(get("/v1/confidence"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("90")));
    }

    @Test
    public void shouldReturnMaxPattern() throws Exception{
        mockMvc.perform(get("/v1/max_pattern"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("10")));
    }
}
