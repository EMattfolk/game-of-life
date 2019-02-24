import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Erik Mattfolk on 2017-04-27.
 * Refactored on 2019-02-19
 *
 * The Renderer that translates code into actual game.
 * Draws the field, markings and shapes
 * It uses a buffered image to draw on for faster response times
 */
public class Renderer {

    private static final int GRID_WIDTH = 2;
    private static final Color BACKGROUND = Color.BLACK;
    private static final Color TILE = Color.LIGHT_GRAY;
    private static final Color OUTLINE = Color.DARK_GRAY;
    private static final Color GRIDLINE = Color.DARK_GRAY.darker();
    private static final Color MARKING = new Color(0, 0, 255, 50);

    private int width, height, tileSize;
    private Dimension dimension;
    private BufferedImage image;
    private Graphics g;

    public Renderer(int width, int height, int tileSize) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        dimension = new Dimension(
                width * (tileSize + GRID_WIDTH) - GRID_WIDTH,
                height * (tileSize + GRID_WIDTH) - GRID_WIDTH
        );
        image = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_RGB);
        g = image.getGraphics();
    }

    public void clear() {
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, dimension.width, dimension.height);
    }

    public void drawGridlines() {
        g.setColor(GRIDLINE);
        for (int i = 1; i < width; i++) {
            int x = i * (tileSize + GRID_WIDTH) - GRID_WIDTH;
            g.fillRect(x, 0, GRID_WIDTH, dimension.height);
        }
        for (int i = 1; i < height; i++) {
            int y = i * (tileSize + GRID_WIDTH) - GRID_WIDTH;
            g.fillRect(0, y, dimension.width, GRID_WIDTH);
        }
    }

    public void drawActiveTiles(Field field) {
        g.setColor(TILE);
        for (int i = 0; i < height; i++) {
            int y = i * (tileSize + GRID_WIDTH);
            for (int j = 0; j < height; j++) {
                int x = j * (tileSize + GRID_WIDTH);
                if (field.getTile(j, i)) {
                    g.fillRect(x, y, tileSize, tileSize);
                }
            }
        }
    }

    public void drawMarking(MouseHelper helper) {
        if (!helper.isMarking()) return;
        Rectangle marking = helper.getMarking();
        g.setColor(MARKING);
        g.fillRect(
            marking.x * (tileSize + GRID_WIDTH),
            marking.y * (tileSize + GRID_WIDTH),
            marking.width * (tileSize + GRID_WIDTH),
            marking.height * (tileSize + GRID_WIDTH)
        );
    }

    public void drawShapeOutline(Shape shape, Vec2 pos) {
        g.setColor(OUTLINE);
        Vec2 middle = shape.getMiddle();
        pos.translate(-middle.x, -middle.y);
        for (Vec2 coord : shape.getPoints()) {
            g.fillRect(
                (tileSize + GRID_WIDTH) * (coord.x + pos.x),
                (tileSize + GRID_WIDTH) * (coord.y + pos.y),
                tileSize,
                tileSize
            );
        }
    }

    public Dimension getDimension() {
        return dimension;
    }

    public BufferedImage getImage() {
        return image;
    }

    public MouseHelper getMousehelper() {
        return new MouseHelper(tileSize + GRID_WIDTH);
    }
}
