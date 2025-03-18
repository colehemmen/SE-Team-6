package screens;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Countdown {

    public static void run(JFrame frame, JTextField[][] textFields) {
        Timer countdownTimer = new Timer(1000, new ActionListener() {
            private int timeLeft = 30;

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (timeLeft > 0) {
                    System.out.println("Starting game action display in " + timeLeft + " seconds...");
                    timeLeft--;
                } else {
                    ((Timer) evt.getSource()).stop();
                    frame.dispose();
                    new GameAction(textFields);
                }
            }
        });

        countdownTimer.start();
        JOptionPane.showMessageDialog(frame, "Game action display will start in 30 seconds!",
                "Countdown Timer", JOptionPane.INFORMATION_MESSAGE);
    }
}
