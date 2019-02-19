import java.util.ArrayList;

/**
 * Created by Erik Mattfolk on 2019-02-13.
 *
 * Stores information about shapes which can be drawn or put on the field
 * The constructor takes an arbitrary collection of Points in the 2D plane and translates them
 * so that the origin is (0, 0)
 */
public class Shape {

    public static final Shape EMPTY = new Shape(new ArrayList<>());
    private ArrayList<Vec2> points;
    private Vec2 middle;

    public Shape(ArrayList<Vec2> points) {
        this.points = points;
        Vec2 min = new Vec2(Integer.MAX_VALUE), max = new Vec2(Integer.MIN_VALUE);
        for (Vec2 p : points) {
            min.x = p.x < min.x ? p.x : min.x;
            min.y = p.y < min.y ? p.y : min.y;
            max.x = p.x > max.x ? p.x : max.x;
            max.y = p.y > max.y ? p.y : max.y;
        }
        max.translate(-min.x, -min.y);
        for (int i = 0; i < points.size(); i++) {
            points.get(i).translate(-min.x, -min.y);
        }
        middle = max.dividedBy(2);
    }

    public Shape getRotation() {
        ArrayList<Vec2> rotated = new ArrayList<>();
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (Vec2 p : points) {
            maxX = p.x > maxX ? p.x : maxX;
            maxY = p.y > maxY ? p.y : maxY;
        }
        for (Vec2 p : points) {
            rotated.add(new Vec2(maxY - p.y, p.x - maxX));
        }

        return new Shape(rotated);
    }

    public ArrayList<Vec2> getPoints() {
        return points;
    }

    public Vec2 getMiddle() {
        return middle;
    }
}
