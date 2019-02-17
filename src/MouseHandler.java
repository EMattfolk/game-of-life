/**
 * Created by Pwnxl on 2017-05-11.
 * <p>
 * Keeps track of mouse movements, positions and updates
 */
public class MouseHandler {

    private int tileSize, mouseX, mouseY, markX, markY;
    private boolean changed, marking;

    public MouseHandler(int tileSize) {
        this.tileSize = tileSize;
        mouseX = -Integer.MAX_VALUE;
        mouseY = -Integer.MAX_VALUE;
        changed = true;
        marking = false;
    }

    public void set_mouse_position(int x, int y) {
        x = x / tileSize;
        y = y / tileSize;
        if (x != mouseX || y != mouseY) {
            mouseX = x;
            mouseY = y;
            changed = true;
        }
    }

    public boolean hasChanged() {
        if (changed) {
            changed = false;
            return true;
        }
        return false;
    }

    public boolean isMarking() {
        return marking;
    }

    public void setMarking(boolean b) {
        if (b) {
            markX = mouseX;
            markY = mouseY;
            changed = true;
        }
        marking = b;
    }

    public int getX() {
        return mouseX;
    }

    public int getY() {
        return mouseY;
    }

    public int getMarkX() {
        return markX;
    }

    public int getMarkY() {
        return markY;
    }
}
