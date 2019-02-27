package Windows;

import Utils.Setting;
import Utils.InputTriple;
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
    private static final String DEFAULT_GRID_WIDTH = "2";

    private JPanel left, right, bottomLeft, bottomRight;
    private InputTriple[] triples;
    private boolean done;

    public SettingsFrame() {
        done = false;
        setLayout(new BorderLayout());
        setBounds(100, 100, 0, 0);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Game of Life");
        setResizable(false);

        setupPanels();
        setupInputs();
        setupButton();

        pack();
        setVisible(true);
    }

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

    private void setupInputs() {
        triples = new InputTriple[] {
                new InputTriple(" Width", DEFAULT_WIDTH, LABEL_SIZE),
                new InputTriple(" Height", DEFAULT_HEIGHT, LABEL_SIZE),
                new InputTriple(" Tile Size", DEFAULT_TILE_SIZE, LABEL_SIZE),
                new InputTriple(" Grid Width", DEFAULT_GRID_WIDTH, LABEL_SIZE)
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

    private void setupButton() {
        JButton start = new JButton("Start");
        start.addActionListener(e -> {
            boolean hasAllInputs = true;
            for (int i = 0; i < triples.length; i++) {
                try {
                    triples[i].extractValue();
                    if (triples[i].value < 1) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    triples[i].textField.setText("error");
                    hasAllInputs = false;
                }
            }
            done = hasAllInputs;
        });
        add(start, BorderLayout.EAST);
    }

    public boolean isDone() {
        return done;
    }

    public Setting getSetting() {
        return new Setting(
                triples[0].value,
                triples[1].value,
                triples[2].value,
                triples[3].value
        );
    }
}
