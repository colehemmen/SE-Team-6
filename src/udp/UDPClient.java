package udp;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPClient {
    private final DatagramSocket socket;
    private final InetAddress address;
    private final Integer port;

    private byte[] buf = new byte[256];

    public UDPClient(Integer port) throws UnknownHostException, SocketException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");

        this.port = port;
    }

    public String transmitEquipmentCode(String code) throws IOException {
        buf = code.getBytes(StandardCharsets.UTF_8);

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);

        // TODO: Figure out what we need to do here
        packet = new DatagramPacket(buf,buf.length);
        socket.receive(packet);

        return new String(packet.getData(), 0, packet.getLength());
    }

    public void close() {
        socket.close();
    }
}
