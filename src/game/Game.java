package game;

import utils.AbstractMouseMode;
import utils.Setting;
import windows.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Erik Mattfolk on 2017-04-27.
 * Refactored on 2019-02-17
 *
 * Game of Life
 *
 * Rules:
 * Tile is "born" if it has 3 neighbors
 * Tile "dies" if it has less than 2 neighbors or greater than 3 neighbors
 *
 * Controls:
 * Switch mode:               Shift
 * Start/Stop simulation:     Space
 * Clear board:               C
 * Fast Mode:                 F
 *
 * In Tilemode (Default)
 * Create life:               Left click  (hold and drag to create more)
 * Kill life:                 Right click (hold and drag to kill more)
 * Change simulation speed:   Right/Left arrow keys
 * Simulate one generation:   Up arrow key
 *
 * In Shapemode
 * Save shapes:               Mark area using Right mouse button
 * Place shapes:              Left click
 * Cycle shapes:              Left/Right arrow keys
 * Rotate shapes:             Up arrow key
 * Delete shapes:             D
 *
 * Version history:
 * 1.0: It works
 * 1.0.1: Fixed the game crashing at "high" loads and if sleep time was negative
 * 1.1: Added saving, printing and rotation of shapes and restructured the program which led to better performance
 * 1.2: Refactored all code and changed the controls. Added a fast mode and more options for rendering.
 */

public class Game extends JComponent{

    private static final Logger LOGGER = Logger.getLogger(ShapeHandler.class.getName());
    private static final long FPS = 30;
    private static final long MILLION = 1000000;
    private static final long BILLION = 1000000000;
    private static final int UPS_SOFT_CAP = 0; // Increase this to allow placing while simulating (unsafe)
    private static final int UPS_HARD_CAP = 1000;

    private long updateTime;
    private long ups;
    private boolean paused, tileMode, fastMode;
    private Shape currentShape;
    private GameFrame gameFrame;
    private Field field;
    private Renderer renderer;
    private ShapeHandler shapeHandler;
    private AbstractMouseMode tileMouse, shapeMouse;
    private KeyListener keyListener;
    private Setting setting;

    public Game(Setting setting) {
        this.setting = setting;
        ups = 10;
        paused = true;
        tileMode = true;
        fastMode = false;
        currentShape = Shape.EMPTY;
        field = new Field(setting.width, setting.height);
        renderer = new Renderer(setting);
        shapeHandler = new ShapeHandler();
        setupListeners();
    }

    /**
     * Game loop
     * This function starts the game and updates it
     */
    public void start() {
        gameFrame.pack();
        updateTime = BILLION / ups;
        long frameTime = 1000 / FPS;
        long lastUpdate = System.nanoTime();
        boolean running = true;

        while (running) {
            long frameStart = System.nanoTime();

            while (lastUpdate + updateTime < frameStart) {
                lastUpdate += updateTime;
                if (!paused) {
                    gameUpdate();
                }
            }
            while (!paused && fastMode && frameTime - (System.nanoTime() - frameStart) / MILLION > 0) {
                gameUpdate();
            }

            render();

            long frameEnd = System.nanoTime();

            long sleepTime = (int) (frameTime - (frameEnd - frameStart) / MILLION);

            if (sleepTime < 0)
                continue;

            try {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
                running = false;
            }
        }
    }

    /**
     * Update the game
     * The game is not that complicated so only the field needs to be updated.
     */
    private void gameUpdate() {
        field.update();
    }

    /**
     * Draw the game
     * Some things should not be drawn at all times. Hence the need for multiple draw calls.
     */
    private void render() {
        renderer.clear();
        renderer.drawGridlines();
        if (!tileMode) {
            renderer.drawShapeOutline(currentShape, shapeMouse.getMouseHelper().getPos());
        }
        renderer.drawActiveTiles(field);
        if (!tileMode) {
            renderer.drawMarking(shapeMouse.getMouseHelper());
        }
        repaint();
    }

    /**
     * Set up MouseListeners and KeyListener
     * This function sets up the listeners so that user input can be handled.
     */
    private void setupListeners() {
        setFocusable(true);
        setupMouseModes();
        setMouse(tileMouse);
        setUpKeyListener();
        addKeyListener(keyListener);
        requestFocus();
    }

    /**
     * Create a KeyListener for handling keyboard input
     */
    private void setUpKeyListener() {
        keyListener = new KeyListener() {

            public void keyPressed(KeyEvent e) {

                int key = e.getKeyCode();

                // Global keys. Used to start/stop simulation and change between modes
                if (key == KeyEvent.VK_SPACE) {
                    paused = !paused;
                    updateFrameTitle();
                }
                else if (key == KeyEvent.VK_SHIFT) {
                    switchMode();
                }
                else if (key == KeyEvent.VK_C) {
                    if (canPlaceTile()) {
                        field.reset();
                        updateFrameTitle();
                    }
                }
                else if (key == KeyEvent.VK_F) {
                    fastMode = !fastMode;
                    updateFrameTitle();
                }

                // Keybindings in tile mode
                if (tileMode) {
                    if (key == KeyEvent.VK_UP) {
                        if (paused) {
                            gameUpdate();
                        }
                    }
                    else if (key == KeyEvent.VK_LEFT) {
                        changeUps(false);
                    }
                    else if (key == KeyEvent.VK_RIGHT) {
                        changeUps(true);
                    }
                }
                // Keybindings in shape mode
                else {
                    if (key == KeyEvent.VK_UP) {
                        currentShape = currentShape.getRotation();
                    }
                    else if (key == KeyEvent.VK_LEFT) {
                        shapeHandler.cycleBackward();
                        currentShape = shapeHandler.getCurrentShape();
                    }
                    else if (key == KeyEvent.VK_RIGHT) {
                        shapeHandler.cycleForward();
                        currentShape = shapeHandler.getCurrentShape();
                    }
                    else if (key == KeyEvent.VK_D) {
                        shapeHandler.deleteCurrentShape();
                        currentShape = shapeHandler.getCurrentShape();
                    }
                }
            }

            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {}
        };
    }

    /**
     * Switch between tileMode and shapeMode
     * The main purpose of this method is to change the MouseListener
     */
    private void switchMode() {
        tileMode = !tileMode;
        updateFrameTitle();
        removeMouse();
        if (tileMode) {
            currentShape = Shape.EMPTY;
            tileMouse.getMouseHelper().copyPosFrom(shapeMouse.getMouseHelper());
            setMouse(tileMouse);
        }
        else {
            currentShape = shapeHandler.getCurrentShape();
            shapeMouse.getMouseHelper().copyPosFrom(tileMouse.getMouseHelper());
            setMouse(shapeMouse);
        }
    }

    private void setMouse(MouseAdapter mouse) {
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    private void removeMouse() {
        removeMouseListener(getMouseListeners()[0]);
        removeMouseMotionListener(getMouseMotionListeners()[0]);
    }

    /**
     * Create MouseListeners for tileMode and shapeMode
     */
    private void setupMouseModes() {
        tileMouse = new AbstractMouseMode(setting) {
            @Override
            public void onPress(int x, int y) {
                if (canPlaceTile() && (leftPressed || rightPressed)) {
                    field.setTile(x, y, leftDown);
                }
            }

            @Override
            public void onDrag(int x, int y) {
                if (canPlaceTile() && (leftDown || rightDown)) {
                    field.setTile(x, y, leftDown);
                }
            }

            @Override
            public void onRelease(int x, int y) {}
        };

        shapeMouse = new AbstractMouseMode(setting) {
            @Override
            public void onPress(int x, int y) {
                if (canPlaceTile() && leftPressed) {
                    field.putShape(x, y, currentShape);
                }
                else if (rightPressed) {
                    mouseHelper.startMarking();
                    currentShape = Shape.EMPTY;
                }
            }

            @Override
            public void onRelease(int x, int y) {
                if (rightReleased) {
                    mouseHelper.endMarking();
                    shapeHandler.addShape(field.getShape(mouseHelper.getMarking()));
                    shapeHandler.cycleToEnd();
                    currentShape = shapeHandler.getCurrentShape();
                }
            }

            @Override
            public void onDrag(int x, int y) {}
        };
    }

    private boolean canPlaceTile() {
        return (ups < UPS_SOFT_CAP && !fastMode) || paused;
    }

    /**
     * @param increase If true, increase the updates per second. Otherwise decrease.
     */
    private void changeUps(boolean increase) {
        int change = increase ? 1 : -1;
        ups = Math.max(1, Math.min(ups + change, UPS_HARD_CAP)); // Clamp ups between 1 and UPS_HARD_CAP
        updateTime = BILLION / ups;
        updateFrameTitle();
    }

    /**
     * Set the frame title to a String with information about the game.
     */
    private void updateFrameTitle() {
        gameFrame.setTitle(
            String.format("%s - %s Updates / sec - %s",
                    paused ? "Paused" : "Running",
                    fastMode ? "Many" : String.valueOf(ups),
                    tileMode ? "Tile mode" : "Shape mode")
        );
    }

    public void paint(Graphics g) {
        g.drawImage(renderer.getImage(), 0, 0, null);
    }

    public Dimension getPreferredSize() {
        return renderer.getDimension();
    }

    public void setGameFrame(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        updateFrameTitle();
    }
}
