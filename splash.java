import javax.swing.*;

public class splash {
    public static void main(String[] args) {
        // creates a splash screen frame
        JFrame splashFrame = new JFrame("Splash Screen");
        splashFrame.setUndecorated(true);
        splashFrame.setSize(900, 500);
        splashFrame.setLocationRelativeTo(null);

        // adds a label with the photon image
        ImageIcon photon = new ImageIcon("images/photon.png");
        JLabel splashLabel = new JLabel(photon, SwingConstants.CENTER);
        splashFrame.add(splashLabel);

        // display the splash screen
        splashFrame.setVisible(true);

        // display for 3 seconds
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }

        // close the splash screen
        splashFrame.dispose();

        // launch main
        JFrame mainFrame = new JFrame("Main Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(900, 600);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
}
