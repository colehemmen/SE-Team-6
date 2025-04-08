package screens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javazoom.jl.player.Player;
import java.io.FileInputStream;

public class Countdown {

    static JPanel panel;

    public static JPanel init() {
        panel = new JPanel(new GridLayout());
        panel.setPreferredSize(new Dimension(600, 300));
        panel.setBackground(Color.BLACK);

        paintImage(30);

        return panel;
    }

    public static void run(CardLayout cardLayout, JPanel cardPanel) {
        Timer timer = new Timer(1000, new ActionListener() {
            private int timeLeft = 29;
            int trackNumber = (int) (Math.random() * 8 + 1); // Chooses random music track between Track01 and Track08

            @Override
            public void actionPerformed(ActionEvent evt) {
                // Play selected soundtrack at specific time to sync with the countdown
                if (timeLeft == 18) {
                    startSoundtrack(trackNumber);
                }
                if (timeLeft >= 0) {
                    paintImage(timeLeft);

                    timeLeft--;
                } else {
                    ((Timer) evt.getSource()).stop();

                    cardLayout.show(cardPanel, "game-action");
                }
            }
        });

        timer.setRepeats(true);
        timer.start();
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
                    Player playMP3 = new Player(track);
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
        ImageIcon countdownImage = new ImageIcon(Objects.requireNonNull(Countdown.class.getClassLoader().getResource(String.format("images/countdown/%s.png", id))));
        JLabel label = new JLabel(countdownImage, SwingConstants.CENTER);

        panel.setSize(countdownImage.getIconWidth(), countdownImage.getIconHeight());
        panel.removeAll();
        panel.add(label);
        panel.revalidate();
        panel.repaint();
    }
}
