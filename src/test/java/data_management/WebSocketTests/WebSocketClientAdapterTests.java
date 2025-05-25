package data_management.WebSocketTests;
import com.data_management.DataStorage;
import com.data_management.WebSocketClientAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

public class WebSocketClientAdapterTests {

    private DataStorage storage;
    private WebSocketClientAdapter adapter ;

    @BeforeEach

    public void setUp() throws Exception {
        this.storage = DataStorage.getInstance();
        adapter = new WebSocketClientAdapter(new URI("ws://localhost:8080"),storage);
    }


    /**
     * Makes sure that when we pass a correct format the code starts running
     */
    @Test
    public void testValidMessage() {
        String message = "3,"+ System.currentTimeMillis()+", Saturation, 76";
        assertDoesNotThrow(() -> adapter.onMessage(message));
    }


    /**
     * Test code for an invalid input such that the system doesn't crash
     */
    @Test
    public void testInvalidMessageFormat() {
        String message = "12, 5767, TESTING , invalid Number";
        assertDoesNotThrow(() -> adapter.onMessage(message));
    }


    @Test
    public void testOnCloseLogsGracefully() {
        int[] closeCodes = {1000, 1001, 1006, 1011}; // Normal, Going away, Abnormal, Internal error
        for (int code : closeCodes) {
            assertDoesNotThrow(() -> adapter.onClose(code, "Simulated close", false));
        }
    }

    @Test
    public void testOnErrorDoesNotCrash() {
        assertDoesNotThrow(() -> adapter.onError(new RuntimeException("Error")));
    }

    @Test
    public void testMalformedMessageWithMissingFields() {
        String message = "10,EmergencyButton,0.44";  // Only 3 parts

        assertDoesNotThrow(() -> adapter.onMessage(message));
    }
}
