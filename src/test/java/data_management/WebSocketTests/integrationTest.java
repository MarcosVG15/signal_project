package data_management.WebSocketTests;
import com.data_management.DataStorage;
import com.data_management.WebSocketClientAdapter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class integrationTest {

    private DataStorage storage;
    private WebSocketClientAdapter adapter ;

    @BeforeEach

    public void setUp() throws Exception {
        this.storage = DataStorage.getInstance();
        adapter = new WebSocketClientAdapter(new URI("ws://localhost:8080"),storage);
    }


    @Test
    public void test() throws Exception {
        adapter.startStreaming();
        Thread.sleep(30_000); // INCREASE TO DICTATE THE SIZE OF SAMPLES YOU WANT TO TEST

        boolean hasData = false;
        if (DataStorage.getInstance().getAllPatients() != null){
             hasData = true;
        }

        assertTrue(hasData, "At least one patient should have received records from WebSocket stream.");

        adapter.onClose(100 , "TEst", true );
    }
}