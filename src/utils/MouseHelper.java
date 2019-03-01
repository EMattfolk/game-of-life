package utils;

import java.awt.*;

/**
 * Created by Erik Mattfolk on 2017-05-11.
 * Refactored on 2019-02-19
 *
 * Keeps track of mouse movements, positions and updates
 * This class is meant to translate what you see on the screen to what is usable by the program.
 * Therefore it is closely related to the Game.Renderer
 */
public class MouseHelper {

    private int tileSize;
    private Vec2 pos;
    private Vec2 markPos;
    private boolean marking;

    public MouseHelper(Setting setting) {
        this.tileSize = setting.tileSize + setting.gridWidth;
        pos = new Vec2();
        markPos = new Vec2();
        marking = false;
    }

    public void setMousePosition(int x, int y) {
        pos.x = x / tileSize;
        pos.y = y / tileSize;
    }

    public boolean isMarking() {
        return marking;
    }

    public void startMarking() {
        marking = true;
        markPos = pos.copy();
    }

    public void endMarking() {
        marking = false;
    }

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

    public Vec2 getPos() {
        return pos.copy();
    }

    public void copyPosFrom(MouseHelper other) {
        pos = other.getPos();
    }
}
