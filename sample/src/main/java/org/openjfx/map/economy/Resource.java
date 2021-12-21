package org.openjfx.map.economy;

import javafx.scene.paint.Color;
import lombok.Getter;

@Getter
public enum Resource {
    IRON(1, Color.LIGHTGRAY),
    NONFERRUS(2, Color.YELLOW),
    OIL(3, Color.BLACK),
    CHEMICALS(4, Color.LIME),
    RUBBER(5, Color.PINK),
    COAL(6, Color.RED),
    CHROMIUM(7, Color.YELLOW),
    ALUMINIUM(8, Color.YELLOW);
    private int id;
    private String name;
    private Color color;

    Resource(int id, Color color) {
        this.id = id;
        this.color = color;
        this.name = this.name().toLowerCase();
    }
}
