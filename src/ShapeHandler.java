import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Created by Erik Mattfolk on 2017-05-06.
 * Refactored on 2019-02-13
 * <p>
 * Loades, saves, returns and cycles Shapes to be put on the field
 */
public class ShapeHandler {

    public static final String SAVE_PATH = "shapes.json";
    private static final Type shapesType = new TypeToken<ArrayList<Shape>>() {
    }.getType();
    private int currentShape;
    private ArrayList<Shape> shapes;
    private Gson gson;

    public ShapeHandler() {
        currentShape = 0;
        shapes = new ArrayList();
        gson = new Gson();
        String data = read_file();
        extract_data(data);
    }

    public Shape getCurrentShape() {
        if (shapes.size() == 0)
            return Shape.EMPTY;
        else
            return shapes.get(currentShape);
    }

    public void deleteCurrentShape() {
        if (shapes.size() == 0) return;
        shapes.remove(currentShape);
        if (shapes.size() == 0) return;
        currentShape %= shapes.size();
    }

    public Vec2 get_offset() {
        return getCurrentShape().getMiddle();
    }

    public void cycleForward() {
        if (shapes.size() == 0) return;
        currentShape = (currentShape + 1) % shapes.size();
    }

    public void cycleBackward() {
        if (shapes.size() == 0) return;
        currentShape = Math.floorMod(currentShape - 1, shapes.size());
    }

    public void cycleToEnd() {
        if (shapes.size() == 0) return;
        currentShape = shapes.size() - 1;
    }

    private String read_file() {
        File file = new File(SAVE_PATH);
        String text = "";

        if (!file.isFile()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Failed to create file");
            }
        }

        try {
            text = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            System.out.println("Failed to find file");
        }

        return text;
    }

    private void extract_data(String data) {
        if (data.equals("")) return;
        shapes = gson.fromJson(data, shapesType);
    }

    public void add_shape(Shape shape) {
        if (!shape.getPoints().isEmpty()) {
            shapes.add(shape);
            save();
        }
    }

    public void save() {
        File file = new File(SAVE_PATH);
        if (!file.isFile()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Failed to create file");
            }
        }

        String text = gson.toJson(shapes, shapesType);

        try {
            Files.write(file.toPath(), text.getBytes());
        } catch (IOException e) {
            System.out.println("Failed to save");
        }
    }
}
