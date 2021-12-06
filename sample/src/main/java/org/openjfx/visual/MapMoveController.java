package org.openjfx.visual;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import org.openjfx.controls.SpeedPane;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class MapMoveController {

    private Map<String, Boolean> currentlyActiveKeys = new HashMap<>();

    private long millis;
    private MapDrawer mapDrawer;
    private final Object mouseMonitor = new Object();
    private double lastX = 0d;
    private double lastY = 0d;
    private int mouseAwaitTime = 0;
    private CellStyleProvider styleProvider;
    private final SpeedPane speedPane;

    public MapMoveController(MapDrawer mapDrawer, Scene scene, CellStyleProvider styleProvider, SpeedPane speedPane) {
        this.speedPane = speedPane;
        this.mapDrawer = mapDrawer;
        this.millis = Instant.now().toEpochMilli() * 1000;
        this.styleProvider = styleProvider;
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::keyPressed);
        scene.setOnKeyReleased(event -> currentlyActiveKeys.remove(event.getCode().toString()));
        scene.setOnScroll((ScrollEvent event) -> {
            int index = mapDrawer.getSize() / 5;
            if (index <= 0) {
                index = 1;
            }
            if (event.getDeltaY() < 0) {
                mapDrawer.setScale(mapDrawer.getSize() - index);
            } else {
                mapDrawer.setScale(mapDrawer.getSize() + index);
            }

        });
//        scene.setOnMouseMoved(event -> {
//            synchronized (mouseMonitor) {
//                lastX = event.getX();
//                lastY = event.getY();
//                mouseAwaitTime = -1;
//            }
//        });
        var timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                moveMap(now);
            }
        };
        timer.start();
    }

    private void keyPressed(KeyEvent event) {
        String codeString = event.getCode().toString();
        if (!currentlyActiveKeys.containsKey(codeString)) {
            currentlyActiveKeys.put(codeString, true);
        }
        if (codeString.equals("SPACE")) {
            speedPane.setSpeed(0);
        }
    }

    private void checkMouseMoved(int diffMillis) {
        synchronized (mouseMonitor) {
            if (mouseAwaitTime < 0) {
                mouseAwaitTime = 0;
                //styleProvider.setAwaitedCell(null);
                return;
            }
//            else if (styleProvider.getAwaitedCell() != null) {
//                return;
//            }
            mouseAwaitTime += diffMillis;
            if (mouseAwaitTime > 1000) {
                //styleProvider.setAwaitedCell(mapDrawer.getCell(lastX, lastY));
            }
        }
    }

    private void moveMap(long now) {
        speedPane.redraw();
        long diff = now - millis;
        millis = now;
        checkMouseMoved((int) (diff / 1000000));
        int x = mapDrawer.getCursorX();
        int y = mapDrawer.getCursorY();
        int index = (int) (mapDrawer.getSize() * diff / 100000000);
        if (mapDrawer.getSize() < 20) {
            index *= 2;
        }
        if (mapDrawer.getSize() < 10) {
            index *= 3;
        }
        if (mapDrawer.getSize() < 7) {
            index *= 4;
        }
        if (currentlyActiveKeys.containsKey("RIGHT")) {
            x -= index;
        }
        if (currentlyActiveKeys.containsKey("LEFT")) {
            x += index;
        }

        if (currentlyActiveKeys.containsKey("UP")) {
            y -= index;
        }
        if (currentlyActiveKeys.containsKey("DOWN")) {
            y += index;
        }
        mapDrawer.setCursorX(x);
        mapDrawer.setCursorY(y);
        mapDrawer.redrawMap();
    }
}
