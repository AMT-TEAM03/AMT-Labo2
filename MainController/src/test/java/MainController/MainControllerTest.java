package MainController;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

class MainControllerTest {   

    // Nothing exists
    @Test
    public void Run_All_Scenarios() throws IOException{
        String[] dummyArgs = {};
        final InputStream in = System.in;
        Main.startIntegrationTests(in, dummyArgs);
    }
}