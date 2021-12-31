package org.openjfx.map;

import javafx.scene.paint.Color;
import lombok.Getter;

@Getter
public enum Terrain {
    GRASSLANDS(1, Color.LIGHTGREEN, 1),
    FOREST(2, Color.FORESTGREEN, 2),
    HILLS(3, Color.DARKORANGE, 2),
    HIGHLANDS(4, Color.DARKGRAY, 4),
    MOUNTAINS(5, Color.DARKSLATEGRAY, 30),
    JUNGLE(6, Color.LIMEGREEN, 4),
    MARSH(7, Color.TAN, 3),
    DESERT(8, Color.SANDYBROWN, 30);
    private int id;
    private String name;
    private Color color;
    private int economyAbility;

    Terrain(int id, Color color, int economyAbility) {
        this.id = id;
        this.color = color;
        this.name = this.name().toLowerCase();
        this.economyAbility = economyAbility;
    }
}
