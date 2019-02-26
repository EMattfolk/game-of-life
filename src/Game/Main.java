package Game;

import Utils.Setting;
import Windows.Frame;
import Windows.SettingsFrame;

/**
 * Created by Erik Mattfolk on 2017-04-27.
 *
 * Main entry point of the program.
 * Here the settings frame is first shown and then the game is started
 */
public class Main {
    public static void main(String args[]) {
        SettingsFrame settingsFrame = new SettingsFrame();
        while (!settingsFrame.isDone()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.exit(0);
            }
        }
        settingsFrame.dispose();
        Setting setting = settingsFrame.getSetting();
        Game game = new Game(setting);

        new Frame(game);
        game.start();
    }
}
