package org.openjfx.visual;

import javafx.scene.paint.Color;

public interface CellStyleProvider {

    CellStyle getStyle(int x, int y);

    Color getColor(int x, int y);

    Color[] getBordersColor(int x, int y);
}
