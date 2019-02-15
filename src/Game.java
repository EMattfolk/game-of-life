import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ConcurrentModificationException;

/**
 * Created by Erik Mattfolk on 2017-04-27.
 * <p>
 * Game of Life recreated by Erik Mattfolk
 * <p>
 * Rules:
 * Tile is "born" if it has 3 neighbours
 * Tile "dies" if it has less than two neighbors or greater than three neighbors
 * <p>
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
 * <p>
 * Version history:
 * 1.0: It works
 * 1.0.1: Fixed the game crashing at "high" loads and if sleep time was negative
 * 1.1: Added saving, printing and rotation of shapes and restructured the program which led to better performance
 */

public class Game extends JComponent {
    private final long MILLION = 1000000, BILLION = 1000000000, FPS = 60;
    private long frame_time, update_time, UPS;
    private boolean paused, tileMode;
    private Shape currentShape;
    private Vec2 current_offset;
    private Frame frame;
    private Field field;
    private Renderer renderer;
    private ShapeHandler shapeHandler;
    private MouseHandler mouseHandler;
    private BufferedImage image;
    private MouseAdapter tileMouse, shapeMouse;

    public Game(Setting setting) {

        int width = setting.width;
        int height = setting.height;
        int tile_size = setting.size + 1; // +1 is the border
        UPS = 10;
        paused = true;
        tileMode = true;
        currentShape = null;
        current_offset = null;
        field = new Field(width, height);
        renderer = new Renderer(width, height, tile_size, field);
        shapeHandler = new ShapeHandler();
        mouseHandler = new MouseHandler(tile_size);
        image = renderer.get_image();
        setPreferredSize(get_frame_dimension());
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
                    try {
                        game_update();
                    }
                    catch (ConcurrentModificationException e) {
                        System.out.println("Concurrent Modification: You are probably going too fast");
                    }
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

    private void game_update() {
        field.update();
    }

    private void render() {

        boolean field_change = field.has_changed(), mouse_change = mouseHandler.has_changed();
        if (mouseHandler.is_marking() && (mouse_change || field_change)) {
            renderer.render();
            renderer.draw_marking(mouseHandler.get_x(), mouseHandler.get_y(), mouseHandler.get_mark_x(), mouseHandler.get_mark_y());
            repaint();
        }
        else if (currentShape != null && (mouse_change || field_change)) {
            renderer.render();
            renderer.draw_shape_outline(mouseHandler.get_x(), mouseHandler.get_y(), currentShape.getPoints(), current_offset);
            repaint();
        }
        else if (field_change) {
            renderer.render();
            repaint();
        }
    }

    private void setup_listeners() {
        setFocusable(true);
        setupMouseModes();
        setMouse(tileMouse);
        addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
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
                else if (key == KeyEvent.VK_UP) {
                    if (paused) {
                        game_update();
                    }
                }
                else if (key == KeyEvent.VK_DOWN) {
                    paused = !paused;
                }
                else if (key == KeyEvent.VK_LEFT) {
                    changeUPS(false);
                }
                else if (key == KeyEvent.VK_RIGHT) {
                    changeUPS(true);
                }
                else if (!tileMode && key == KeyEvent.VK_CONTROL) {
                    currentShape = currentShape.getRotation();
                    current_offset = currentShape.getMiddle();
                    field.set_changed();
                }
                else if (!tileMode && key == KeyEvent.VK_UP) {

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

                int x = mouseHandler.get_x();
                int y = mouseHandler.get_y();

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

                mouseHandler.set_mouse_position(e.getX(), e.getY());
                int x = mouseHandler.get_x();
                int y = mouseHandler.get_y();

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
                mouseHandler.set_mouse_position(e.getX(), e.getY());
            }
        };
        shapeMouse = new MouseAdapter() {

            int start_x, start_y, end_x, end_y;

            public void mousePressed(MouseEvent e) {

                if (currentShape != null && e.getButton() == MouseEvent.BUTTON1) {
                    field.put_shape(mouseHandler.get_x(), mouseHandler.get_y(), currentShape.getPoints(), current_offset);
                }
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    start_x = mouseHandler.get_x();
                    start_y = mouseHandler.get_y();
                    mouseHandler.set_marking(true);
                    field.set_changed();
                }
            }

            public void mouseReleased(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON3) {
                    mouseHandler.set_marking(false);
                    end_x = mouseHandler.get_x();
                    end_y = mouseHandler.get_y();
                    shapeHandler.add_shape(field.get_shape(start_x, start_y, end_x, end_y));
                    field.set_changed();
                }
            }

            public void mouseDragged(MouseEvent e) {
                mouseHandler.set_mouse_position(e.getX(), e.getY());
            }

            public void mouseMoved(MouseEvent e) {
                mouseHandler.set_mouse_position(e.getX(), e.getY());
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
        g.drawImage(image, 0, 0, null);
    }

    public Dimension get_frame_dimension() {
        return new Dimension(image.getWidth(), image.getHeight());
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
    }
}
