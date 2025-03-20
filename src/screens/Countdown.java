package screens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

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

            @Override
            public void actionPerformed(ActionEvent evt) {
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

    private static void paintImage(Integer id) {
        ImageIcon countdownImage = new ImageIcon(Objects.requireNonNull(Countdown.class.getResource(String.format("/images/countdown/%s.png", id))));
        JLabel label = new JLabel(countdownImage, SwingConstants.CENTER);

        panel.setSize(countdownImage.getIconWidth(), countdownImage.getIconHeight());
        panel.removeAll();
        panel.add(label);
        panel.revalidate();
        panel.repaint();
    }
}
