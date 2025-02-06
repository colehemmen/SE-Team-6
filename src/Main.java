import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import udp.UDPClient;
import udp.UDPServer;

import java.io.IOException;

import static org.junit.Assert.*;

public class Main {
    UDPClient client;
    UDPServer server;

    @Before
    public void setup() throws IOException {
        Integer port = 1234;

        server = new UDPServer(port);
        server.start();

        client = new UDPClient(port);
    }

    @Test
    public void testTrasnmitEquipmentCodeEchosCode() throws IOException {
        String code = client.transmitEquipmentCode("123456");
        assertEquals("123456", code);
        code = client.transmitEquipmentCode("123");
        assertNotEquals("hello server", code);
    }

    @After
    public void tearDown() throws IOException {
        client.transmitEquipmentCode("bye");
        client.close();
    }
}