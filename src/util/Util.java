package util;

import javax.swing.*;

public final class Util {
    public static boolean isValidInput(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static void writeToScreen(JTextField[][] textFields, String codename) {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 2; j++) {
                if (textFields[i][j].getText().isEmpty()) {
                    textFields[i][j].setText(codename);
                    return;
                }
            }
        }
    }

    public static void clearTextFields(JTextField[][] textFields) {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 2; j++) {
                textFields[i][j].setText("");
            }
        }
    }
}
