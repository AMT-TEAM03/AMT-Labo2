package LabelDetector;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

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
        mockMvc.perform(get("/v1/analyze").param("imageUrlString", imageUrl))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"time\":")))
                .andExpect(content().string(containsString("\"name\":")))
                .andExpect(content().string(containsString("\"confidence\":")));
    }


    @Test
    public void shouldNotReturnListTimeAndPatterns() throws Exception {
        mockMvc.perform(get("/v1/analyze").param("imageUrlString", "https://www.google.ch"))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Reckognition has encountered an issue")));
    }
}
