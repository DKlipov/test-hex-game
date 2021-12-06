package org.openjfx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.openjfx.controls.CountrySelector;
import org.openjfx.controls.SpeedPane;
import org.openjfx.map.DataStorage;
import org.openjfx.timeline.TimeThread;
import org.openjfx.timeline.TimelineEventLoop;
import org.openjfx.utils.Clocker;
import org.openjfx.visual.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX App
 */
public class App extends Application {
    static Scene scene;

    private int windowHeight = 640;
    private int windowWidth = 880;
    private int mapRows = 65;
    private int mapColumns = 80;

    private MapDrawer mapDrawer;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        Canvas canvas = new Canvas(windowWidth, windowHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        new Clocker();

        AnimationTimerDecorator animationTimer = new AnimationTimerDecorator();
        var dataStorage = new DataStorage();
        SpeedPane pane = new SpeedPane(LocalDate.now());
        animationTimer.addAnimation(pane);
        var styleProvider = new CachedStyleProvider(mapRows, mapColumns, new CellStyleProviderImpl(dataStorage, mapColumns));
        mapDrawer = new MapDrawer(gc, windowHeight, windowWidth, mapRows, mapColumns, styleProvider);
        mapDrawer.redrawMap();

        var countrySelector = new CountrySelector(dataStorage);

        AnchorPane.setRightAnchor(countrySelector.getNode(), 40.0);
        AnchorPane.setRightAnchor(pane.getNode(), 10.0);
        AnchorPane apane = new AnchorPane(canvas, pane.getNode(), countrySelector.getNode());
        root.getChildren().add(apane);
        scene = new Scene(root);


        var mapEditor = new MapEditor(mapDrawer, scene, countrySelector, dataStorage);

        var thread = new TimeThread(pane, new TimelineEventLoop());
        primaryStage.setScene(scene);
        InteractiveMap interactiveMap = new InteractiveMap(mapDrawer);
        MapMoveController moveController = new MapMoveController(windowWidth, windowHeight, mapDrawer, scene, interactiveMap);
        animationTimer.addAnimation(moveController);
        primaryStage.show();

        animationTimer.start();
    }


    public static void main(String[] args) {
        launch();
    }

}
