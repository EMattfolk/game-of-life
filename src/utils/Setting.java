package utils;

/**
 * Created by Erik Mattfolk on 2017-04-29.
 *
 * Immutable struct for the settings
 * that can be specified when opening the program
 */
public class Setting {

    public final int width;
    public final int height;
    public final int tileSize;
    public final int gridWidth;

    public Setting(int width, int height, int tileSize, int gridWidth) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.gridWidth = gridWidth;
    }
}
