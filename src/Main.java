import database.DatabaseConnection;
import screens.Countdown;
import screens.GameAction;
import screens.PlayerEntry;
import screens.Splash;
import udp.UDPClient;
import udp.UDPServer;

import java.awt.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.function.Consumer;
import javax.swing.*;

@SuppressWarnings("InstantiationOfUtilityClass")
public class Main {

    private static JFrame frame;
    private static final CardLayout cardLayout = new CardLayout();
    private static final JPanel cardPanel = new JPanel(cardLayout);
    private static final JTextField[][] textFields = new JTextField[15][2];

    public static void main(String[] args) throws SocketException, UnknownHostException {
        initializeFrame();
        initializeScreens();

        buildAndShowInitialScreens();

        startGame();
    }

    private static void startGame() {
        // Start the initial frame with the splash screen
        SwingUtilities.invokeLater(Main::buildAndShowInitialScreens);
    }

    private static void buildAndShowInitialScreens() {

        // Make a wrapper so we can set the dimensions of the screen
        JPanel playerEntryWrapper = new JPanel();
        playerEntryWrapper.setPreferredSize(new Dimension(600, 800));
        playerEntryWrapper.setBackground(Color.WHITE);

        frame.add(cardPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        JPanel splashPanel = Splash.run();
        JPanel playerEntryPanel = PlayerEntry.init(cardLayout, cardPanel);
        JPanel gameActionPanel = GameAction.getMainPanel();
        JPanel countdownPanel = Countdown.init();

        playerEntryWrapper.add(playerEntryPanel, BorderLayout.CENTER);

        cardPanel.add(splashPanel, "splash");
        cardPanel.add(playerEntryWrapper, "player-entry");
        cardPanel.add(gameActionPanel, "game-action");
        cardPanel.add(countdownPanel, "countdown");

        //Countdown.run(textFields);
        cardLayout.show(cardPanel, "splash");

        new Timer(3000, e -> {
            ((Timer) e.getSource()).stop();

            cardLayout.show(cardPanel, "player-entry");
        }).start();
    }

    private static void initializeScreens() throws SocketException, UnknownHostException {
        Consumer<String> handler = GameAction::processEvent;

        UDPClient udpClient = new UDPClient(7500);
        UDPServer udpServer = new UDPServer(7501, udpClient, handler);
        DatabaseConnection database = new DatabaseConnection();

        udpServer.start();

        new PlayerEntry(database, udpClient, textFields);
        new GameAction(textFields, database);
        new Countdown(udpClient);
    }

    private static void initializeFrame() {
        frame = new JFrame("Photon Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
    }

}
