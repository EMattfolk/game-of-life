package game;

import utils.Vec2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

    // Game rules
    private static final int LIFE_COUNT = 3;
    private static final int DEATH_LOWER = 2;
    private static final int DEATH_UPPER = 3;

    private int width, height;
    private boolean updating;
    private boolean[][] field;
    private int[][] neighborCount;
    private ArrayList<Vec2>[][] adjacentPoints;
    private List<Vec2> decrease, increase;
    private List<Vec2> toUpdate;

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        field = new boolean[height][width];
        neighborCount = new int[height][width];
        decrease = new ArrayList<>();
        increase = new ArrayList<>();
        toUpdate = new ArrayList<>();
        createAdjacentPoints();
    }

    /**
     * Simulate one generation on the field.
     * Kill and create life according to the rules of game of life.
     */
    public void update() {
        updating = true;

        for (Vec2 coord : toUpdate) {
            int x = coord.x, y = coord.y;
            int neighbors = neighborCount[y][x];
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

    /**
     * @param x x-coordinate
     * @param y y-coordinate
     * @param b boolean determining what to set (x, y) to
     */
    public void setTile(int x, int y, boolean b)
    {
        if (updating || !withinBounds(x, y) || field[y][x] == b) return;
        field[y][x] = b;
        updateNeighbors(x, y, b ? 1 : -1);
    }

    private void flipTile(int x, int y) {
        field[y][x] = !field[y][x];
    }

    /**
     * @param x x-coordinate where the shape will be put
     * @param y y-coordinate where the shape will by put
     * @param shape The shape to be put on the field
     */
    public void putShape(int x, int y, Shape shape) {
        Vec2 offset = shape.getMiddle();
        for (Vec2 p : shape.getPoints()) {
            setTile(x + p.x - offset.x, y + p.y - offset.y, true);
        }
    }

    /**
     * @param bounds Rectangle representing a marking.
     * @return Shape with all points inside the bounds that contain a life.
     */
    public Shape getShape(Rectangle bounds) {
        ArrayList<Vec2> points = new ArrayList<>();
        int startX = Math.max(bounds.x, 0);
        int startY = Math.max(bounds.y, 0);
        int endX = Math.min(bounds.x + bounds.width, width);
        int endY = Math.min(bounds.y + bounds.height, height);
        for (int i = startY; i < endY; i++) {
            for (int j = startX; j < endX; j++) {
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

    /**
     * Set all tiles to False. This clears the field of any life.
     */
    public void reset() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                setTile(j, i, false);
            }
        }
    }

    /**
     * @param x x-coordinate
     * @param y y-coordinate
     * @return True if (x, y) is on the board, False otherwise
     *
     * Ever heard of bounds-checking? This is it.
     */
    private boolean withinBounds(int x, int y) {
        return y >= 0 && y < height && x >= 0 && x < width;
    }

    /**
     * @param x x-coordinate of the tile
     * @param y y-coordinate of the tile
     * @param change 1 or -1 depending on whether the tile at (x, y) was switched on or off
     *
     * Change neighborCount for all neighbors of (x, y) by change.
     */
    private void updateNeighbors(int x, int y, int change) {
        toUpdate.add(new Vec2(x, y));
        for (Vec2 coord : adjacentPoints[y][x]) {
            neighborCount[coord.y][coord.x] += change;
            toUpdate.add(coord);
        }
    }

    /**
     * Create a fast lookup-table for neighboring indexes.
     * Used for updating the table without having to calculate the neighbors every time.
     */
    private void createAdjacentPoints() {

        adjacentPoints = new ArrayList[height][width];
        Vec2[] offsets = new Vec2[] {
                new Vec2(-1, -1), new Vec2(0, -1), new Vec2(1, -1),
                new Vec2(-1, 0),                         new Vec2(1, 0),
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
