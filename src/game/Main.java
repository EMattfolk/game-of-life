package game;

import utils.Setting;
import windows.GameFrame;
import windows.SettingsFrame;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Erik Mattfolk on 2017-04-27.
 *
 * Main entry point of the program.
 * Here the settings frame is first shown and then the game is started
 */
public final class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static final int WAIT_TIME = 200;

    private Main() {}

    public static void main(String[] args) {
        SettingsFrame settingsFrame = new SettingsFrame();
        while (!settingsFrame.isDone()) {
            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
                System.exit(0);
            }
        }
        settingsFrame.dispose();
        Setting setting = settingsFrame.getSetting();
        Game game = new Game(setting);

        new GameFrame(game);
        game.start();
    }
}
