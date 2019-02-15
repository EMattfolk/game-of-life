import javax.swing.*;
import java.awt.*;

/**
 * Created by Erik Mattfolk on 2017-04-27.
 * <p>
 * The JFrame on which the game is displayed
 */
public class Frame extends JFrame {
    public Frame(Game game) {
        setLayout(new BorderLayout());
        add(game, BorderLayout.CENTER);
        setBounds(100, 100, 0, 0);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Game of Life   UPS: 10");
        setResizable(false);
        setVisible(true);
    }
}
