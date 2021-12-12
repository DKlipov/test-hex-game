package org.openjfx.visual;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MapMoveController implements AnimatedComponent {

    private Map<String, Boolean> currentlyActiveKeys = new HashMap<>();

    private MapDrawer mapDrawer;
    private final Object mouseMonitor = new Object();
    private double lastX = 0d;
    private double lastY = 0d;
    private boolean hoverTriggered = false;
    private int mouseAwaitTime = 0;
    private final InteractiveMap interactiveMap;
    private final int windowWidth;
    private final int windowHeight;

    public MapMoveController(int windowWidth, int windowHeight,
                             MapDrawer mapDrawer, Scene scene, InteractiveMap interactiveMap) {
        this.interactiveMap = interactiveMap;
        this.mapDrawer = mapDrawer;
        this.windowHeight = windowHeight;
        this.windowWidth = windowWidth;
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
        scene.setOnMouseMoved(event -> {
            synchronized (mouseMonitor) {
                lastX = event.getX();
                lastY = event.getY();
                mouseAwaitTime = -1;
                interactiveMap.mouseMoved(mapDrawer.getCell(lastX, lastY), event);
            }
        });
        scene.setOnMouseClicked(event->interactiveMap.cellClicked(mapDrawer.getCell(event.getX(), event.getY())));
        scene.setOnMouseDragged(event -> interactiveMap.mouseMoved(mapDrawer.getCell(event.getX(), event.getY()), event));
    }

    @Override
    public void update(long now, long frameTimeDiff) {
        moveMap(frameTimeDiff);
    }

    private void keyPressed(KeyEvent event) {
        String codeString = event.getCode().toString();
        if (!currentlyActiveKeys.containsKey(codeString)) {
            currentlyActiveKeys.put(codeString, true);
        }
        interactiveMap.keyPressed(codeString);
    }

    private void checkMouseHover(int diffMillis) {
        synchronized (mouseMonitor) {
            if (mouseAwaitTime < 0) {
                mouseAwaitTime = 0;
                interactiveMap.clearCellHover();
                hoverTriggered = false;
                return;
            } else if (hoverTriggered) {
                return;
            }
            mouseAwaitTime += diffMillis;
            if (mouseAwaitTime > 1000) {
                hoverTriggered = true;
                Point cell = mapDrawer.getCell(lastX, lastY);
                interactiveMap.cellDelayedHover(cell);
            }
        }
    }

    private void moveMap(long diff) {
        checkMouseHover((int) (diff / 1000000));
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
            index *= 2;
        }
        if (currentlyActiveKeys.containsKey("RIGHT") || (lastX > windowWidth - 100)) {
            x -= index;
        }
        if (currentlyActiveKeys.containsKey("LEFT") || (lastX < 100)) {
            x += index;
        }

        if (currentlyActiveKeys.containsKey("UP") || (lastY < 100)) {
            y -= index;
        }
        if (currentlyActiveKeys.containsKey("DOWN") || (lastY > windowHeight - 100)) {
            y += index;
        }
        mapDrawer.setCursorX(x);
        mapDrawer.setCursorY(y);
        mapDrawer.redrawMap();
    }
}
