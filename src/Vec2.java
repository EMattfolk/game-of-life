/**
 * Created by Erik Mattfolk on 2019-02-13.
 *
 * A simple 2D Vector implementation
 */
public class Vec2 {

    public int x;
    public int y;

    public Vec2()
    {
        x = 0;
        y = 0;
    }

    public Vec2(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void translate(int dx, int dy)
    {
        x += dx;
        y += dy;
    }
}
