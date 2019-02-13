import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

/**
 * Created by Erik Mattfolk on 2017-04-27.
 *
 * Renders the game on a BufferedImage
 */
public class Renderer
{
    private int width, height, image_width, image_height, tile_size, border_color, shape_color, marking_color_blank, marking_color_tile;
    private Field field;
    private BufferedImage image;
    private int[] pixels;

    public Renderer (int width, int height, int tile_size, Field field)
    {
        this.width = width;
        this.height = height;
        this.tile_size = tile_size;
        this.field = field;
        border_color = Color.GRAY.getRGB();
        shape_color = Color.LIGHT_GRAY.getRGB();
        set_marking_color();
        image = new BufferedImage(width*(tile_size)+1,height*(tile_size)+1, BufferedImage.TYPE_INT_RGB);
        image_width = image.getWidth();
        image_height = image.getHeight();
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
        paint_border();
        render();
    }

    public void render ()
    {
        int color;
        for (int y = 0; y < image_height-1; y += tile_size)
        {
            for (int x = 0; x < image_width-1; x += tile_size)
            {
                color = field.get_tile(x/tile_size,y/tile_size) ? 0 : Integer.MAX_VALUE;
                if (pixels[(y+1)*image_width + x+1] == color) continue;

                for (int i = 1; i < tile_size; i++)
                {
                    for (int j = 1; j < tile_size; j++)
                    {
                        pixels[(y+i)*image_width + x+j] = color;
                    }
                }
            }
        }
    }

    public void draw_shape_outline (int mouse_x, int mouse_y, ArrayList<Vec2> shape, Vec2 offset)
    {
        int x, y;
        for (Vec2 p : shape)
        {
            x = p.x + mouse_x - offset.x;
            y = p.y + mouse_y - offset.y;
            if (in_bounds(x, y) && !field.get_tile(x, y))
            {
                x *= tile_size;
                y *= tile_size;
                for (int i = 1; i < tile_size; i++)
                {
                    for (int j = 1; j < tile_size; j++)
                    {
                        pixels[(y+i)*image_width + x+j] = shape_color;
                    }
                }
            }
        }
    }

    public void draw_marking (int x1, int y1, int x2, int y2)
    {
        int min_x, max_x, min_y, max_y, x, y, index;
        x1 = put_within_bounds(x1,width);
        x2 = put_within_bounds(x2,width);
        y1 = put_within_bounds(y1,height);
        y2 = put_within_bounds(y2,height);
        min_x = x1 < x2 ? x1 : x2;
        min_y = y1 < y2 ? y1 : y2;
        max_x = x1 > x2 ? x1 : x2;
        max_y = y1 > y2 ? y1 : y2;
        for (int i = min_y; i <= max_y; i++)
        {
            y = i*tile_size;
            for (int j = min_x; j <= max_x; j++)
            {
                x = j*tile_size;
                for (int k = 1; k < tile_size; k++)
                {
                    for (int l = 1; l < tile_size; l++)
                    {
                        index = (y+k)*image_width + x+l;
                        if (pixels[index] == 0)
                        {
                            pixels[index] = marking_color_tile;
                        }
                        else
                        {
                            pixels[index] = marking_color_blank;
                        }
                    }
                }
            }
        }
    }

    private void paint_border ()
    {
        for (int i = 0; i < image_width; i++)
        {
            pixels[image_width*(image_height-1) + i] = border_color;
        }
        for (int i = 0; i < image_height; i++)
        {
            pixels[image_width*i + image_width-1] = border_color;
        }
        for (int y = 0; y < image_height-1; y += tile_size)
        {
            for (int x = 0; x < image_width - 1; x += tile_size)
            {
                for (int i = 0; i < tile_size; i++)
                {
                    pixels[(y+i)*image_width + x] = border_color;
                    pixels[y*image_width + x+i] = border_color;
                }
            }
        }
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

    private void set_marking_color ()
    {
        marking_color_blank = -8605953;
        marking_color_tile = -3181;
    }

    public BufferedImage get_image ()
    {
        return image;
    }
}
