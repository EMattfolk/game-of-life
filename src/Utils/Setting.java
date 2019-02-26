package Utils;

/**
 * Created by Erik Mattfolk on 2017-04-29.
 *
 * Basically a immutable struct for the settings
 * that can be specified when opening the program
 */
public class Setting {

    public final int width, height, tileSize, gridWidth;

    public Setting(int width, int height, int tileSize, int gridWidth) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.gridWidth = gridWidth;
    }
}
