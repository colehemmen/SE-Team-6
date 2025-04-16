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
        address = InetAddress.getByName("127.0.0.1");

        this.port = port;
    }

    public void trasmitMessage(String message) {
        try {
            byte[] buf = message.getBytes(StandardCharsets.UTF_8);

            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
        } catch (IOException e) {
            System.out.printf("Failed to transmit message %s.\n\n%s", message, e);
        }
    }

    public void transmitEquipmentCode(String equipmentId) {
        try {
            byte[] buf = equipmentId.getBytes(StandardCharsets.UTF_8);

            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
        } catch (IOException e) {
            System.out.printf("Failed to transmit equipment code %s.\n\n%s", equipmentId, e);
        }
    }

    public void transitStatusCode(int statusCode) {
        try {
            String payload = String.valueOf(statusCode);

            byte[] buf = payload.getBytes(StandardCharsets.UTF_8);

            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
        } catch (IOException e) {
            System.out.printf("Failed to transmit status code %s.\n\n%s", statusCode, e);
        }
    }

    public void close() {
        socket.close();
    }
}
