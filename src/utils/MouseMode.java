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

    protected final MouseHelper mouseHelper;
    private boolean leftDown;
    private boolean rightDown;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean leftReleased;
    private boolean rightReleased;

    protected MouseMode(Setting setting) {
        mouseHelper = new MouseHelper(setting);
        leftDown = false;
        rightDown = false;
        leftPressed = false;
        rightPressed = false;
        leftReleased = false;
        rightReleased = false;
    }

    /*
     *  Start implementable methods
     *  Note that none of these methods have to be implemented to have a valid MouseMode
     */

    /**
     * @param x field x-coordinate
     * @param y field y-coordinate
     *
     * Called when a mouse button is pressed
     */
    public void onPress(int x, int y) {}

    /**
     * @param x field x-coordinate
     * @param y field y-coordinate
     *
     * Called when a mouse button is released
     */
    public void onRelease(int x, int y) {}

    /**
     * @param x field x-coordinate
     * @param y field y-coordinate
     *
     * Called when a mouse button is held down and the mouse is moved
     */
    public void onDrag(int x, int y) {}

    /**
     * @param dir direction of scrolling
     *
     * Called when the scroll wheel moves
     */
    public void onWheel(int dir) {}

    /*
     *  End implementable methods
     */

    /**
     * FINAL
     * @param e MouseEvent
     *
     * Handle mousePresses and set up variables so that they are in the right state when calling onPress.
     */
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

    /**
     * FINAL
     * @param e MouseEvent
     *
     * Handle mouseReleases and set up variables so that they are in the right state when calling onRelease.
     */
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

    /**
     * FINAL
     * @param e MouseEvent
     *
     * Handle mouseDragged event and update mouseHelper position before calling onDrag.
     */
    public final void mouseDragged(MouseEvent e) {
        mouseHelper.setMousePosition(e.getX(), e.getY());
        onDrag(mouseHelper.getX(), mouseHelper.getY());
    }

    /**
     * FINAL
     * @param e MouseEvent
     *
     * Handle mouseMoved event and update mouseHelper position.
     */
    public final void mouseMoved(MouseEvent e) {
        mouseHelper.setMousePosition(e.getX(), e.getY());
    }

    /**
     * FINAL
     * @param e MouseEvent
     *
     * Handle mouseWheelMoved event and call onWheel with the scroll direction.
     */
    public final void mouseWheelMoved(MouseWheelEvent e) {
        onWheel(e.getWheelRotation());
    }

    public final MouseHelper getMouseHelper() {
        return mouseHelper;
    }

    public boolean isLeftDown() {
        return leftDown;
    }

    public boolean isRightDown() {
        return rightDown;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isLeftReleased() {
        return leftReleased;
    }

    public boolean isRightReleased() {
        return rightReleased;
    }

}
