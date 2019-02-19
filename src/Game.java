import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Erik Mattfolk on 2017-04-27.
 * Refactored on 2019-02-17
 *
 * Game of Life recreated by Erik Mattfolk
 *
 * Rules:
 * Tile is "born" if it has 3 neighbours
 * Tile "dies" if it has less than two neighbors or greater than three neighbors
 *
 * Controls:
 * Create life: Left click (hold and drag to create more)
 * Kill life: Right click (hold and drag to kill more)
 * Start/Pause simulation: Down arrow key
 * Increase/Decrease simulation speed: Right/Left arrow key
 * When the game is paused, Up arrow key updates the board once
 * Saving shapes: press 0-9 or a-z and drag mouse with right mouse button
 * Printing shapes: press 0-9 or a-z and click with left mouse button
 * Rotate shape: control key
 * Clear board: Space
 *
 * Version history:
 * 1.0: It works
 * 1.0.1: Fixed the game crashing at "high" loads and if sleep time was negative
 * 1.1: Added saving, printing and rotation of shapes and restructured the program which led to better performance
 */

public class Game extends JComponent{
    private final long MILLION = 1000000, BILLION = 1000000000, FPS = 60;
    private long frame_time, update_time, UPS;
    private boolean paused, tileMode;
    private Shape currentShape;
    private Frame frame;
    private Field field;
    private Renderer renderer;
    private ShapeHandler shapeHandler;
    private MouseHelper mouseHelper;
    private MouseAdapter tileMouse, shapeMouse;

    public Game(Setting setting) {

        int width = setting.width;
        int height = setting.height;
        int tile_size = setting.size;
        UPS = 10;
        paused = true;
        tileMode = true;
        currentShape = Shape.EMPTY;
        field = new Field(width, height);
        renderer = new Renderer(width, height, tile_size);
        shapeHandler = new ShapeHandler();
        mouseHelper = renderer.getMousehelper();
        setup_listeners();
    }

    public void start() {

        frame.pack();
        update_time = BILLION / UPS;
        frame_time = 1000 / FPS;
        long start_time, end_time, sleep_time, last_time = System.nanoTime();
        boolean running = true;

        while (running) {
            start_time = System.nanoTime();

            if (last_time + update_time < start_time) {
                last_time += update_time;
                if (!paused) {
                    gameUpdate();
                }
            }

            render();

            end_time = System.nanoTime();

            sleep_time = (int) (frame_time - (end_time - start_time) / MILLION);

            try {
                Thread.sleep(sleep_time);
            }
            catch (InterruptedException e) {
                running = false;
            }
            catch (IllegalArgumentException e) {
                System.out.println("Negative time value");
            }
        }
    }

    private void gameUpdate() {
        field.update();
    }

    private void render() {
        renderer.clear();
        renderer.drawGridlines();
        renderer.drawShapeOutline(currentShape, mouseHelper.getPos());
        renderer.drawActiveTiles(field);
        renderer.drawMarking(mouseHelper);
        repaint();
    }

    private void setup_listeners() {
        setFocusable(true);
        setupMouseModes();
        setMouse(tileMouse);
        addKeyListener(new KeyListener() { //TODO: Move to another file

            public void keyPressed(KeyEvent e) {

                int key = e.getKeyCode();

                // Global keys. Used to start/stop simulation and change between modes
                if (key == KeyEvent.VK_SPACE) {
                    paused = !paused;
                }
                else if (key == KeyEvent.VK_SHIFT) {
                    tileMode = !tileMode;
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
                    else if (key == KeyEvent.VK_D) {
                        field.reset();
                        paused = true;
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
        });
        requestFocus();
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

        tileMouse = new MouseAdapter() {

            boolean left_down = false, right_down = false;

            public void mousePressed(MouseEvent e) {

                int x = mouseHelper.getX();
                int y = mouseHelper.getY();

                if (e.getButton() == MouseEvent.BUTTON1) {
                    left_down = true;
                    change_tile(x, y, true);
                }
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    right_down = true;
                    change_tile(x, y, false);
                }
            }

            public void mouseDragged(MouseEvent e) {

                mouseHelper.setMousePosition(e.getX(), e.getY());
                int x = mouseHelper.getX();
                int y = mouseHelper.getY();

                if (left_down) {
                    change_tile(x, y, true);
                }
                else if (right_down) {
                    change_tile(x, y, false);
                }
            }

            public void mouseReleased(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON1) {
                    left_down = false;
                }
                else if (e.getButton() == MouseEvent.BUTTON2) {
                    right_down = false;
                }
            }

            public void mouseMoved(MouseEvent e) {
                mouseHelper.setMousePosition(e.getX(), e.getY());
            }
        };
        shapeMouse = new MouseAdapter() {

            public void mousePressed(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON1) {
                    field.put_shape(mouseHelper.getX(), mouseHelper.getY(), currentShape.getPoints(), currentShape.getMiddle());
                }
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    mouseHelper.startMarking();
                    currentShape = Shape.EMPTY;
                }
            }

            public void mouseReleased(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON3) {
                    mouseHelper.endMarking();
                    shapeHandler.add_shape(field.getShape(mouseHelper.getMarking()));
                    shapeHandler.cycleToEnd();
                    currentShape = shapeHandler.getCurrentShape();
                }
            }

            public void mouseDragged(MouseEvent e) {
                mouseHelper.setMousePosition(e.getX(), e.getY());
            }

            public void mouseMoved(MouseEvent e) {
                mouseHelper.setMousePosition(e.getX(), e.getY());
            }
        };
    }

    private void change_tile(int x, int y, boolean b) {
        field.set_tile(x, y, b);
    }

    private void changeUPS(boolean increase) {

        if (increase && UPS < 60) {
            UPS++;
        }
        else if (!increase && UPS > 1) {
            UPS--;
        }

        update_time = BILLION / UPS;
        frame.setTitle("Game of Life   UPS: " + UPS);
    }

    public void paint(Graphics g) {
        g.drawImage(renderer.getImage(), 0, 0, null);
    }

    public Dimension getPreferredSize() {
        return renderer.getDimension();
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
    }
}
