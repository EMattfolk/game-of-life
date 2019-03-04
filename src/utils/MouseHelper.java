package utils;

import java.awt.*;

/**
 * Created by Erik Mattfolk on 2017-05-11.
 * Refactored on 2019-02-19
 *
 * Keeps track of mouse movements, positions and updates
 * This class is meant to translate what you see on the screen to what is usable by the program.
 */
public class MouseHelper {

    private int cellSize;
    private Vec2 pos;
    private Vec2 markPos;
    private boolean marking;

    public MouseHelper(Setting setting) {
        this.cellSize = setting.tileSize + setting.gridWidth;
        pos = new Vec2();
        markPos = new Vec2();
        marking = false;
    }

    /**
     * @param x pixel x-coordinate
     * @param y pixel y-coordinate
     *
     * Turn pixel coordinates into field coordinates
     */
    public void setMousePosition(int x, int y) {
        pos.x = x / cellSize;
        pos.y = y / cellSize;
    }

    /**
     * Set the start point for the marking.
     */
    public void startMarking() {
        marking = true;
        markPos = pos.copy();
    }

    public void endMarking() {
        marking = false;
    }

    /**
     * @return Rectangle representing the marked area on the field
     */
    public Rectangle getMarking() {
        return new Rectangle(
                Math.min(pos.x, markPos.x),
                Math.min(pos.y, markPos.y),
                Math.abs(pos.x - markPos.x) + 1,
                Math.abs(pos.y - markPos.y) + 1
        );
    }

    public int getX() {
        return pos.x;
    }

    public int getY() {
        return pos.y;
    }

    public boolean isMarking() {
        return marking;
    }

    public Vec2 getPos() {
        return pos.copy();
    }

    public void copyPosFrom(MouseHelper other) {
        pos = other.getPos();
    }
}
