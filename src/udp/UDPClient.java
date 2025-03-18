package udp;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPClient {
    private final DatagramSocket socket;
    private final InetAddress address;
    private final Integer port;

    public UDPClient(Integer port) throws UnknownHostException, SocketException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");

        this.port = port;
    }

    public void transmitEquipmentCode(String code) {
        try {
            byte[] buf = code.getBytes(StandardCharsets.UTF_8);

            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
        } catch (IOException e) {
            System.out.printf("Failed to transmit equipment code %s.\n\n%s", code, e);
        }
    }

    public void close() {
        socket.close();
    }
}
