package utils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Erik Mattfolk on 2019-02-27
 *
 * This class contains classes necessary to convert
 * input data from the user into integer values.
 */
public class InputTriple {
    public final JLabel label;
    public final JTextField textField;
    private final int minValue;
    private int value;

    /**
     * @param labelText
     * The text that describes the input
     * @param defaultValue
     * Default string in the textField
     * @param preferredSize
     * The size of both the label and the textField
     * @param minValue
     * The minimum value allowed for input
     */
    public InputTriple(String labelText, String defaultValue, Dimension preferredSize, int minValue) {
        label = new JLabel(labelText);
        textField = new JTextField(defaultValue);
        value = 0;
        this.minValue = minValue;

        label.setPreferredSize(preferredSize);
        textField.setPreferredSize(preferredSize);
    }

    /**
     * @throws NumberFormatException
     * Throws if there is a non numeric value or a value below the minValue
     *
     * Attempt to set value to the integer value of the String in textField
     */
    public void extractValue() throws NumberFormatException {
        value = Integer.parseInt(textField.getText());
        if (value < minValue) {
            throw new NumberFormatException("Invalid input");
        }
    }

    public int getValue() {
        return value;
    }
}
