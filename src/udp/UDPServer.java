package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class UDPServer extends Thread {
    private final DatagramSocket socket;
    private final byte[] buf = new byte[256];

    public UDPServer(Integer port)  throws SocketException {
        socket = new DatagramSocket(port);
    }

    public void run() {
        boolean running = true;

        while(running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            InetAddress address = packet.getAddress();
            int clientPort = packet.getPort();

            String received = new String(packet.getData(), 0, packet.getLength());
            if (received.equals("bye")) {
                running = false;
            }

            // TODO: Figure out what we need to do here
            byte[] responseBuf = received.getBytes(StandardCharsets.UTF_8);
            DatagramPacket response = new DatagramPacket(responseBuf, responseBuf.length, address, clientPort);

            try {
                socket.send(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        socket.close();
    }
}
