package windows;

import utils.Setting;
import utils.InputTriple;
import utils.WindowUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Erik Mattfolk on 2017-04-28.
 * Refactored on 2019-02-27
 *
 * This is a prompt window that allows the user to input settings.
 * It is shown at the start of the game.
 */
public class SettingsFrame extends JFrame {

    private static final Dimension LABEL_SIZE = new Dimension(100, 20);
    private static final String DEFAULT_WIDTH = "50";
    private static final String DEFAULT_HEIGHT = "50";
    private static final String DEFAULT_TILE_SIZE = "15";
    private static final String DEFAULT_GRID_WIDTH = "0";

    private JPanel left;
    private JPanel right;
    private JPanel bottomLeft;
    private JPanel bottomRight;
    private InputTriple[] triples;
    private boolean done;

    public SettingsFrame() {
        done = false;
        setLayout(new BorderLayout());

        setupPanels();
        setupInputs();
        setupButton();

        pack();
        setResizable(false);
        setTitle("Game of Life");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(WindowUtils.getCenteredBound(getPreferredSize()));
        setVisible(true);
    }

    /**
     * Set up panels in the following configuration:
     *
     * Window
     * |---------------------------------------|
     * |            |              |           |
     * | Left panel | Right  panel |           |
     * |            |              |           |
     * |------------|--------------|   East    |
     * |            |              |           |
     * |   Bottom   |    Bottom    |           |
     * | Left panel | Right  panel |           |
     * |            |              |           |
     * |---------------------------------------|
     */
    private void setupPanels() {
        left = new JPanel();
        right = new JPanel();
        bottomLeft = new JPanel();
        bottomRight = new JPanel();
        left.setLayout(new BorderLayout());
        right.setLayout(new BorderLayout());
        bottomLeft.setLayout(new BorderLayout());
        bottomRight.setLayout(new BorderLayout());
        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
        left.add(bottomLeft, BorderLayout.SOUTH);
        right.add(bottomRight, BorderLayout.SOUTH);
    }

    /**
     * Put four input fields onto the window.
     * The inputs are placed on the left hand side of the window from the top down.
     */
    private void setupInputs() {
        triples = new InputTriple[] {
                new InputTriple(" Width", DEFAULT_WIDTH, LABEL_SIZE, 1),
                new InputTriple(" Height", DEFAULT_HEIGHT, LABEL_SIZE, 1),
                new InputTriple(" Tile Size", DEFAULT_TILE_SIZE, LABEL_SIZE, 1),
                new InputTriple(" Grid Width", DEFAULT_GRID_WIDTH, LABEL_SIZE, 0)
        };

        left.add(triples[0].label, BorderLayout.NORTH);
        left.add(triples[1].label, BorderLayout.CENTER);
        bottomLeft.add(triples[2].label, BorderLayout.NORTH);
        bottomLeft.add(triples[3].label, BorderLayout.SOUTH);

        right.add(triples[0].textField, BorderLayout.NORTH);
        right.add(triples[1].textField, BorderLayout.CENTER);
        bottomRight.add(triples[2].textField, BorderLayout.NORTH);
        bottomRight.add(triples[3].textField, BorderLayout.SOUTH);
    }

    /**
     * Place a button on the right hand side of the window.
     * Then add a ActionListener to the button which checks if all inputs are correct.
     */
    private void setupButton() {
        JButton startButton = new JButton("Start");
        add(startButton, BorderLayout.EAST);

        startButton.addActionListener(e -> {
            boolean hasAllInputs = true;
            for (InputTriple triple : triples) {
                try {
                    triple.extractValue();
                } catch (NumberFormatException ignored) {
                    triple.textField.setText("error");
                    hasAllInputs = false;
                }
            }
            done = hasAllInputs;
        });
    }

    public boolean isDone() {
        return done;
    }

    public Setting getSetting() {
        return new Setting(
                triples[0].getValue(),
                triples[1].getValue(),
                triples[2].getValue(),
                triples[3].getValue()
        );
    }
}
