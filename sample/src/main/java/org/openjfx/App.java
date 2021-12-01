package org.openjfx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import org.openjfx.visual.CellStyleProviderImpl;
import org.openjfx.visual.MapDrawer;
import org.openjfx.visual.MapMoveController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * JavaFX App
 */
public class App extends Application {
    private static final double ANGLE_CONST = 2 * Math.PI / 6;
    static Scene scene;

    private int windowHeight = 640;
    private int windowWidth = 880;
    private int mapRows = 140;
    private int mapColumns = 50;

    private MapDrawer mapDrawer;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        Canvas canvas = new Canvas(windowWidth, windowHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        var styleProvider=new CellStyleProviderImpl();
        mapDrawer = new MapDrawer(gc, windowHeight, windowWidth, mapRows, mapColumns, styleProvider);
        mapDrawer.redrawMap();


        root.getChildren().add(canvas);
        scene = new Scene(root);


        primaryStage.setScene(scene);
        MapMoveController moveController = new MapMoveController(mapDrawer, scene, styleProvider);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch();
    }

}
