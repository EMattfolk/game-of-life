import javax.swing.*;
import java.awt.*;

/**
 * Created by Erik Mattfolk on 2017-04-27.
 *
 * The JFrame on which the game is displayed
 * This class is a JFrame but with some altered settings
 */
public class Frame extends JFrame {
    public Frame(Game game) {
        game.setFrame(this);
        setLayout(new BorderLayout());
        add(game, BorderLayout.CENTER);
        setBounds(100, 100, 0, 0);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }
}
