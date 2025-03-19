package screens;

import database.DatabaseConnection;
import udp.UDPClient;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Splash {
    public static JPanel run() {
        JPanel panel = new JPanel(new GridLayout());
        panel.setPreferredSize(new Dimension(600, 300));
        panel.setBackground(Color.BLACK);

        ImageIcon photon = new ImageIcon(Objects.requireNonNull(Splash.class.getResource("/images/splash.png")));
        JLabel splashLabel = new JLabel(photon, SwingConstants.CENTER);

        panel.setSize(photon.getIconWidth(), photon.getIconHeight());
        panel.add(splashLabel);

        return panel;
    }
}
