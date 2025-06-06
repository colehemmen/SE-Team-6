package udp;

import java.io.IOException;
import java.net.*;
import java.util.function.Consumer;

public class UDPServer extends Thread {
    private final DatagramSocket socket;
    private final Consumer<String> handler;
    private final UDPClient udpClient;

    private final byte[] buf = new byte[256];

    public UDPServer(Integer port, UDPClient udp, Consumer<String> consumer) throws SocketException, UnknownHostException {
        socket = new DatagramSocket(port, InetAddress.getByName("127.0.0.1"));
        handler = consumer;
        udpClient = udp;
    }

    @Override
    public void run() {
        boolean running = true;

        while(running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String received = new String(packet.getData(), 0, packet.getLength());
            handler.accept(received);

            udpClient.transmitMessage(String.format("Processed: %s", received));
        }

        socket.close();
    }
}
