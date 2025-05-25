package data_management.WebSocketTests;
import com.cardio_generator.outputs.FileOutputStrategy;
import com.cardio_generator.outputs.TcpOutputStrategy;
import com.cardio_generator.outputs.WebSocketOutputStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class WebSocketOutput {

    /**
     * Simple test of whether the code runs
     * very basic coverage
     */
    @Test
    public void WebSocketOutputStrategy() {
        WebSocketOutputStrategy webSocketOutputStrategy = new WebSocketOutputStrategy(3);

        assertDoesNotThrow(() ->webSocketOutputStrategy.output(2, 1200, "TEST", "Data"));

    }

    @Test
    public void TCPPortOutputStrategy() {
        TcpOutputStrategy tcpOutputStrategy = new TcpOutputStrategy(4);
        assertDoesNotThrow(() -> tcpOutputStrategy.output(2, 1200, "TEST", "Data"));

    }

    @Test
    public void FileOutputStrategy() {
    FileOutputStrategy fileOutputStrategy = new FileOutputStrategy("C:\\Users\\marco\\Documents\\period 4 to 6\\Software Engineering\\Project 1 - Assignement\\signal_project\\output");
    assertDoesNotThrow(() ->fileOutputStrategy.output(2,1200,"TEST" , "DATA"));

    }
}
