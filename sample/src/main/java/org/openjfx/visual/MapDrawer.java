package org.openjfx.visual;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.openjfx.visual.mapmodes.CellStyle;
import org.openjfx.visual.mapmodes.CellStyleProvider;

import java.awt.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MapDrawer {
    private static final double ANGLE_CONST = 2 * Math.PI / 6;
    private static final double SIN30 = Math.sin(Math.PI / 6);
    private static final double COS30 = Math.cos(Math.PI / 6);
    private static final double TWO_HEX_LENGTH_K = 6 * SIN30;
    private static final Double[] SIN = IntStream.range(0, 6)
            .boxed()
            .map(i -> Math.sin(ANGLE_CONST * i))
            .collect(Collectors.toList())
            .toArray(new Double[6]);
    private static final Double[] COS = IntStream.range(0, 6)
            .boxed()
            .map(i -> Math.cos(ANGLE_CONST * i))
            .collect(Collectors.toList())
            .toArray(new Double[6]);

    private final GraphicsContext gc;

    private static final int OFFSET = 3;

    private final int windowHeight;
    private final int windowWidth;
    private final int mapRows;
    private final int mapColumns;
    private final CellStyleProvider cellStyleProvider;
    private final int minSize;
    private final int maxSize;

    public MapDrawer(GraphicsContext gc, int windowHeight, int windowWidth, int mapRows, int mapColumns, CellStyleProvider cellStyleProvider) {
        this.gc = gc;
        this.windowHeight = windowHeight;
        this.windowWidth = windowWidth;
        this.mapRows = mapRows;
        this.mapColumns = mapColumns;
        this.cellStyleProvider = cellStyleProvider;
        this.minSize = Math.max((int) Math.ceil(windowHeight / ((mapRows + 1) * SIN[1]) / 2), 1);
        this.maxSize = (int) Math.floor(windowHeight / SIN[1] / 6);
        this.size = minSize;
    }

    @Getter
    private int size;

    @Getter
    private int cursorX;
    @Getter
    private int cursorY;

    public Point getCell(double x, double y) {
        x -= cursorX;
        y += cursorY;
        double hexSize = TWO_HEX_LENGTH_K * size;
        double cellCouple = x / hexSize;
        double k = (x % hexSize) / hexSize;
        int resultX = ((int) Math.floor(cellCouple)) * 2;
        if (k >= 0.5 || k < 0 && k > -0.5) {
            resultX++;
        }
        if (resultX % 2 == 0) {
            y -= COS30 * size;
        }
        int resultY = (int) Math.floor(y / (COS30 * size * 2));
        return new Point(getCellColumn(resultX), resultY);
    }

    public void setScale(int scale) {
        int oldSize = size;
        size = scale;
        if (size > maxSize) {
            size = maxSize;
        } else if (size < minSize) {
            size = minSize;
        }
        if (oldSize != size) {
            int oldCY = windowHeight / 2 + cursorY;
            int newCY = oldCY * size / oldSize;
            int newY = newCY - windowHeight / 2;
            setCursorY(newY);
            int oldCX = cursorX - windowWidth / 2;
            int newCX = oldCX * size / oldSize;
            int newX = newCX + windowWidth / 2;
            setCursorX(newX);
        }
    }

    public void setCursorX(int x) {
        cursorX = x;
        int mapWidth = (int) (size * (1 + COS[1])) * mapColumns;
        while (cursorX < mapWidth) {
            cursorX += mapWidth;
        }
        while (cursorX > mapWidth) {
            cursorX -= mapWidth;
        }
    }

    public void setCursorY(int y) {
        cursorY = y;
        int maxY = (int) Math.floor(((mapRows + 1) * SIN[1] * size * 2) - windowHeight);
        if (cursorY < 0 && cursorY < -(SIN[1] * size)) {
            cursorY = (int) -(SIN[1] * size);
        } else if (cursorY > maxY) {
            cursorY = maxY;
        }
    }

    public void redrawMap() {
        drawGrid(gc, cursorX, cursorY, size);
    }

    private int getCellColumn(int rawRowNumber) {
        int result = rawRowNumber;
        while (result < 0) {
            result += mapColumns;
        }
        while (result >= mapColumns) {
            result -= mapColumns;
        }
        return result;
    }

    private void drawGrid(GraphicsContext gc, double startX, double startY, int r) {
        double x = startX;
        double y;
        boolean defaultBorders = false;
        if (size > 6) {
            defaultBorders = true;
        }

        int columnNumber = 0;
        if (x > -size - size) {
            columnNumber = -(int) Math.ceil(startX / (r * (1 + COS[1])));
            x = startX + columnNumber * (r * (1 + COS[1]));
            columnNumber = getCellColumn(columnNumber);
        }

        while (true) {
            if (columnNumber % 2 == 0) {
                y = -startY - (r * SIN[1]);
            } else {
                y = -startY - (2 * r * SIN[1]);
            }
            gc.setFill(Color.WHITE);
            drawHex(gc, x, y, defaultBorders, null);
            y = y + 2 * r * SIN[1];
            for (int j = 0; j < mapRows; j++) {
                CellStyle style = cellStyleProvider.getStyle(columnNumber, j);
                if (!defaultBorders && style.getBorders() != null) {
                    gc.setFill(style.getBorder());
                    drawHex(gc, x, y, defaultBorders, null);
                } else {
                    gc.setFill(style.getFill());
                    drawHex(gc, x, y, defaultBorders, style.getBorders());
                }
                y = y + 2 * r * SIN[1];
            }
            gc.setFill(Color.WHITE);
            drawHex(gc, x, y, defaultBorders, null);
            x += r * (1 + COS[1]);
            if (x > windowWidth + size) {
                return;
            }
            columnNumber++;
            if (columnNumber >= mapColumns) {
                columnNumber -= mapColumns;
            }
        }
    }

    private final static int[] xDiff = new int[]{0, -2, -2, 0, 2, 2};
    private final static int[] yDiff = new int[]{2, 2, -2, -2, -2, 2};

    private void drawHex(GraphicsContext gc, double startX, double startY,
                         boolean defaultBorders, Color[] borders) {
        if (startY < -size - size || startY > windowHeight
                || startX < -size - size || startX > windowWidth + size) {
            return;
        }
        double x = startX;
        double y = startY;
        double[] xArray = new double[7];
        double[] yArray = new double[7];
        for (var i = 0; i < 6; i++) {
            double newX = x + size * COS[i];
            double newY = y + size * SIN[i];
            xArray[i] = x;
            yArray[i] = y;
            x = newX;
            y = newY;
        }
        xArray[6] = x;
        yArray[6] = y;
        gc.fillPolygon(xArray, yArray, 6);
        if (borders != null) {
            gc.setLineWidth(4);
            for (var i = 0; i < 6; i++) {
                var color = borders[i];
                if (color != null) {
                    gc.setStroke(color);
                    gc.strokeLine(xArray[i] + xDiff[i], yArray[i] + yDiff[i], xArray[i + 1] + xDiff[i], yArray[i + 1] + yDiff[i]);
                }
            }
        }
        if (defaultBorders) {
            gc.setLineWidth(0.5);
            gc.setStroke(Color.BLACK);
            gc.strokePolygon(xArray, yArray, 6);
        }
    }

}
