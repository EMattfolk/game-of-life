import java.util.ArrayList;

/**
 * Created by Erik Mattfolk on 2019-02-13.
 * <p>
 * Stores information about shapes which can be drawn or put on the field
 */
public class Shape {

    public static final Shape EMPTY = new Shape(new ArrayList<>());
    private ArrayList<Vec2> points;
    private Vec2 middle;

    public Shape(ArrayList<Vec2> points) {
        this.points = points;
        middle = new Vec2();
        for (Vec2 p : points) {
            middle.x = p.x > middle.x ? p.x : middle.x;
            middle.y = p.y > middle.y ? p.y : middle.y;
        }
        middle.divide(2);
    }

    // TODO: implement
    public Shape getRotation() {
        return this;
    }

    public ArrayList<Vec2> getPoints() {
        return points;
    }

    public Vec2 getMiddle() {
        return middle;
    }
}
