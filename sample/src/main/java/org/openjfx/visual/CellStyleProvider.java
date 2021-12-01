package org.openjfx.visual;

import javafx.scene.paint.Color;

public interface CellStyleProvider {
    Color getColor(int x, int y);
}
