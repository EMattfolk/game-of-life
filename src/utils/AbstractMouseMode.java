package utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Erik Mattfolk on 2019-02-26
 *
 * This is a wrapper class for MouseAdapter.
 * It simplifies making mouse handlers which interact with the game.
 */
public abstract class AbstractMouseMode extends MouseAdapter {

    protected MouseHelper mouseHelper;
    protected boolean leftDown;
    protected boolean rightDown;
    protected boolean leftPressed;
    protected boolean rightPressed;
    protected boolean leftReleased;
    protected boolean rightReleased;

    protected AbstractMouseMode(Setting setting) {
        mouseHelper = new MouseHelper(setting);
        leftDown = false;
        rightDown = false;
        leftPressed = false;
        rightPressed = false;
        leftReleased = false;
        rightReleased = false;
    }

    public void mousePressed(MouseEvent e) {
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

    public void mouseReleased(MouseEvent e) {
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

    public void mouseDragged(MouseEvent e) {
        mouseHelper.setMousePosition(e.getX(), e.getY());
        onDrag(mouseHelper.getX(), mouseHelper.getY());
    }

    public void mouseMoved(MouseEvent e) {
        mouseHelper.setMousePosition(e.getX(), e.getY());
    }

    abstract public void onPress(int x, int y);
    abstract public void onRelease(int x, int y);
    abstract public void onDrag(int x, int y);

    public MouseHelper getMouseHelper() {
        return mouseHelper;
    }
}
