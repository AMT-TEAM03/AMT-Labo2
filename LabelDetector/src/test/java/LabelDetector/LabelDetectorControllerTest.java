package LabelDetector;


import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;
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

    static String imageUrl;

    @BeforeTestClass
    void beforeAll() throws Exception {
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/3/32/Googleplex_HQ_%28cropped%29.jpg";
    }

    @Test
    public void shouldReturnListTimeAndPatterns() throws Exception {
        mockMvc.perform(get("/v1/execute").param("imageUrlString", imageUrl))
                .andExpect(status().isOk());
    }


    @Test
    public void shouldNotReturnListTimeAndPatterns() throws Exception {
        mockMvc.perform(get("/v1/execute").param("imageUrlString", "https://www.google.ch"))
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
