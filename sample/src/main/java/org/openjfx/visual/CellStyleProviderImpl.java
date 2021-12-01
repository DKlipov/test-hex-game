package org.openjfx.visual;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class CellStyleProviderImpl implements CellStyleProvider {
    @Getter
    @Setter
    private Point awaitedCell;


    public Color getColor(int x, int y) {
        if (awaitedCell != null && awaitedCell.x == x && awaitedCell.y == y) {
            return Color.WHITE;
        }
        if (x + y == 5) {
            return Color.YELLOWGREEN;
        }
        if (x + y == 33) {
            return Color.GREENYELLOW;
        }

        if (x + y == 55) {
            return Color.ORANGE;
        }
        if (x - y == 18) {
            return Color.LIGHTBLUE;
        }
        if (x > 100) {
            return Color.BLUE;
        }
        if (y == 100) {
            return Color.GREEN.brighter();
        }
        if (y == 198) {
            return Color.BLUE;
        }
        if (y == 199) {
            return Color.RED;
        }
        if (y == 200) {
            return Color.BLUE;
        }
        return Color.YELLOW;
    }
}
