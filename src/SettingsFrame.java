import javax.swing.*;
import java.awt.*;

/**
 * Created by Pwnxl on 2017-04-28.
 * <p>
 * Allows the user to input settings
 */
public class SettingsFrame extends JFrame {

    private JPanel left, right;
    private JTextField widthText, heightText, sizeText;
    private boolean done;
    private int width, height, size;

    public SettingsFrame() {
        done = false;
        setLayout(new BorderLayout());
        setBounds(100, 100, 0, 0);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Game of Life");
        setResizable(false);

        setupPanels();
        setupLabels();
        setupTextfields();
        setup_button();

        pack();
        setVisible(true);
    }

    private void setupPanels() {
        left = new JPanel();
        right = new JPanel();
        left.setLayout(new BorderLayout());
        right.setLayout(new BorderLayout());
        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
    }

    private void setupLabels() {
        JLabel widthLabel = new JLabel(" Width");
        JLabel heightLabel = new JLabel(" Height");
        JLabel sizeLabel = new JLabel(" Tile Size ");
        left.add(widthLabel, BorderLayout.NORTH);
        left.add(heightLabel, BorderLayout.CENTER);
        left.add(sizeLabel, BorderLayout.SOUTH);
    }

    private void setupTextfields() {
        widthText = new JTextField("45");
        heightText = new JTextField("45");
        sizeText = new JTextField("15");
        widthText.setPreferredSize(new Dimension(120, 20));
        heightText.setPreferredSize(new Dimension(120, 20));
        sizeText.setPreferredSize(new Dimension(120, 20));
        right.add(widthText, BorderLayout.NORTH);
        right.add(heightText, BorderLayout.CENTER);
        right.add(sizeText, BorderLayout.SOUTH);
    }

    private void setup_button() {
        JButton start = new JButton("Start");
        start.addActionListener(e -> {
            boolean flag = true;
            try {
                width = Integer.parseInt(widthText.getText());
                if (width < 1) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                widthText.setText("error");
                flag = false;
            }
            try {
                height = Integer.parseInt(heightText.getText());
                if (height < 1) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                heightText.setText("error");
                flag = false;
            }
            try {
                size = Integer.parseInt(sizeText.getText());
                if (size < 1) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                sizeText.setText("error");
                flag = false;
            }
            if (flag) {
                done = true;
            }
        });
        add(start, BorderLayout.EAST);
    }

    public boolean isDone() {
        return done;
    }

    public Setting get_setting() {
        return new Setting(width, height, size);
    }
}
