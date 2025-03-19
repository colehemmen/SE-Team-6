import database.DatabaseConnection;
import screens.PlayerEntry;
import screens.Splash;
import udp.UDPClient;

import java.awt.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.*;

@SuppressWarnings("InstantiationOfUtilityClass")
public class Main {

    private static JFrame frame;
    private static final JTextField[][] textFields = new JTextField[15][2];

    public static void main(String[] args) throws SocketException, UnknownHostException {
        initializeFrame();
        initailizeScreens();

        buildAndShowInitialScreens();

        startGame();
    }

    private static void startGame() {
        // Start the initial frame with the splash screen
        SwingUtilities.invokeLater(Main::buildAndShowInitialScreens);
    }

    private static void buildAndShowInitialScreens() {
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);

        // Make a wrapper so we can set the dimensions of the screen
        JPanel playerEntryWrapper = new JPanel();
        playerEntryWrapper.setPreferredSize(new Dimension(600, 800));
        playerEntryWrapper.setBackground(Color.WHITE);

        frame.add(cardPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        JPanel splashPanel = Splash.run();
        JPanel playerEntryPanel = PlayerEntry.run(cardLayout, cardPanel);

        playerEntryWrapper.add(playerEntryPanel, BorderLayout.CENTER);

        cardPanel.add(splashPanel, "splash");
        cardPanel.add(playerEntryWrapper, "player-entry");

        cardLayout.show(cardPanel, "splash");

        new Timer(3000, e -> cardLayout.show(cardPanel,"player-entry")).start();
    }

    private static void initailizeScreens() throws SocketException, UnknownHostException {
        //UDPServer udpServer = new UDPServer(7501); // TODO: uncomment this when we know what to do w/ the server next sprint
        //udpServer.start();
        DatabaseConnection database = new DatabaseConnection();
        UDPClient udpClient = new UDPClient(7500);

        new PlayerEntry(database, udpClient, textFields);
    }

    private static JFrame initializeFrame() {
        frame = new JFrame("Photon Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(true);

        return frame;
    }
}