package org.openjfx.map;

import javafx.scene.paint.Color;
import lombok.Getter;

@Getter
public enum Terrain {
    GRASSLANDS(1, Color.LIGHTGREEN),
    FOREST(2, Color.FORESTGREEN),
    HILLS(3, Color.DARKORANGE),
    HIGHLANDS(4, Color.DARKGRAY),
    MOUNTAINS(5, Color.DARKSLATEGRAY),
    JUNGLE(6, Color.LIMEGREEN),
    MARSH(7, Color.TAN),
    DESERT(8, Color.SANDYBROWN);
    private int id;
    private String name;
    private Color color;

    Terrain(int id, Color color) {
        this.id = id;
        this.color = color;
        this.name = this.name().toLowerCase();
    }
}
