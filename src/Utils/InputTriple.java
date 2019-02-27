package Utils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Erik Mattfolk on 2019-02-27
 *
 * This class contains classes necessary to convert input data
 * from the user into integer values.
 */
public class InputTriple {
    public final JLabel label;
    public final JTextField textField;
    public int value, minValue;

    public InputTriple(String labelText, String defaultValue, Dimension preferredSize, int minValue) {
        label = new JLabel(labelText);
        textField = new JTextField(defaultValue);
        value = 0;
        this.minValue = minValue;

        label.setPreferredSize(preferredSize);
        textField.setPreferredSize(preferredSize);
    }

    public void extractValue() {
        value = Integer.parseInt(textField.getText());
    }

    public boolean hasValidValue() {
        return value >= minValue;
    }
}
