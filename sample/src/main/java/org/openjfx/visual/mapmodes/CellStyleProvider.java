package org.openjfx.visual.mapmodes;

import javafx.scene.paint.Color;

public interface CellStyleProvider {

    CellStyle getStyle(int x, int y);

    Color getFill(int x, int y);

    Color getBorder(int x, int y);

    Color[] getBordersColor(int x, int y);
}
