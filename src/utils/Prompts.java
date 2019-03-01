package utils;

import javax.swing.*;

public final class Prompts {

    public static int showErrorDialog(String exceptionString) {
        return JOptionPane.showOptionDialog(
            null,
            exceptionString + "\nTry Again?",
            "Error",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            new Object[] { "yes", "no" },
            "yes"
        );
    }

    public static void showNotice(String noticeString, String message) {
        JOptionPane.showMessageDialog(null, noticeString + "\n" + message);
    }
}
