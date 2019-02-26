package Game;

import Utils.Vec2;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Erik Mattfolk on 2017-04-27.
 * Refactored on 2019-02-16
 *
 * This class keeps track of the field which you see in-game.
 * The field is stored as a 2D array of booleans indicating whether
 * a cell is alive or not. It also keeps track of data that is
 * necessary for updating the field, such as neighbor count.
 */
public class Field {

    // Game.Game rules
    private static final int LIFE_COUNT = 3;
    private static final int DEATH_LOWER = 2;
    private static final int DEATH_UPPER = 3;

    private int width, height;
    private boolean updating;
    private boolean[][] field;
    private int[][] neighbourCount;
    private ArrayList<Vec2>[][] adjacentPoints;
    private ArrayList<Vec2> decrease, increase;
    private ArrayList<Vec2> toUpdate;

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        field = new boolean[height][width];
        neighbourCount = new int[height][width];
        decrease = new ArrayList<>();
        increase = new ArrayList<>();
        toUpdate = new ArrayList<>();
        createAdjacentPoints();
    }

    public void update() {
        updating = true;

        for (Vec2 coord : toUpdate) {
            int x = coord.x, y = coord.y;
            int neighbors = neighbourCount[y][x];
            if (!field[y][x] && neighbors == LIFE_COUNT) {
                flipTile(x, y);
                increase.add(coord);
            } else if (field[y][x] && (neighbors < DEATH_LOWER || neighbors > DEATH_UPPER)) {
                flipTile(x, y);
                decrease.add(coord);
            }
        }
        toUpdate.clear();
        for (Vec2 coord : increase) {
            updateNeighbors(coord.x, coord.y, 1);
        }
        for (Vec2 coord : decrease) {
            updateNeighbors(coord.x, coord.y, -1);
        }
        increase.clear();
        decrease.clear();

        updating = false;
    }

    public void setTile(int x, int y, boolean b)
    {
        if (updating || !withinBounds(x, y) || field[y][x] == b) return;
        field[y][x] = b;
        updateNeighbors(x, y, b ? 1 : -1);
    }

    private void flipTile(int x, int y) {
        field[y][x] = !field[y][x];
    }

    public void putShape(int x, int y, Shape shape) {
        Vec2 offset = shape.getMiddle();
        for (Vec2 p : shape.getPoints()) {
            setTile(x + p.x - offset.x, y + p.y - offset.y, true);
        }
    }

    public Shape getShape(Rectangle bounds) {
        ArrayList<Vec2> points = new ArrayList<>();
        int endX = Math.min(bounds.x + bounds.width, width);
        int endY = Math.min(bounds.y + bounds.height, height);
        for (int i = bounds.y; i < endY; i++) {
            for (int j = bounds.x; j < endX; j++) {
                if (field[i][j]) {
                    points.add(new Vec2(j, i));
                }
            }
        }

        return new Shape(points);
    }

    public boolean getTile(int x, int y) {
        return field[y][x];
    }

    public void reset() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (field[i][j]) {
                    flipTile(j, i);
                    updateNeighbors(j, i, -1);
                }
            }
        }
        toUpdate.clear();
    }

    private boolean withinBounds(int x, int y) {
        return y >= 0 && y < height && x >= 0 && x < width;
    }

    private void updateNeighbors(int x, int y, int change) {
        toUpdate.add(new Vec2(x, y));
        for (Vec2 coord : adjacentPoints[y][x]) {
            neighbourCount[coord.y][coord.x] += change;
            toUpdate.add(coord);
        }
    }

    private void createAdjacentPoints() {

        adjacentPoints = new ArrayList[height][width];
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
                    if (withinBounds(neighbor.x, neighbor.y)) {
                        temp.add(neighbor);
                    }
                }

                adjacentPoints[i][j] = temp;
            }
        }
    }
}
