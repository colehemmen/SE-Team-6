import database.DatabaseConnection;
import screens.Splash;
import udp.UDPClient;

import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.*;

@SuppressWarnings("InstantiationOfUtilityClass")
public class Main {

    private static final JTextField[][] textFields = new JTextField[15][2];

    public static void main(String[] args) throws SocketException, UnknownHostException {
        //UDPServer udpServer = new UDPServer(7501); // TODO: uncomment this when we know what to do w/ the server next sprint
        //udpServer.start();
        DatabaseConnection database = new DatabaseConnection();
        UDPClient udpClient = new UDPClient(7500);

        SwingUtilities.invokeLater(() -> {
            new Splash(database, udpClient, textFields);
            Splash.run();
        });
    }
}