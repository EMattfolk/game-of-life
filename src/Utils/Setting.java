package Utils;

/**
 * Created by Erik Mattfolk on 2017-04-29.
 *
 * Basically a immutable struct for the settings
 * that can be specified when opening the program
 */
public class Setting {

    public final int width, height, size;

    public Setting(int width, int height, int size) {
        this.width = width;
        this.height = height;
        this.size = size;
    }
}
