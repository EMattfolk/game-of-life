import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Erik Mattfolk on 2017-04-27.
 *
 * Keeps track of, and updates cells.
 */
public class Field {

    private int width, height, size;
    private boolean changed;
    private boolean[] field;
    private int[] neighbour_count;
    private ArrayList<int[]> adjacent_indexes;
    private ArrayList<Integer> decrease, increase;
    private HashSet<Integer> check_indexes;

    public Field (int width, int height)
    {
        this.width = width;
        this.height = height;
        size = width * height;
        changed = true;
        field = new boolean[width*height];
        neighbour_count = new int[width*height];
        decrease = new ArrayList<Integer>();
        increase = new ArrayList<Integer>();
        check_indexes = new HashSet<Integer>();
        get_adjacent_indexes();
    }

    public void update ()
    {
        for (int i : check_indexes)
        {
            if (!field[i] && neighbour_count[i] == 3)
            {
                flip_tile(i);
                increase.add(i);
                changed = true;
            }
            else if (field[i] && (neighbour_count[i] < 2 || neighbour_count[i] > 3))
            {
                flip_tile(i);
                decrease.add(i);
                changed = true;
            }
        }
        check_indexes.clear();
        for (int i : increase)
        {
            change_neighbors(i,1);
        }
        for (int i : decrease)
        {
            change_neighbors(i,-1);
        }
        increase.clear();
        decrease.clear();
    }

    public void set_tile (int x, int y, boolean b) //x and y has to be valid
    {
        int index = y * width + x;
        if (!in_bounds(x, y) || field[index] == b) return;
        field[index] = b;
        if (b)
        {
            change_neighbors(index,1);
        }
        else
        {
            change_neighbors(index,-1);
        }
        changed = true;
    }

    public void flip_tile (int index)
    {
        field[index] = !field[index];
    }

    public void put_shape (int x, int y, ArrayList<Vec2> shape, Vec2 offset)
    {
        for (Vec2 p : shape)
        {
            set_tile(x+p.x-offset.x,y+p.y-offset.y,true);
        }
    }

    public ArrayList<Vec2> get_shape (int x1, int y1, int x2, int y2)
    {
        ArrayList<Vec2> shape = new ArrayList<Vec2>();
        x1 = put_within_bounds(x1,width);
        x2 = put_within_bounds(x2,width);
        y1 = put_within_bounds(y1,height);
        y2 = put_within_bounds(y2,height);
        int min_x = x1 < x2 ? x1 : x2, max_x = x1 > x2 ? x1 : x2;
        int min_y = y1 < y2 ? y1 : y2, max_y = y1 > y2 ? y1 : y2;
        int shape_max_y = Integer.MIN_VALUE, shape_min_x = Integer.MAX_VALUE;
        for (int i = min_y; i <= max_y; i++)
        {
            for (int j = min_x; j <= max_x ; j++)
            {
                if (field[i*width + j])
                {
                    shape.add(new Vec2(j,i));
                    shape_max_y = i > shape_max_y ? i : shape_max_y;
                    shape_min_x = j < shape_min_x ? j : shape_min_x;
                }
            }
        }

        int l = shape.size();
        for (int i = 0; i < l; i++) {
            shape.get(i).translate(-shape_min_x, -shape_max_y);
        }

        return shape;
    }

    public boolean get_tile (int x, int y)
    {
        int index = y * width + x;
        return field[index];
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

    public void set_changed ()
    {
        changed = true;
    }

    public void reset ()
    {
        for (int i = 0; i < size; i++)
        {
            if (field[i])
            {
                flip_tile(i);
                change_neighbors(i,-1);
            }
        }
        check_indexes.clear();
        changed = true;
    }

    private boolean in_bounds(int x, int y)
    {
        return y >= 0 && y < height && x >= 0 && x < width;
    }

    private int put_within_bounds (int n, int constaint)
    {
        n = n < 0 ? 0 : n;
        n = n >= constaint ? constaint-1 : n;
        return n;
    }

    private void change_neighbors (int index, int change)
    {
        check_indexes.add(index);
        for (int i : adjacent_indexes.get(index))
        {
            neighbour_count[i] += change;
            check_indexes.add(i);
        }
    }

    private void get_adjacent_indexes ()
    {
        adjacent_indexes = new ArrayList<int[]>();
        int[] temp = {};
        for (int i = 0; i < size; i++)
        {
            if (i%width == 0)
            {
                if (i/width == 0)
                {
                    temp = new int[] {i+1,i+width,i+width+1};
                }
                else if (i/width == height-1)
                {
                    temp = new int[] {i+1,i-width,i-width+1};
                }
                else
                {
                    temp = new int[] {i+1,i-width,i-width+1,i+width,i+width+1};
                }
            }
            else if (i%width == width-1)
            {
                if (i/width == 0)
                {
                    temp = new int[] {i-1,i+width-1,i+width};
                }
                else if (i/width == height-1)
                {
                    temp = new int[] {i-1,i-width-1,i-width};
                }
                else
                {
                    temp = new int[] {i-width-1,i-width,i-1,i+width,i+width-1};
                }
            }
            else
            {
                if (i/width == 0)
                {
                    temp = new int[] {i-1,i+1,i+width-1,i+width,i+width+1};
                }
                else if (i/width == height-1)
                {
                    temp = new int[] {i-width-1,i-width,i-width+1,i-1,i+1};
                }
                else
                {
                    temp = new int[] {i-width-1,i-width,i-width+1,i-1,i+1,i+width-1,i+width,i+width+1};
                }
            }
            adjacent_indexes.add(temp);
        }
    }
}
