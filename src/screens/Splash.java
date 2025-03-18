package screens;

import database.DatabaseConnection;
import udp.UDPClient;

import javax.swing.*;
import java.util.Objects;

public class Splash {

    private static PlayerEntry playerEntryScreen;

    public Splash(DatabaseConnection databaseConnection, UDPClient udpClient, JTextField[][] textFields) {
        playerEntryScreen = new PlayerEntry(databaseConnection, udpClient, textFields);
    }

    public static void run() {
        JFrame splashFrame = new JFrame("Splash Screen");
        splashFrame.setUndecorated(true);
        splashFrame.setSize(900, 500);
        splashFrame.setLocationRelativeTo(null);

        ImageIcon photon = new ImageIcon(Objects.requireNonNull(Splash.class.getResource("/images/splash.png")));
        JLabel splashLabel = new JLabel(photon, SwingConstants.CENTER);
        splashFrame.add(splashLabel);

        splashFrame.setVisible(true);

        Timer timer = new Timer(3000, e -> {
            splashFrame.dispose();
            playerEntryScreen.run();
        });
        timer.setRepeats(false);
        timer.start();
    }
}
