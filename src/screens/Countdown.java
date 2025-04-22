package screens;

import udp.UDPClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javazoom.jl.player.Player;
import java.io.FileInputStream;

public class Countdown {

    static UDPClient udpClient;

    static JPanel panel;

    static Player playMP3;

    public Countdown(UDPClient udp) {
        udpClient = udp;
    }

    public static JPanel init() {
        panel = new JPanel(new GridLayout());
        panel.setPreferredSize(new Dimension(1200, 600));
        panel.setBackground(Color.BLACK);

        return panel;
    }

    public static void run(CardLayout cardLayout, JPanel cardPanel) {
        Action countdown = new AbstractAction() {
            int timeLeft = 30;
            final int trackNumber = (int) (Math.random() * 8 + 1); // Chooses random music track between Track01 and Track08

            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeLeft == 18) {
                    startSoundtrack(trackNumber);
                }
                if (timeLeft >= 0) {
                    paintImage(timeLeft);

                    timeLeft--;
                } else {
                    ((Timer) e.getSource()).stop();

                    udpClient.transitStatusCode(202);
                    GameAction.run(playMP3);

                    timeLeft = 30; // reset if they come back to screen
                    cardLayout.show(cardPanel, "game-action");
                }
            }
        };

        Timer timer = new Timer(1000, countdown);
        timer.start();

        countdown.actionPerformed(null);
    }

    private static void startSoundtrack(int trackNumber) {
        // Method for playing soundtrack based on the provided track number
        String filePath = "src/audio/Track0" + trackNumber + ".mp3";
        new Thread(() -> {
            try {
                // Sync tracks to say "begin" when countdown hits 0
                if (trackNumber == 1)
                    Thread.sleep(1633);
                else if (trackNumber == 2)
                    Thread.sleep(167);
                else if (trackNumber == 3)
                    Thread.sleep(1167);
                else if (trackNumber == 4 || trackNumber == 5)
                    Thread.sleep(933);
                else if (trackNumber == 6)
                    Thread.sleep(1767);
                else if (trackNumber == 7)
                    Thread.sleep(700);
                else if (trackNumber == 8)
                    Thread.sleep(500);
                else
                    System.out.println("Invalid Track Number");

                try (FileInputStream track = new FileInputStream(filePath)) {
                    playMP3 = new Player(track);
                    playMP3.play();
                }

            } catch (InterruptedException e) {
                System.err.println("Soundtrack playback was interrupted.");
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("An error occurred while playing the soundtrack.");
                e.printStackTrace();
            }
        }).start();
    }

    private static void paintImage(Integer id) {
        ImageIcon ogBackgroundImage = new ImageIcon(Objects.requireNonNull(
                Countdown.class.getClassLoader().getResource("images/countdown/background.png"))
        );
        ImageIcon ogCountdownImage = new ImageIcon(Objects.requireNonNull(
                Countdown.class.getClassLoader().getResource(String.format("images/countdown/%s.png", id)))
        );

        // calculate scaling for images
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        double scaleFactor = Math.max(screenWidth / 1920.0, screenHeight / 1080.0);

        int scaledBackgroundWidth = (int) (636 * scaleFactor);
        int scaledBackgroundHeight = (int) (483 * scaleFactor);

        int scaledCountdownWidth = (int) (264 * scaleFactor) + 25;
        int scaledCountdownHeight = (int) (121 * scaleFactor);

        ImageIcon backgroundImage = new ImageIcon(
                ogBackgroundImage.getImage().getScaledInstance(scaledBackgroundWidth, scaledBackgroundHeight, Image.SCALE_SMOOTH)
        );
        ImageIcon countdownImage = new ImageIcon(
                ogCountdownImage.getImage().getScaledInstance(scaledCountdownWidth, scaledCountdownHeight, Image.SCALE_SMOOTH)
        );

        JLayeredPane layeredPane = getLayeredPane(backgroundImage, countdownImage);

        panel.removeAll();
        panel.setLayout(new BorderLayout());
        panel.add(layeredPane, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private static JLayeredPane getLayeredPane(ImageIcon backgroundImage, ImageIcon countdownImage) {
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(0, 0, panel.getWidth(), panel.getHeight());

        JLabel countdownLabel = new JLabel(countdownImage);
        int cw = countdownImage.getIconWidth();
        int ch = countdownImage.getIconHeight();

        countdownLabel.setBounds(589, 420, cw, ch);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(panel.getSize());

        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(countdownLabel, JLayeredPane.PALETTE_LAYER);
        return layeredPane;
    }
}
