import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pwnxl on 2017-05-06.
 *
 * Stores, returns, saves and rotates shapes to be put on the field
 */
public class ShapeHandler {

    private HashMap<Character,ArrayList<Point>> shapes;
    private HashMap<Character,Point> shape_offsets;

    public ShapeHandler ()
    {
        shapes = new HashMap<Character,ArrayList<Point>>();
        shape_offsets = new HashMap<Character,Point>();
        read_file();
    }

    public ArrayList<Point> get_shape (char shape)
    {
        return shapes.get(shape);
    }

    public Point get_offset (char shape)
    {
        return shape_offsets.get(shape);
    }

    public boolean has_shape (char shape)
    {
        return shapes.containsKey(shape);
    }

    private void read_file ()
    {
        File file = new File("shapes.txt");
        String text = "";

        if (!file.isFile())
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                System.out.println("Failed to create file");
            }
        }

        try
        {
            text = new String(Files.readAllBytes(file.toPath()));
        }
        catch (IOException e)
        {
            System.out.println("Failed to find file");
        }
        extract_data(text);
    }

    private void extract_data (String data)
    {
        data = data.replaceAll(System.lineSeparator(),"\n");
        ArrayList<Point> shape = new ArrayList<Point>();
        char current_char ,button = 0;
        int height = 0, width = 0, index = 0;
        while (data.length() > index)
        {
            current_char = data.charAt(index);
            if (current_char == '-')
            {
                index++;
            }
            else if (current_char == 'O')
            {
                shape.add(new Point(index,height));
                index++;
            }
            else if (current_char == '\n')
            {
                data = data.substring(index+1);
                index = 0;
                height++;
            }
            else if (Game.valid_buttons.contains(current_char))
            {
                if (!shape.isEmpty())
                {
                    shapes.put(button,shape);
                    shape = new ArrayList<Point>();
                }
                button = current_char;
                data = data.substring(data.indexOf(' ')+1);
                width = Integer.parseInt(data.substring(0,data.indexOf(' ')))-1;
                data = data.substring(data.indexOf(' ')+1);
                height = 1-Integer.parseInt(data.substring(0,data.indexOf('\n')));
                data = data.substring(data.indexOf('\n')+1);
                shape_offsets.put(current_char, new Point(width/2,height/2));
                index = 0;
            }
            else if (current_char == '/')
            {
                if (data.contains("\n"))
                {
                    data = data.substring(data.indexOf('\n')+1);
                }
                else data = "";
            }
        }
        if (!shape.isEmpty())
        {
            shapes.put(button,shape);
            return;
        }
    }

    public void add_shape (char key, ArrayList<Point> shape)
    {
        if (!shape.isEmpty())
        {
            int max_x = 0, max_y = 0;
            for (Point p : shape)
            {
                max_x = p.x > max_x ? p.x : max_x;
                max_y = p.y < max_y ? p.y : max_y;
            }
            shape_offsets.put(key, new Point(max_x/2,max_y/2));
            shapes.put(key, shape);
            save();
        }
    }

    public ArrayList<Point> rotate_shape (ArrayList<Point> shape)
    {
        ArrayList<Point> rotation = new ArrayList<Point>();
        int max_x = Integer.MIN_VALUE, max_y = Integer.MIN_VALUE;
        for (Point p : shape)
        {
            max_x = p.x > max_x ? p.x : max_x;
            max_y = p.y > max_y ? p.y : max_y;
        }
        for (Point p : shape)
        {
            rotation.add(new Point(max_y - p.y,p.x - max_x));
        }
        return rotation;
    }

    public void save ()
    {
        File file = new File("shapes.txt");
        if (!file.isFile())
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                System.out.println("Failed to create file");
            }
        }
        String text = "", br = System.lineSeparator();
        text += "/comments are lines starting with '/'" + br;
        text += "/shapes are structured like this: (button) (width) (height) and then the shape below" + br;
        text += "/use 'O' (capital 'o') for black tile and '-' for white tile" + br + br;
        ArrayList<Point> shape;
        char [] grid;
        int max_x, max_y, br_length = br.length();
        for (char key : shapes.keySet())
        {
            shape = shapes.get(key);
            max_x = 0;
            max_y = 0;
            for (Point p : shape)
            {
                max_x = p.x > max_x ? p.x : max_x;
                max_y = p.y < max_y ? p.y : max_y;
            }
            max_y = -max_y;
            text += key + " " + (max_x + 1) + " " + (max_y + 1) + br;

            grid = new char[(max_x + 1) * (max_y + 1)];
            for (int i = 0; i <= max_y; i++)
            {
                for (int j = 0; j <= max_x; j++)
                {
                    grid[i*(max_x + 1) + j] = '-';
                }
            }
            for (Point p : shape)
            {
                grid[(max_y + p.y)*(max_x + 1) + p.x] = 'O';
            }
            for (int i = 0; i <= max_y; i++)
            {
                for (int j = 0; j <= max_x; j++)
                {
                    text += grid[i*(max_x + 1) + j];
                }
                text += br;
            }
            text += br;
        }
        try
        {
            Files.write(file.toPath(),text.getBytes());
        }
        catch (IOException e)
        {
            System.out.println("Failed to save");
        }
    }
}
