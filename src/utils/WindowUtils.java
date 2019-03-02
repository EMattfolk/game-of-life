package utils;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class for showing prompts to the user
 */
public final class WindowUtils {

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

    /**
     * @param windowDimension The dimension of the window to be centered
     * @return Rectangle representing the bounds of the window if it was centered
     */
    public static Rectangle getCenteredBound(Dimension windowDimension) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - windowDimension.width) / 2;
        int y = (screenSize.height - windowDimension.height) / 2;
        return new Rectangle(x, y, windowDimension.width, windowDimension.height);
    }
}
