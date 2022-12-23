package LabelDetector;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.ObjectMapper;

import LabelDetector.controller.request.LabelRequest;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LabelDetectorControllerTests {
    @Autowired
    private MockMvc mockMvc;

    static String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/3/32/Googleplex_HQ_%28cropped%29.jpg";

    @Test
    public void shouldReturnListTimeAndPatterns() throws Exception {
        LabelRequest labelRequest = new LabelRequest()
                .setImageUrl(imageUrl);
        mockMvc.perform(get("/v1/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(labelRequest))
                ).andExpect(status().isOk())
                .andExpect(content().string(containsString("\"time\":")))
                .andExpect(content().string(containsString("\"name\":")))
                .andExpect(content().string(containsString("\"confidence\":")));
    }


    @Test
    public void shouldNotReturnListTimeAndPatterns() throws Exception {
        LabelRequest labelRequest = new LabelRequest()
                .setImageUrl("https://www.google.ch");
        mockMvc.perform(get("/v1/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(labelRequest))
                ).andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Reckognition has encountered an issue")));
    }

    private static String asJsonString(final LabelRequest obj) {
        try {
            return new ObjectMapper().setBase64Variant(Base64Variants.getDefaultVariant()).writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
