package org.openjfx;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.openjfx.controls.ItemSelector;
import org.openjfx.controls.NumberInput;
import org.openjfx.controls.SpeedPane;
import org.openjfx.map.*;
import org.openjfx.map.economy.Resource;
import org.openjfx.timeline.InitialMigration;
import org.openjfx.timeline.TimeThread;
import org.openjfx.timeline.TimelineEventLoop;
import org.openjfx.utils.CellUtils;
import org.openjfx.utils.Clocker;
import org.openjfx.visual.*;
import org.openjfx.visual.editors.MapEditor;
import org.openjfx.visual.mapmodes.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * JavaFX App
 */
public class App extends Application {
    static Scene scene;

    private int windowHeight = 640;
    private int windowWidth = 880;
    public static final int mapRows = 65;
    public static final int mapColumns = 80;

    private MapDrawer mapDrawer;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        Canvas canvas = new Canvas(windowWidth, windowHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        new Clocker();

        InteractiveMap interactiveMap = new InteractiveMap();

        AnimationTimerDecorator animationTimer = new AnimationTimerDecorator();
        var dataStorage = new DataStorage();
        SpeedPane pane = new SpeedPane(LocalDate.now());
        animationTimer.addAnimation(pane);
        interactiveMap.addKeyListener("SPACE", () -> pane.setSpeed(0));

        var defaultProvider = new PoliticalMode(dataStorage, mapColumns);
        var cachedProvider = new CachedStyleProvider(mapRows, mapColumns, defaultProvider);
        Map<String, CellStyleProvider> providers = Map.of(
                "D", defaultProvider,
                "P", new PopulationMode(dataStorage, mapColumns),
                "N", new NationalityMode(dataStorage, mapColumns),
                "T", new TerrainMode(dataStorage, mapColumns),
                "R", new ResourceMode(dataStorage, mapColumns),
                "A", new AreaMode(dataStorage, mapColumns),
                "M", new AdministrativeMode(dataStorage, mapColumns));
        MapModeController mapModeController = new MapModeController(cachedProvider, providers);
        providers.keySet().forEach(k -> interactiveMap.addKeyListener(k, () -> mapModeController.setMode(k)));

        mapDrawer = new MapDrawer(gc, windowHeight, windowWidth, mapRows, mapColumns, cachedProvider);
        mapDrawer.redrawMap();

//        var itemSelector = new ItemSelector<>(Stream.of(Resource.values())
//                .collect(Collectors.toMap(n -> n.getName(), n -> n)));

        var itemSelector = new NumberInput();

        AnchorPane.setRightAnchor(itemSelector.getNode(), 40.0);
        AnchorPane.setTopAnchor(itemSelector.getNode(), 40.0);
        AnchorPane.setRightAnchor(pane.getNode(), 10.0);
        AnchorPane apane = new AnchorPane(canvas, pane.getNode(), itemSelector.getNode());
        root.getChildren().add(apane);
        scene = new Scene(root);


        var mapEditor = new MapEditor<>(interactiveMap, itemSelector, (p, n) -> {
            if (p == null || n == null) {
                return;
            }

            int i = dataStorage.getAreas().size();
            while (dataStorage.getAreas().size() <= n) {
                dataStorage.getAreas().add(new Area(i));
                i++;
            }
            var re = dataStorage.getRegion(p.x, p.y);
            int old = re.getArea().getId();
            re.setArea(dataStorage.getAreas().get(n));
            System.out.println("\n\n\n///");
            dataStorage.getRegions()
                    .forEach(r -> System.out.println(r.getX() + "," + r.getY() + "," + r.getArea().getId()));
            System.out.println(old);
        });

        var eventLoop = new TimelineEventLoop();
        var thread = new TimeThread(pane, eventLoop);
        primaryStage.setScene(scene);

        MapMoveController moveController = new MapMoveController(windowWidth, windowHeight, mapDrawer, scene, interactiveMap);
        animationTimer.addAnimation(moveController);
        primaryStage.show();

        animationTimer.start();
    }


    public static void main(String[] args) {
        launch();
    }

}
