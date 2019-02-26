package Game;

import Utils.MouseHelper;
import Utils.Setting;
import Utils.Vec2;

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

    private static final Color BACKGROUND = Color.BLACK;
    private static final Color TILE = Color.LIGHT_GRAY;
    private static final Color OUTLINE = Color.DARK_GRAY;
    private static final Color GRIDLINE = Color.DARK_GRAY.darker();
    private static final Color MARKING = new Color(0, 0, 255, 50);

    private int width, height, tileSize, gridWidth;
    private Dimension dimension;
    private BufferedImage image;
    private Graphics g;

    public Renderer(Setting setting) {
        this.width = setting.width;
        this.height = setting.height;
        this.tileSize = setting.tileSize;
        this.gridWidth = setting.gridWidth;
        dimension = new Dimension(
                width * (tileSize + gridWidth) - gridWidth,
                height * (tileSize + gridWidth) - gridWidth
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
            int x = i * (tileSize + gridWidth) - gridWidth;
            g.fillRect(x, 0, gridWidth, dimension.height);
        }
        for (int i = 1; i < height; i++) {
            int y = i * (tileSize + gridWidth) - gridWidth;
            g.fillRect(0, y, dimension.width, gridWidth);
        }
    }

    public void drawActiveTiles(Field field) {
        g.setColor(TILE);
        for (int i = 0; i < height; i++) {
            int y = i * (tileSize + gridWidth);
            for (int j = 0; j < height; j++) {
                int x = j * (tileSize + gridWidth);
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
            marking.x * (tileSize + gridWidth),
            marking.y * (tileSize + gridWidth),
            marking.width * (tileSize + gridWidth),
            marking.height * (tileSize + gridWidth)
        );
    }

    public void drawShapeOutline(Shape shape, Vec2 pos) {
        g.setColor(OUTLINE);
        Vec2 middle = shape.getMiddle();
        pos.translate(-middle.x, -middle.y);
        for (Vec2 coord : shape.getPoints()) {
            g.fillRect(
                (tileSize + gridWidth) * (coord.x + pos.x),
                (tileSize + gridWidth) * (coord.y + pos.y),
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
}
