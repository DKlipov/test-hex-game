package org.openjfx.visual;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.openjfx.map.DataStorage;

import java.awt.*;

@RequiredArgsConstructor
public class CellStyleProviderImpl implements CellStyleProvider {
    @Getter
    @Setter
    private Point awaitedCell;

    private final DataStorage dataStorage;

    private final static Color empty = Color.LIGHTCYAN;

    private final int mapColumns;

    @Override
    public CellStyle getStyle(int x, int y) {
        return new CellStyle(getColor(x, y), getBordersColor(x, y));
    }

    public Color getColor(int x, int y) {
        int effectiveX;
        if (x >= mapColumns) {
            effectiveX = x - mapColumns;
        } else if (x < 0) {
            effectiveX = x + mapColumns;
        } else {
            effectiveX = x;
        }
        var region = dataStorage.getRegions().stream()
                .filter(r -> r.getX() == effectiveX && r.getY() == y).findAny().orElse(null);
        if (region != null) {
            return region.getCountry().getColor();
        }
        return empty;
    }

    private static final int[] xDiff = new int[]{0, 1, 1, 0, -1, -1};
    private static final int[] y0Diff = new int[]{-1, 0, 1, 1, 1, 0};
    private static final int[] y1Diff = new int[]{-1, -1, 0, 1, 0, -1};


    @Override
    public Color[] getBordersColor(int x, int y) {
        Color[] result = new Color[6];
        Color base = getColor(x, y);
        Color target = getColor(x, y).deriveColor(0, 1.0, 0.7, 1.0);
        int[] yDiff = x % 2 == 0 ? y0Diff : y1Diff;
        boolean isArrayEmpty = true;
        for (int i = 0; i < 6; i++) {
            Color near = getColor(x + xDiff[i], y + yDiff[i]);
            if (!base.equals(empty) && !base.equals(near)) {
                result[i] = target;
                isArrayEmpty = false;
            } else {
                result[i] = null;
            }
        }
        if (isArrayEmpty) {
            return null;
        } else {
            return result;
        }
    }
}
