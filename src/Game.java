import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ConcurrentModificationException;

/**
 * Created by Erik Mattfolk on 2017-04-27.
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

public class Game extends JComponent
{
    private final long MILLION = 1000000, BILLION = 1000000000;
    private int width, height, tile_size;
    private long frame_time, update_time, UPS, FPS;
    private boolean paused, updating;
    private Shape current_shape;
    private Vec2 current_offset;
    private Frame frame;
    private Field field;
    private Renderer renderer;
    private ShapeHandler shape_handler;
    private MouseHandler mouse_handler;
    private BufferedImage image;
    private MouseAdapter tile_mouse, shape_mouse;

    public Game(Setting setting)
    {
        width = setting.width;
        height = setting.height;
        tile_size = setting.size + 1; // +1 is the border
        UPS = 10;
        FPS = 60;
        paused = true;
        updating = false;
        current_shape = null;
        current_offset = null;
        field = new Field(width, height);
        renderer = new Renderer(width, height, tile_size, field);
        shape_handler = new ShapeHandler();
        mouse_handler = new MouseHandler(tile_size);
        image = renderer.get_image();
        setPreferredSize(get_frame_dimension());
        setup_listeners();
    }

    public void start()
    {
        frame.pack();
        update_time = BILLION/UPS;
        frame_time = 1000/FPS;
        long start_time, end_time, sleep_time, last_time = System.nanoTime();
        boolean running = true;

        while (running)
        {
            start_time = System.nanoTime();

            if (last_time + update_time < start_time)
            {
                last_time += update_time;
                if (!paused)
                {
                    try
                    {
                        game_update();
                    }
                    catch (ConcurrentModificationException e)
                    {
                        System.out.println("Concurrent Modification: You are probably going too fast");
                    }
                }
            }
            render();

            end_time = System.nanoTime();

            sleep_time = (int)(frame_time-(end_time-start_time)/MILLION);

            try
            {
                Thread.sleep(sleep_time);
            }
            catch (InterruptedException e)
            {
                running = false;
            }
            catch (IllegalArgumentException e)
            {
                System.out.println("Negative time value");
            }
        }
    }

    private void game_update ()
    {
        updating = true;
        field.update();
        updating = false;
    }

    private void render()
    {
        boolean field_change = field.has_changed(), mouse_change = mouse_handler.has_changed();
        if (mouse_handler.is_marking() && (mouse_change || field_change))
        {
            renderer.render();
            renderer.draw_marking(mouse_handler.get_x(), mouse_handler.get_y(), mouse_handler.get_mark_x(), mouse_handler.get_mark_y());
            repaint();
        }
        else if (current_shape != null && (mouse_change || field_change))
        {
            renderer.render();
            renderer.draw_shape_outline(mouse_handler.get_x(), mouse_handler.get_y(), current_shape.getPoints(), current_offset);
            repaint();
        }
        else if (field_change)
        {
            renderer.render();
            repaint();
        }
    }

    private void setup_listeners ()
    {
        setFocusable(true);
        setupMouseModes();
        setMouse(tile_mouse);
        addKeyListener(new KeyListener()
        {

            public void keyTyped(KeyEvent e) {}

            public void keyPressed(KeyEvent e)
            {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_UP)
                {
                    if (paused)
                    {
                        game_update();
                    }
                }
                else if (key == KeyEvent.VK_DOWN)
                {
                    paused = !paused;
                }
                else if (key == KeyEvent.VK_LEFT)
                {
                    change_UPS(false);
                }
                else if (key == KeyEvent.VK_RIGHT)
                {
                    change_UPS(true);
                }
                else if (key == KeyEvent.VK_SPACE)
                {
                    paused = true;
                    field.reset();
                }
                else if (key == KeyEvent.VK_CONTROL && current_shape != null)
                {
                    current_shape = current_shape.getRotation();
                    current_offset = current_shape.getMiddle();
                    field.set_changed();
                }
                else if (key == KeyEvent.VK_S)
                {
                    current_shape = shape_handler.getCurrentShape();
                    current_offset = current_shape.getMiddle();
                    removeMouse();
                    setMouse(shape_mouse);
                    field.set_changed();
                }
            }

            public void keyReleased(KeyEvent e) {}
        });
        requestFocus();
    }

    private void setMouse (MouseAdapter mouse)
    {
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    private void removeMouse ()
    {
        removeMouseListener(getMouseListeners()[0]);
        removeMouseMotionListener(getMouseMotionListeners()[0]);
    }

    private void setupMouseModes ()
    {
        tile_mouse = new MouseAdapter()
        {
            boolean left_down = false, right_down = false;
            public void mousePressed (MouseEvent e)
            {
                int x = mouse_handler.get_x();
                int y = mouse_handler.get_y();
                if (e.getButton() == MouseEvent.BUTTON1)
                {
                    left_down = true;
                    change_tile(x, y,true);
                }
                else if (e.getButton() == MouseEvent.BUTTON3)
                {
                    right_down = true;
                    change_tile(x, y, false);
                }
            }

            public void mouseDragged (MouseEvent e)
            {
                mouse_handler.set_mouse_position(e.getX(), e.getY());
                int x = mouse_handler.get_x();
                int y = mouse_handler.get_y();
                if (left_down)
                {
                    change_tile(x, y,true);
                }
                else if (right_down)
                {
                    change_tile(x, y,false);
                }
            }

            public void mouseReleased (MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON1)
                {
                    left_down = false;
                }
                else if (e.getButton() == MouseEvent.BUTTON2)
                {
                    right_down = false;
                }
            }

            public void mouseMoved (MouseEvent e)
            {
                mouse_handler.set_mouse_position(e.getX(), e.getY());
            }
        };
        shape_mouse = new MouseAdapter ()
        {
            int start_x, start_y, end_x, end_y;
            public void mousePressed (MouseEvent e)
            {
                if (current_shape != null && e.getButton() == MouseEvent.BUTTON1)
                {
                    field.put_shape(mouse_handler.get_x(), mouse_handler.get_y(), current_shape.getPoints(), current_offset);
                }
                else if (e.getButton() == MouseEvent.BUTTON3)
                {
                    start_x = mouse_handler.get_x();
                    start_y = mouse_handler.get_y();
                    mouse_handler.set_marking(true);
                    field.set_changed();
                }
            }

            public void mouseReleased(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON3)
                {
                    mouse_handler.set_marking(false);
                    end_x = mouse_handler.get_x();
                    end_y = mouse_handler.get_y();
                    shape_handler.add_shape(field.get_shape(start_x, start_y, end_x, end_y));
                    field.set_changed();
                }
            }

            public void mouseDragged (MouseEvent e)
            {
                mouse_handler.set_mouse_position(e.getX(), e.getY());
            }

            public void mouseMoved (MouseEvent e)
            {
                mouse_handler.set_mouse_position(e.getX(), e.getY());
            }
        };
    }

    private void change_tile (int x, int y, boolean b)
    {
        if (updating) return;
        field.set_tile(x, y, b);
    }

    private void change_UPS (boolean increase)
    {
        if (increase && UPS < 60)
        {
            UPS++;
        }
        else if (!increase && UPS > 1)
        {
            UPS--;
        }
        update_time = BILLION/UPS;
        frame.setTitle("Game of Life   UPS: " + UPS);
    }

    public void paint (Graphics g)
    {
        g.drawImage(image,0,0,null);
    }

    public Dimension get_frame_dimension()
    {
        return new Dimension(image.getWidth(), image.getHeight());
    }

    public void setFrame (Frame frame)
    {
        this.frame = frame;
    }
}
