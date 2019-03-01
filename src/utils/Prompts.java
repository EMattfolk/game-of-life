package utils;

import javax.swing.*;

/**
 * Utility class for showing prompts to the user
 */
public final class Prompts {

    /**
     * @param exceptionString Usually a string representation of the error
     * @return {0, 1} if the user pressed {yes, no} respectively
     */
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

    /**
     * @param noticeString A String with information why the notice was shown
     * @param message A message to the user
     *
     * Show a simple window with a message and a "ok" button
     */
    public static void showNotice(String noticeString, String message) {
        JOptionPane.showMessageDialog(null, noticeString + "\n" + message);
    }
}
