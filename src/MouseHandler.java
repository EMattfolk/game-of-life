/**
 * Created by Pwnxl on 2017-05-11.
 *
 * Keeps track of mouse movements, positions and updates
 */
public class MouseHandler {

    private int tile_size, mouse_x, mouse_y, mark_x, mark_y;
    private boolean changed, marking;

    public MouseHandler (int tile_size)
    {
        this.tile_size = tile_size;
        mouse_x = -Integer.MAX_VALUE;
        mouse_y = -Integer.MAX_VALUE;
        changed = true;
        marking = false;
    }

    public void set_mouse_position (int x, int y)
    {
        x = x/tile_size;
        y = y/tile_size;
        if (x != mouse_x || y != mouse_y)
        {
            mouse_x = x;
            mouse_y = y;
            changed = true;
        }
    }

    public void set_marking(boolean b)
    {
        if (b)
        {
            mark_x = mouse_x;
            mark_y = mouse_y;
            changed = true;
        }
        marking = b;
    }

    public boolean has_changed ()
    {
        if (changed)
        {
            changed = false;
            return true;
        }
        return false;
    }

    public boolean is_marking ()
    {
        return marking;
    }

    public int get_x ()
    {
        return mouse_x;
    }

    public int get_y ()
    {
        return mouse_y;
    }

    public int get_mark_x () { return mark_x; }

    public int get_mark_y ()
    {
        return mark_y;
    }
}
