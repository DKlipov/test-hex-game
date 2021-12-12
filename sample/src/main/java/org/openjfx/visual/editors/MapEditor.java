package org.openjfx.visual.editors;

import javafx.scene.input.MouseEvent;
import org.openjfx.controls.ItemSelector;
import org.openjfx.visual.InteractiveMap;

import java.awt.*;
import java.util.function.BiConsumer;

public class MapEditor<T> {
    private final ItemSelector<T> itemSelector;
    private final BiConsumer<Point, T> consumer;


    public MapEditor(InteractiveMap interactiveMap, ItemSelector<T> itemSelector, BiConsumer<Point, T> consumer) {
        this.itemSelector = itemSelector;
        this.consumer = consumer;
        interactiveMap.addClickCallback(p -> setRegion(p, null));
        interactiveMap.addMouseCallback(this::setRegion);
    }

    private void setRegion(Point cell, MouseEvent event) {
        if (cell == null || (event != null && !event.isPrimaryButtonDown())) {
            return;
        }
        consumer.accept(cell, itemSelector.getTarget());
    }

}
