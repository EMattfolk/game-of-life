package Game;

import Utils.AbstractMouseMode;
import Utils.Setting;
import Windows.Frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;

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
 */

public class Game extends JComponent{
    private static final String TITLE_STRING = "%s - %s Updates / sec - %s";
    private static final String TILE_MODE = "Tile mode";
    private static final String SHAPE_MODE = "Shape mode";
    private static final String PAUSED = "Paused";
    private static final String RUNNING = "Running";
    private static final String MANY = "Many";
    private static final long MILLION = 1000000;
    private static final long BILLION = 1000000000;
    private static final long FPS = 30;
    public static final int UPS_SOFT_CAP = 100;
    public static final int UPS_HARD_CAP = 1000;

    private long updateTime;
    private long ups;
    private boolean paused, tileMode, fastMode;
    private Shape currentShape;
    private Frame frame;
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

    public void start() {
        frame.pack();
        updateTime = BILLION / ups;
        long frame_time = 1000 / FPS;
        long frameStart, frameEnd, sleepTime, lastUpdate = System.nanoTime();
        int updatesThisFrame;
        boolean running = true;

        while (running) {
            frameStart = System.nanoTime();
            updatesThisFrame = 0;

            while (lastUpdate + updateTime < frameStart) {
                lastUpdate += updateTime;
                if (!paused) {
                    gameUpdate();
                    updatesThisFrame++;
                }
            }
            while (!paused && fastMode && frame_time - (System.nanoTime() - frameStart) / MILLION > 0) {
                gameUpdate();
                updatesThisFrame++;
            }

            render();

            frameEnd = System.nanoTime();

            sleepTime = (int) (frame_time - (frameEnd - frameStart) / MILLION);

            if (sleepTime < 0)
                continue;

            System.out.println(updatesThisFrame);

            try {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException e) {
                running = false;
            }
        }
    }

    private void gameUpdate() {
        field.update();
    }

    private void render() {
        renderer.clear();
        renderer.drawGridlines();
        if (!tileMode) {
            renderer.drawShapeOutline(currentShape, shapeMouse.getHelper().getPos());
        }
        renderer.drawActiveTiles(field);
        if (!tileMode) {
            renderer.drawMarking(shapeMouse.getHelper());
        }
        repaint();
    }

    private void setupListeners() {
        setFocusable(true);
        setupMouseModes();
        setMouse(tileMouse);
        setUpKeyListener();
        addKeyListener(keyListener);
        requestFocus();
    }

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
                    tileMode = !tileMode;
                    updateFrameTitle();
                    removeMouse();
                    if (tileMode) {
                        currentShape = Shape.EMPTY;
                        setMouse(tileMouse);
                    }
                    else {
                        currentShape = shapeHandler.getCurrentShape();
                        setMouse(shapeMouse);
                    }
                }
                else if (key == KeyEvent.VK_C) {
                    field.reset();
                    paused = true;
                    updateFrameTitle();
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
                        changeUPS(false);
                    }
                    else if (key == KeyEvent.VK_RIGHT) {
                        changeUPS(true);
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

    private void setMouse(MouseAdapter mouse) {
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    private void removeMouse() {
        removeMouseListener(getMouseListeners()[0]);
        removeMouseMotionListener(getMouseMotionListeners()[0]);
    }

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
                    shapeHandler.add_shape(field.getShape(mouseHelper.getMarking()));
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
    private void changeUPS(boolean increase) {
        int change = increase ? 1 : -1;
        ups = Math.max(1, Math.min(ups + change, UPS_HARD_CAP)); // Clamp ups between 1 and UPS_HARD_CAP
        updateTime = BILLION / ups;
        updateFrameTitle();
    }

    private void updateFrameTitle() {
        frame.setTitle(
            String.format(TITLE_STRING,
                    paused ? PAUSED : RUNNING,
                    fastMode ? MANY : String.valueOf(ups),
                    tileMode ? TILE_MODE : SHAPE_MODE)
        );
    }

    public void paint(Graphics g) {
        g.drawImage(renderer.getImage(), 0, 0, null);
    }

    public Dimension getPreferredSize() {
        return renderer.getDimension();
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
        updateFrameTitle();
    }
}
