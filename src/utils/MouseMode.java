package utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Created by Erik Mattfolk on 2019-02-26
 *
 * This is a wrapper class for MouseAdapter.
 * It simplifies making mouse handlers which interact with the game.
 */
public abstract class MouseMode extends MouseAdapter {

    protected MouseHelper mouseHelper;
    protected boolean leftDown;
    protected boolean rightDown;
    protected boolean leftPressed;
    protected boolean rightPressed;
    protected boolean leftReleased;
    protected boolean rightReleased;

    protected MouseMode(Setting setting) {
        mouseHelper = new MouseHelper(setting);
        leftDown = false;
        rightDown = false;
        leftPressed = false;
        rightPressed = false;
        leftReleased = false;
        rightReleased = false;
    }

    public final void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftDown = true;
            leftPressed = true;
        }
        else if (e.getButton() == MouseEvent.BUTTON3) {
            rightDown = true;
            rightPressed = true;
        }
        onPress(mouseHelper.getX(), mouseHelper.getY());
        leftPressed = false;
        rightPressed = false;
    }

    public final void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftDown = false;
            leftReleased = true;
        }
        else if (e.getButton() == MouseEvent.BUTTON3) {
            rightDown = false;
            rightReleased = true;
        }
        onRelease(mouseHelper.getX(), mouseHelper.getY());
        leftReleased = false;
        rightReleased = false;
    }

    public final void mouseDragged(MouseEvent e) {
        mouseHelper.setMousePosition(e.getX(), e.getY());
        onDrag(mouseHelper.getX(), mouseHelper.getY());
    }

    public final void mouseMoved(MouseEvent e) {
        mouseHelper.setMousePosition(e.getX(), e.getY());
    }

    public final void mouseWheelMoved(MouseWheelEvent e) {
        onWheel(e.getWheelRotation());
    }

    public void onPress(int x, int y) {}
    public void onRelease(int x, int y) {}
    public void onDrag(int x, int y) {}
    public void onWheel(int dir) {}

    public final MouseHelper getMouseHelper() {
        return mouseHelper;
    }
}
