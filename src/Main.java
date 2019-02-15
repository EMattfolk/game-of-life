/**
 * Created by Erik Mattfolk on 2017-04-27.
 * <p>
 * Runs the program
 */
public class Main {
    public static void main(String args[]) {
        SettingsFrame settingsFrame = new SettingsFrame();
        while (!settingsFrame.isDone()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
        }
        settingsFrame.dispose();
        Setting setting = settingsFrame.get_setting();
        Game game = new Game(setting);
        Frame frame = new Frame(game);
        game.setFrame(frame);
        game.start();
    }
}
