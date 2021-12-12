package org.openjfx.visual;

import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class InteractiveMap {
    private final Map<String, List<Runnable>> keyCallbacks = new HashMap<>();

    private List<Consumer<Point>> clickCallbacks = new ArrayList<>();
    private List<Consumer<Point>> delayedHoverCallbacks = new ArrayList<>();
    private List<BiConsumer<Point, MouseEvent>> mouseCallbacks = new ArrayList<>();

    public void addKeyListener(String key, Runnable listener) {
        keyCallbacks.compute(key, (k, v) -> {
            if (v != null) {
                v.add(listener);
                return v;
            } else {
                var list = new ArrayList<Runnable>();
                list.add(listener);
                return list;
            }
        });
    }

    public void addClickCallback(Consumer<Point> callback) {
        clickCallbacks.add(callback);
    }

    public void addDelayedHoverCallback(Consumer<Point> callback) {
        delayedHoverCallbacks.add(callback);
    }

    public void addMouseCallback(BiConsumer<Point, MouseEvent> callback) {
        mouseCallbacks.add(callback);
    }


    public void keyPressed(String key) {
        keyCallbacks.getOrDefault(key, List.of()).forEach(Runnable::run);
    }

    public void cellClicked(Point cell) {
        clickCallbacks.forEach(c -> c.accept(cell));
    }

    public void cellDelayedHover(Point cell) {
        delayedHoverCallbacks.forEach(c -> c.accept(cell));
    }

    public void mouseMoved(Point cell, MouseEvent event) {
        mouseCallbacks.forEach(c -> c.accept(cell, event));
    }

    public void clearCellHover() {
        delayedHoverCallbacks.forEach(c -> c.accept(null));
    }
}
