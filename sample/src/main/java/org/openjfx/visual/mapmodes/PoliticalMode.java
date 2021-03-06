package org.openjfx.visual.mapmodes;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.openjfx.map.Country;
import org.openjfx.map.DataStorage;
import org.openjfx.utils.CellUtils;

import java.awt.*;

@RequiredArgsConstructor
public class PoliticalMode implements CellStyleProvider {
    @Getter
    @Setter
    private Point awaitedCell;

    private final DataStorage dataStorage;

    private final static Color empty = Color.LIGHTCYAN;

    private final int mapColumns;

    @Override
    public CellStyle getStyle(int x, int y) {
        return new CellStyle(getFill(x, y),
                getBorder(x, y),
                getBordersColor(x, y));
    }

    @Override
    public Color getFill(int x, int y) {
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
        if (region == null) {
            return empty;
        }
        if (dataStorage.getCountryData().get(region.getCountry()).getCapital() == region) {
            return Color.RED;
        }
        return region.getCountry().getColor();
    }

    private static final int[] xDiff = new int[]{0, 1, 1, 0, -1, -1};
    private static final int[] y0Diff = new int[]{-1, 0, 1, 1, 1, 0};
    private static final int[] y1Diff = new int[]{-1, -1, 0, 1, 0, -1};

    @Override
    public Color getBorder(int x, int y) {
        var r= dataStorage.getRegion(x, y);
        if (r == null) {
            return empty;
        }
        return r.getCountry().getColor().deriveColor(0, 1.0, 0.7, 1.0);
    }

    @Override
    public Color[] getBordersColor(int x, int y) {
        Color[] result = new Color[6];
        var r = dataStorage.getRegion(x, y);
        if (r == null) {
            return null;
        }
        Country base = r.getCountry();
        Color target = getBorder(x, y);
        int[] yDiff = x % 2 == 0 ? y0Diff : y1Diff;
        boolean isArrayEmpty = true;
        for (int i = 0; i < 6; i++) {
            var m = dataStorage.getRegion(CellUtils.getInRealSpace(x + xDiff[i]), y + yDiff[i]);
            Country near = m == null ? null : m.getCountry();
            if (base != near) {
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
