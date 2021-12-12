package org.openjfx.utils;

import lombok.experimental.UtilityClass;
import org.openjfx.App;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CellUtils {
    private static final int[] xDiff = new int[]{0, 1, 1, 0, -1, -1};
    private static final int[] y0Diff = new int[]{-1, 0, 1, 1, 1, 0};
    private static final int[] y1Diff = new int[]{-1, -1, 0, 1, 0, -1};

    public List<Point> getNeigbors(Point cell) {
        int[] yDiff = cell.x % 2 == 0 ? y0Diff : y1Diff;
        List<Point> result = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            int x=cell.x + xDiff[i];
            int effectiveX;
            if (x >= App.mapColumns) {
                effectiveX = x - App.mapColumns;
            } else if (x < 0) {
                effectiveX = x + App.mapColumns;
            } else {
                effectiveX = x;
            }
            result.add(new Point(effectiveX, cell.y + yDiff[i]));
        }

        return result;
    }

}
