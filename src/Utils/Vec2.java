package Utils;

/**
 * Created by Erik Mattfolk on 2019-02-13.
 *
 * A simple 2D Vector implementation
 * Mostly used to specify coordinates for shapes
 * or coordinates on the board.
 */
public class Vec2 {

    // I want this class to behave kind of like
    // a struct, hence the public fields.
    public int x;
    public int y;

    public Vec2() {
        x = 0;
        y = 0;
    }

    public Vec2(int n) {
        x = n;
        y = n;
    }

    public Vec2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void translate(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public Vec2 dividedBy(int n) {
        return new Vec2(x / n, y / n);
    }

    public Vec2 offsetBy(Vec2 offset) {
       return new Vec2(x + offset.x, y + offset.y);
    }

    public Vec2 copy() {
        return new Vec2(x, y);
    }
}
