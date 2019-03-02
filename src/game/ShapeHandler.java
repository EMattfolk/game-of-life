package game;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import utils.WindowUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Erik Mattfolk on 2017-05-06.
 * Refactored on 2019-02-13
 *
 * This class handles shapes (wow!)
 * When the class is instantiated it reads from SAVE_PATH file and
 * creates the shapes associated with the data.
 * Shapes are then accessed by the getCurrentShape method and
 * cycling methods. Its purpose is to provide a easy way to save,
 * store and access shapes.
 */
public class ShapeHandler {

    private static final Logger LOGGER = Logger.getLogger(ShapeHandler.class.getName());
    private static final String SAVE_PATH = "shapes.json";
    private static final Type SHAPES_TYPE = new TypeToken<ArrayList<Shape>>() {}.getType();

    private int currentShape;
    private ArrayList<Shape> shapes;
    private Gson gson;

    public ShapeHandler() {
        currentShape = 0;
        shapes = new ArrayList<>();
        gson = new Gson();
        String data = readFile();
        extractData(data);
    }

    public Shape getCurrentShape() {
        if (shapes.isEmpty())
            return Shape.EMPTY;
        else
            return shapes.get(currentShape);
    }

    public void deleteCurrentShape() {
        if (shapes.isEmpty()) return;
        shapes.remove(currentShape);
        save();
        if (shapes.isEmpty()) return;
        currentShape %= shapes.size();
    }

    public void cycleForward() {
        if (shapes.isEmpty()) return;
        currentShape = (currentShape + 1) % shapes.size();
    }

    public void cycleBackward() {
        if (shapes.isEmpty()) return;
        currentShape = Math.floorMod(currentShape - 1, shapes.size());
    }

    public void cycleToEnd() {
        if (shapes.isEmpty()) return;
        currentShape = shapes.size() - 1;
    }

    private String readFile() {
        File file = new File(SAVE_PATH);
        String text = "";

        if (!file.isFile()) {
            return text;
        }

        while (true) {
            try {
                text = new String(Files.readAllBytes(file.toPath()));
                return text;
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
                if (WindowUtils.showErrorDialog(e.toString()) == JOptionPane.NO_OPTION) {
                    return text;
                }
            }
        }
    }

    private void extractData(String data) {
        if (data.isEmpty()) return;
        try {
            shapes = gson.fromJson(data, SHAPES_TYPE);
        } catch (JsonSyntaxException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            WindowUtils.showNotice(e.toString(), "Fix Json formatting and restart.");
        }
    }

    public void addShape(Shape shape) {
        if (!shape.getPoints().isEmpty()) {
            shapes.add(shape);
            save();
        }
    }

    public void save() {
        File file = new File(SAVE_PATH);
        while (!file.isFile()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
                if (WindowUtils.showErrorDialog(e.toString()) == JOptionPane.NO_OPTION) {
                    return;
                }
            }
        }

        String text = gson.toJson(shapes, SHAPES_TYPE);

        while (true) {
            try {
                Files.write(file.toPath(), text.getBytes());
                return;
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.toString(), e);
                if (WindowUtils.showErrorDialog(e.toString()) == JOptionPane.NO_OPTION) {
                    return;
                }
            }
        }
    }
}
