import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Erik Mattfolk on 2017-04-27.
 * <p>
 * Keeps track of, and updates cells.
 */
public class Field {

    private int width, height;
    private boolean changed;
    private boolean[][] field;
    private int[][] neighbour_count;
    private ArrayList<Vec2>[][] adjacent_indexes;
    private ArrayList<Vec2> decrease, increase;
    private ArrayList<Vec2> check_indexes;

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        changed = true;
        field = new boolean[height][width];
        neighbour_count = new int[height][width];
        decrease = new ArrayList<>();
        increase = new ArrayList<>();
        check_indexes = new ArrayList<>();
        createAdjacentIndexes();
    }

    public void update() {
        for (Vec2 coord : check_indexes) {
            int x = coord.x, y = coord.y;
            if (!field[y][x] && neighbour_count[y][x] == 3) {
                flip_tile(x, y);
                increase.add(new Vec2(x, y));
                changed = true;
            } else if (field[y][x] && (neighbour_count[y][x] < 2 || neighbour_count[y][x] > 3)) {
                flip_tile(x, y);
                decrease.add(coord);
                changed = true;
            }
        }
        check_indexes.clear();
        for (Vec2 coord : increase) {
            change_neighbors(coord.x, coord.y, 1);
        }
        for (Vec2 coord : decrease) {
            change_neighbors(coord.x, coord.y, -1);
        }
        increase.clear();
        decrease.clear();
    }

    public void set_tile(int x, int y, boolean b)
    {
        if (!in_bounds(x, y) || field[y][x] == b) return;
        field[y][x] = b;
        change_neighbors(x, y, b ? 1 : -1);
        changed = true;
    }

    public void flip_tile(int x, int y) {
        field[y][x] = !field[y][x];
    }

    public void put_shape(int x, int y, ArrayList<Vec2> shape, Vec2 offset) {
        for (Vec2 p : shape) {
            set_tile(x + p.x - offset.x, y + p.y - offset.y, true);
        }
    }

    public Shape getShape(int x1, int y1, int x2, int y2) {
        ArrayList<Vec2> points = new ArrayList<>();
        x1 = clamp(0, width - 1,  x1);
        x2 = clamp(0, width - 1,  x2);
        y1 = clamp(0, height - 1, y1);
        y2 = clamp(0, height - 1, y2);
        int min_x = x1 < x2 ? x1 : x2, max_x = x1 > x2 ? x1 : x2;
        int min_y = y1 < y2 ? y1 : y2, max_y = y1 > y2 ? y1 : y2;
        for (int i = min_y; i <= max_y; i++) {
            for (int j = min_x; j <= max_x; j++) {
                if (field[i][j]) {
                    points.add(new Vec2(j, i));
                }
            }
        }

        return new Shape(points);
    }

    public boolean get_tile(int x, int y) {
        return field[y][x];
    }

    public boolean has_changed() {
        if (changed) {
            changed = false;
            return true;
        }
        return false;
    }

    public void set_changed() {
        changed = true;
    }

    public void reset() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (field[i][j]) {
                    flip_tile(j, i);
                    change_neighbors(j, i, -1);
                }
            }
        }
        check_indexes.clear();
        changed = true;
    }

    private boolean in_bounds(int x, int y) {
        return y >= 0 && y < height && x >= 0 && x < width;
    }

    private int clamp(int min, int max, int n) {
        if (n < min) return min;
        if (n > max) return max;
        return n;
    }

    private void change_neighbors(int x, int y, int change) {
        check_indexes.add(new Vec2(x, y));
        for (Vec2 coord : adjacent_indexes[y][x]) {
            neighbour_count[coord.y][coord.x] += change;
            check_indexes.add(coord);
        }
    }

    private void createAdjacentIndexes() {

        adjacent_indexes = new ArrayList[height][width];
        Vec2[] offsets = new Vec2[] {
                new Vec2(-1, -1), new Vec2(0, -1), new Vec2(1, -1),
                new Vec2(-1, 0),                   new Vec2(1, 0),
                new Vec2(-1, 1),  new Vec2(0, 1),  new Vec2(1, 1)
        };

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                Vec2 coord = new Vec2(j, i);
                ArrayList<Vec2> temp = new ArrayList<>();

                for (Vec2 offset : offsets) {
                    Vec2 neighbor = coord.offsetBy(offset);
                    if (in_bounds(neighbor.x, neighbor.y)) {
                        temp.add(neighbor);
                    }
                }

                adjacent_indexes[i][j] = temp;
            }
        }
    }
}
