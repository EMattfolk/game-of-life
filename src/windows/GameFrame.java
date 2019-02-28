package windows;

import game.Game;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Erik Mattfolk on 2017-04-27.
 *
 * The JFrame on which the game is displayed
 * This class is a JFrame but with some altered settings
 */
public class GameFrame extends JFrame {
    public GameFrame(Game game) {
        game.setGameFrame(this);
        setLayout(new BorderLayout());
        add(game, BorderLayout.CENTER);
        setBounds(100, 100, 0, 0);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }
}
